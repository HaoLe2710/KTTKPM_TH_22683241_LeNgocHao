const express = require("express");
const path = require("path");
const cookieParser = require("cookie-parser");

const config = require("./config");
const webRoutes = require("./routes/web");
const apiRoutes = require("./routes/api");
const { attachUserFromAccessToken } = require("./auth/middleware");

const app = express();

app.set("view engine", "ejs");
app.set("views", path.join(__dirname, "views"));

app.use(express.urlencoded({ extended: false }));
app.use(express.json());
app.use(cookieParser());

app.use(attachUserFromAccessToken);

app.use("/", webRoutes);
app.use("/api", apiRoutes);

app.listen(config.PORT, () => {
  console.log(`JWT EJS demo running: http://localhost:${config.PORT}`);
});
