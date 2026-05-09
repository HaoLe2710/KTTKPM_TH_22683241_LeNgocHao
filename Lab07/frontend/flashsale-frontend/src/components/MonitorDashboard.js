import React, { useEffect, useMemo, useState } from 'react';
import axios from 'axios';

const SERVICES = [
  { key: 'product', name: 'Product PU', baseUrl: 'http://localhost:8081', samplePath: '/products' },
  { key: 'cart', name: 'Cart PU', baseUrl: 'http://localhost:8082', samplePath: '/cart/1' },
  { key: 'order', name: 'Order PU', baseUrl: 'http://localhost:8083', samplePath: '/checkout' },
  { key: 'inventory', name: 'Inventory PU', baseUrl: 'http://localhost:8084', samplePath: '/stock/1' },
];

const silentHeaders = { 'Cache-Control': 'no-cache', 'x-silent-log': 'true' };

function shortStage(stage) {
  if (!stage) return 'event';
  return stage.replace(/^\d_/, '').replaceAll('_', ' ').toLowerCase();
}

function MonitorDashboard({ onDataChanged }) {
  const [health, setHealth] = useState({});
  const [logs, setLogs] = useState([]);
  const [selectedLog, setSelectedLog] = useState(null);
  const [requestCount, setRequestCount] = useState(50);
  const [concurrency, setConcurrency] = useState(10);
  const [isRunning, setIsRunning] = useState(false);
  const [loadResult, setLoadResult] = useState(null);
  const [products, setProducts] = useState([]);
  const [logType, setLogType] = useState('app');
  const [serviceFilter, setServiceFilter] = useState('all');
  const [levelFilter, setLevelFilter] = useState('all');

  const filteredLogs = useMemo(() => logs.filter(log => {
    const matchesType = logType === 'all' || log.type === logType;
    const matchesService = serviceFilter === 'all' || log.service === serviceFilter;
    const matchesLevel = levelFilter === 'all' || log.level === levelFilter;
    return matchesType && matchesService && matchesLevel;
  }), [logs, logType, serviceFilter, levelFilter]);

  const totalErrors = useMemo(
    () => logs.filter(log => log.level === 'error' || log.level === 'warn' || log.status >= 400).length,
    [logs]
  );

  const latestFlow = useMemo(() => {
    const flowLogs = logs.filter(log => log.flowId);
    if (!flowLogs.length) return [];
    const id = flowLogs[0].flowId;
    return flowLogs.filter(log => log.flowId === id).slice().reverse();
  }, [logs]);

  const fetchMonitorData = async () => {
    const healthResponses = await Promise.allSettled(
      SERVICES.map(service => axios.get(`${service.baseUrl}/health`, { timeout: 1500, headers: silentHeaders }))
    );
    const logResponses = await Promise.allSettled(
      SERVICES.map(service => axios.get(`${service.baseUrl}/logs?limit=120`, { timeout: 1500, headers: silentHeaders }))
    );
    const productResponse = await Promise.allSettled([
      axios.get('http://localhost:8081/products?silent=1', { timeout: 1500, headers: silentHeaders }),
    ]);

    const nextHealth = {};
    healthResponses.forEach((result, index) => {
      const service = SERVICES[index];
      nextHealth[service.key] = result.status === 'fulfilled'
        ? result.value.data
        : { service: service.key, status: 'down', redis: 'unreachable', error: result.reason.message };
    });

    const nextLogs = logResponses.flatMap(result => (
      result.status === 'fulfilled' ? result.value.data : []
    )).sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));

    setHealth(nextHealth);
    setLogs(nextLogs.slice(0, 240));
    if (productResponse[0].status === 'fulfilled') {
      setProducts(productResponse[0].value.data);
    }
  };

  useEffect(() => {
    fetchMonitorData();
    const timer = setInterval(fetchMonitorData, 1500);
    return () => clearInterval(timer);
  }, []);

  const clearLogs = async () => {
    await Promise.allSettled(SERVICES.map(service => axios.delete(`${service.baseUrl}/logs/clear`)));
    setSelectedLog(null);
    await fetchMonitorData();
  };

  const resetDemoData = async () => {
    await axios.post('http://localhost:8081/products/reset');
    setLoadResult(null);
    onDataChanged?.();
    await fetchMonitorData();
  };

  const runLoadTest = async () => {
    setIsRunning(true);
    setLoadResult(null);

    const runId = `run-${Date.now()}`;
    const total = Math.max(1, Number(requestCount));
    const workers = Math.min(Math.max(1, Number(concurrency)), total);
    const startedAt = performance.now();
    const results = [];
    let cursor = 0;

    const runScenario = async (index) => {
      const flowId = `${runId}-${index + 1}`;
      const userId = 1000 + index;
      const productId = (index % 3) + 1;
      const quantity = 1;
      const headers = { 'x-flow-id': flowId };
      const requestStartedAt = performance.now();

      try {
        const addCart = await axios.post('http://localhost:8082/cart/add', { userId, productId, quantity, flowId }, { headers });
        const stock = await axios.get(`http://localhost:8084/stock/${productId}`, { headers });
        const checkout = await axios.post('http://localhost:8083/checkout', { userId, flowId }, { headers });

        return {
          ok: true,
          flowId,
          ms: Math.round(performance.now() - requestStartedAt),
          userId,
          productId,
          addCart: addCart.status,
          stock: stock.data,
          checkout: checkout.data,
        };
      } catch (error) {
        return {
          ok: false,
          flowId,
          ms: Math.round(performance.now() - requestStartedAt),
          userId,
          productId,
          status: error.response?.status || 0,
          message: error.response?.data?.msg || error.message,
          issues: error.response?.data?.issues,
        };
      }
    };

    const worker = async () => {
      while (cursor < total) {
        const index = cursor;
        cursor += 1;
        results.push(await runScenario(index));
      }
    };

    await Promise.all(Array.from({ length: workers }, worker));

    const durationMs = Math.round(performance.now() - startedAt);
    const success = results.filter(item => item.ok).length;
    const failed = results.length - success;
    const sortedDurations = results.map(item => item.ms).sort((a, b) => a - b);
    const avgMs = Math.round(sortedDurations.reduce((sum, ms) => sum + ms, 0) / sortedDurations.length);
    const p95Ms = sortedDurations[Math.max(0, Math.ceil(sortedDurations.length * 0.95) - 1)];

    setLoadResult({
      total,
      success,
      failed,
      durationMs,
      avgMs,
      p95Ms,
      rps: Number((total / (durationMs / 1000)).toFixed(2)),
      samples: results.slice(-8).reverse(),
    });

    setIsRunning(false);
    onDataChanged?.();
    await fetchMonitorData();
  };

  return (
    <section className="monitor">
      <div className="monitor__header">
        <div>
          <h2>PU Monitor</h2>
          <p>Health, stock, business timeline, and optional raw HTTP logs.</p>
        </div>
        <div className="monitor__actions">
          <button className="button button--secondary" onClick={resetDemoData}>Reset data</button>
          <button className="button button--secondary" onClick={clearLogs}>Clear logs</button>
          <button className="button" onClick={runLoadTest} disabled={isRunning}>
            {isRunning ? 'Running...' : 'Run load test'}
          </button>
        </div>
      </div>

      <div className="service-grid">
        {SERVICES.map(service => {
          const item = health[service.key];
          const isOk = item?.status === 'ok';
          return (
            <article className="service-card" key={service.key}>
              <div className={`status-dot ${isOk ? 'status-dot--ok' : 'status-dot--down'}`} />
              <div>
                <h3>{service.name}</h3>
                <p>{service.baseUrl}{service.samplePath}</p>
              </div>
              <strong>{item?.status || 'checking'}</strong>
              <span>Redis: {item?.redis || '-'}</span>
              <span>Logs: {item?.logCount ?? '-'}</span>
            </article>
          );
        })}
      </div>

      <div className="load-panel">
        <label>
          Requests
          <input type="number" min="1" max="100000" value={requestCount} onChange={event => setRequestCount(event.target.value)} />
        </label>
        <label>
          Concurrency
          <input type="number" min="1" max="100" value={concurrency} onChange={event => setConcurrency(event.target.value)} />
        </label>
        <div className="metric">
          <span>Visible logs</span>
          <strong>{filteredLogs.length}</strong>
        </div>
        <div className="metric">
          <span>Warn/Error</span>
          <strong>{totalErrors}</strong>
        </div>
      </div>

      <div className="stock-panel">
        <div>
          <h3>Live stock</h3>
          <p>Current Redis product stock. Polling requests are hidden from logs.</p>
        </div>
        <div className="stock-grid">
          {products.map(product => {
            const isInvalid = product.stock < 0;
            const isEmpty = product.stock === 0;
            return (
              <article className={`stock-card ${isInvalid ? 'stock-card--danger' : isEmpty ? 'stock-card--empty' : ''}`} key={product.id}>
                <span>#{product.id}</span>
                <strong>{product.name}</strong>
                <b>{product.stock}</b>
                <small>{isInvalid ? 'Invalid stock' : isEmpty ? 'Out of stock' : 'Available'}</small>
              </article>
            );
          })}
        </div>
      </div>

      {loadResult && (
        <div className="result-strip">
          <span>Requests: <strong>{loadResult.total}</strong></span>
          <span>Success: <strong>{loadResult.success}</strong></span>
          <span>Failed: <strong>{loadResult.failed}</strong></span>
          <span>RPS: <strong>{loadResult.rps}</strong></span>
          <span>Avg: <strong>{loadResult.avgMs} ms</strong></span>
          <span>P95: <strong>{loadResult.p95Ms} ms</strong></span>
        </div>
      )}

      <div className="timeline-summary">
        <h3>Latest flow progress</h3>
        {latestFlow.length === 0 ? (
          <p>No flow yet. Run a load test or use the manual cart/checkout flow.</p>
        ) : (
          <div className="flow-steps">
            {latestFlow.map(log => (
              <button key={log.id} className={`flow-step flow-step--${log.level}`} onClick={() => setSelectedLog(log)}>
                <span>{shortStage(log.stage)}</span>
                <strong>{log.message}</strong>
                <small>{log.service} - {new Date(log.timestamp).toLocaleTimeString()}</small>
              </button>
            ))}
          </div>
        )}
      </div>

      <div className="log-toolbar">
        <label>
          Type
          <select value={logType} onChange={event => setLogType(event.target.value)}>
            <option value="app">Business timeline</option>
            <option value="http">Raw HTTP</option>
            <option value="all">All</option>
          </select>
        </label>
        <label>
          Service
          <select value={serviceFilter} onChange={event => setServiceFilter(event.target.value)}>
            <option value="all">All services</option>
            {SERVICES.map(service => <option key={service.key} value={`pu-${service.key}`}>{service.name}</option>)}
          </select>
        </label>
        <label>
          Level
          <select value={levelFilter} onChange={event => setLevelFilter(event.target.value)}>
            <option value="all">All levels</option>
            <option value="info">Info</option>
            <option value="warn">Warn</option>
            <option value="error">Error</option>
          </select>
        </label>
      </div>

      <div className="monitor-grid">
        <div className="log-list">
          {filteredLogs.length === 0 && <div className="empty-state">No matching logs. Try Raw HTTP or run a test.</div>}
          {filteredLogs.map(log => (
            <button
              className={`log-row log-row--timeline ${selectedLog?.id === log.id ? 'log-row--selected' : ''}`}
              key={log.id}
              onClick={() => setSelectedLog(log)}
            >
              <span className={`log-level log-level--${log.level}`}>{log.level}</span>
              <span>{new Date(log.timestamp).toLocaleTimeString()}</span>
              <strong>{log.service}</strong>
              <span>
                <b>{shortStage(log.stage)}</b>
                {log.message || `${log.method} ${log.path}`}
                {log.flowId ? <small>Flow: {log.flowId}</small> : null}
              </span>
              <span>{log.status ? `${log.status} - ${log.durationMs} ms` : ''}</span>
            </button>
          ))}
        </div>

        <aside className="log-detail">
          <h3>Selected log</h3>
          {selectedLog ? (
            <div className="log-readable">
              <p><strong>What happened:</strong> {selectedLog.message || `${selectedLog.method} ${selectedLog.path}`}</p>
              <p><strong>Stage:</strong> {shortStage(selectedLog.stage)}</p>
              <p><strong>Service:</strong> {selectedLog.service}</p>
              <p><strong>Result:</strong> {selectedLog.status || selectedLog.level}</p>
              <pre>{JSON.stringify(selectedLog, null, 2)}</pre>
            </div>
          ) : (
            <pre>Select a row to inspect the full request, response, and business details.</pre>
          )}
        </aside>
      </div>

      {loadResult?.samples?.length > 0 && (
        <div className="sample-results">
          <h3>Latest load test samples</h3>
          <pre>{JSON.stringify(loadResult.samples, null, 2)}</pre>
        </div>
      )}
    </section>
  );
}

export default MonitorDashboard;
