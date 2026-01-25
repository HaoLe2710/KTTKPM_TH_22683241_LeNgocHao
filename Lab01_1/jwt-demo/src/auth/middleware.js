const { verifyAccessToken } = require("./jwt");
const config = require("../config");

function attachUserFromAccessToken(req, _res, next) {
  const token = req.cookies?.[config.COOKIE.accessName];
  if (!token) {
    req.user = null;
    return next();
  }

  try {
    const decoded = verifyAccessToken(token);
    req.user = decoded; // { sub, username, ... }
  } catch {
    req.user = null; // hết hạn hoặc invalid
  }
  next();
}

function requireAuth(req, res, next) {
  if (!req.user) return res.redirect("/login");
  next();
}

module.exports = {
  attachUserFromAccessToken,
  requireAuth
};
