require("http")
  .createServer((req, res) => {
    res.end("Hello, Docker!");
  })
  .listen(3000);