function createMonitor({ serviceName, port, redis }) {
  const logs = [];
  const maxLogs = 500;

  function now() {
    return new Date().toISOString();
  }

  function addLog(entry) {
    const row = {
      id: `${Date.now()}-${Math.random().toString(16).slice(2)}`,
      service: serviceName,
      timestamp: now(),
      ...entry,
    };
    logs.unshift(row);
    if (logs.length > maxLogs) logs.length = maxLogs;
    return row;
  }

  function log(level, message, details = {}) {
    const row = addLog({
      type: 'app',
      level,
      message,
      flowId: details.flowId,
      stage: details.stage,
      details,
    });
    console.log(`[${serviceName}] ${level.toUpperCase()} ${message}`, details);
    return row;
  }

  function middleware(req, res, next) {
    if (
      req.path === '/logs' ||
      req.path === '/logs/clear' ||
      req.path === '/health' ||
      (req.method === 'GET' && req.path === '/products') ||
      req.query.silent === '1' ||
      req.get('x-silent-log') === 'true'
    ) {
      next();
      return;
    }

    const startedAt = Date.now();
    const requestBody = req.body && Object.keys(req.body).length ? req.body : null;
    let responseBody;

    const originalJson = res.json.bind(res);
    res.json = (body) => {
      responseBody = body;
      return originalJson(body);
    };

    res.on('finish', () => {
      addLog({
        type: 'http',
        level: res.statusCode >= 500 ? 'error' : res.statusCode >= 400 ? 'warn' : 'info',
        method: req.method,
        path: req.originalUrl,
        flowId: req.get('x-flow-id') || req.body?.flowId,
        status: res.statusCode,
        durationMs: Date.now() - startedAt,
        request: {
          params: req.params,
          query: req.query,
          body: requestBody,
        },
        response: responseBody,
      });
    });

    next();
  }

  function routes(app) {
    app.get('/health', async (req, res) => {
      let redisStatus = 'not-configured';
      if (redis) {
        try {
          await redis.ping();
          redisStatus = 'ok';
        } catch (error) {
          redisStatus = error.message;
        }
      }

      res.json({
        service: serviceName,
        port,
        status: redisStatus === 'ok' || redisStatus === 'not-configured' ? 'ok' : 'degraded',
        redis: redisStatus,
        logCount: logs.length,
        uptimeSec: Math.round(process.uptime()),
        timestamp: now(),
      });
    });

    app.get('/logs', (req, res) => {
      const limit = Math.min(parseInt(req.query.limit || '100', 10), maxLogs);
      res.json(logs.slice(0, limit));
    });

    app.delete('/logs/clear', (req, res) => {
      logs.length = 0;
      res.json({ cleared: true, service: serviceName });
    });
  }

  return { middleware, routes, log };
}

module.exports = { createMonitor };
