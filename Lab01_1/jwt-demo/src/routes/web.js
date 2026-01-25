const express = require("express");
const config = require("../config");
const { requireAuth } = require("../auth/middleware");
const jwt = require("jsonwebtoken");

const router = express.Router();

function decodeUnsafe(token) {
  try {
    return jwt.decode(token);
  } catch {
    return null;
  }
}

router.get("/", (req, res) => {
  res.render("index", {
    user: req.user,
    accessTTL: config.ACCESS_TOKEN_TTL,
    refreshTTL: config.REFRESH_TOKEN_TTL,
  });
});

router.get("/login", (req, res) => {
  res.render("login", { user: req.user, error: null });
});

router.get("/dashboard", requireAuth, (req, res) => {
  res.render("dashboard", { user: req.user });
});

router.get("/tokens", (req, res) => {
  const at = req.cookies?.[config.COOKIE.accessName] || null;
  const rt = req.cookies?.[config.COOKIE.refreshName] || null; // HttpOnly => EJS sẽ KHÔNG đọc được từ client JS, nhưng server render được

  res.render("tokens", {
    user: req.user,
    accessToken: at,
    refreshTokenPresent: Boolean(rt),
    decodedAccess: at ? decodeUnsafe(at) : null,
    decodedRefresh: rt ? decodeUnsafe(rt) : null,
    accessTTL: config.ACCESS_TOKEN_TTL,
    refreshTTL: config.REFRESH_TOKEN_TTL,
  });
});

module.exports = router;
