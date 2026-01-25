const jwt = require("jsonwebtoken");
const ms = require("ms");
const config = require("../config");

function signAccessToken(payload) {
  return jwt.sign(payload, config.JWT_ACCESS_SECRET, {
    expiresIn: config.ACCESS_TOKEN_TTL,
    issuer: "jwt-ejs-demo",
    audience: "web",
  });
}

function signRefreshToken(payload) {
  return jwt.sign(payload, config.JWT_REFRESH_SECRET, {
    expiresIn: config.REFRESH_TOKEN_TTL,
    issuer: "jwt-ejs-demo",
    audience: "web",
  });
}

function verifyAccessToken(token) {
  return jwt.verify(token, config.JWT_ACCESS_SECRET, {
    issuer: "jwt-ejs-demo",
    audience: "web",
  });
}

function verifyRefreshToken(token) {
  return jwt.verify(token, config.JWT_REFRESH_SECRET, {
    issuer: "jwt-ejs-demo",
    audience: "web",
  });
}

// tiện ích: lấy exp -> ms còn lại
function getExpiryInfo(decoded) {
  if (!decoded?.exp) return null;
  const expMs = decoded.exp * 1000;
  const now = Date.now();
  return {
    expIso: new Date(expMs).toISOString(),
    remainingMs: Math.max(0, expMs - now),
    remainingHuman: ms(Math.max(0, expMs - now))
  };
}

module.exports = {
  signAccessToken,
  signRefreshToken,
  verifyAccessToken,
  verifyRefreshToken,
  getExpiryInfo,
};
