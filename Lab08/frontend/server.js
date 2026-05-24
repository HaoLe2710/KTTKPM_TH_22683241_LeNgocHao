const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = process.env.PORT || 5500;
const HOST = process.env.HOST || '0.0.0.0';

const contentTypes = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.json': 'application/json; charset=utf-8'
};

const server = http.createServer((req, res) => {
  if (req.url === '/favicon.ico') {
    res.writeHead(204);
    res.end();
    return;
  }

  const requestedPath = req.url === '/' ? '/index.html' : req.url;
  const safePath = path.normalize(requestedPath).replace(/^(\.\.[/\\])+/, '');
  const filePath = path.join(__dirname, safePath);

  fs.readFile(filePath, (error, content) => {
    if (error) {
      res.writeHead(error.code === 'ENOENT' ? 404 : 500, { 'Content-Type': 'text/plain; charset=utf-8' });
      res.end(error.code === 'ENOENT' ? '404 Not Found' : '500 Server Error');
      return;
    }

    const ext = path.extname(filePath);
    res.writeHead(200, { 'Content-Type': contentTypes[ext] || 'application/octet-stream' });
    res.end(content);
  });
});

server.listen(PORT, HOST, () => {
  console.log(`Frontend is running at http://${HOST}:${PORT}`);
  console.log(`Open http://localhost:${PORT} on this machine`);
});
