const http = require("http");

http.createServer((req, res) => {
  res.end("Hello Multi-stage Docker!");
}).listen(3000);