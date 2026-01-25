const express = require("express");
const config = require("../config");
const {
  signAccessToken,
  signRefreshToken,
  verifyRefreshToken,
  getExpiryInfo
} = require("../auth/jwt");
const store = require("../auth/store");

const router = express.Router();

// Demo user cố định
const DEMO_USER = { id: "u1", username: "admin", password: "123" };

function setAuthCookies(res, { accessToken, refreshToken }) {
  // Access token cookie (demo để UI đọc dễ)
  res.cookie(config.COOKIE.accessName, accessToken, {
    httpOnly: false, // demo: false để bạn thấy token trong browser
    sameSite: "lax",
  });

  // Refresh token cookie: HttpOnly (đúng chuẩn)
  res.cookie(config.COOKIE.refreshName, refreshToken, {
    httpOnly: true,
    sameSite: "lax",
  });
}

function clearAuthCookies(res) {
  res.clearCookie(config.COOKIE.accessName);
  res.clearCookie(config.COOKIE.refreshName);
}

router.post("/login", (req, res) => {
  const { username, password } = req.body;

  if (username !== DEMO_USER.username || password !== DEMO_USER.password) {
    return res.status(401).json({ message: "Invalid credentials" });
  }

  const payload = { sub: DEMO_USER.id, username: DEMO_USER.username };
  const accessToken = signAccessToken(payload);
  const refreshToken = signRefreshToken(payload);

  store.saveNewRefreshToken(DEMO_USER.id, refreshToken, {
    ua: req.headers["user-agent"]
  });

  setAuthCookies(res, { accessToken, refreshToken });

  return res.json({
    message: "Logged in",
    access: { token: accessToken },
    refresh: { token: "(HttpOnly cookie)" }
  });
});

router.post("/refresh", (req, res) => {
  const refreshToken = req.cookies?.[config.COOKIE.refreshName];
  if (!refreshToken) return res.status(401).json({ message: "Missing refresh token" });

  let decoded;
  try {
    decoded = verifyRefreshToken(refreshToken);
  } catch {
    return res.status(401).json({ message: "Invalid/expired refresh token" });
  }

  const userId = decoded.sub;
  if (!store.isRefreshTokenValid(userId, refreshToken)) {
    return res.status(401).json({ message: "Refresh token revoked/rotated" });
  }

  // rotation: revoke token cũ, phát token mới
  store.revokeRefreshToken(userId, refreshToken);

  const payload = { sub: userId, username: decoded.username };
  const newAccessToken = signAccessToken(payload);
  const newRefreshToken = signRefreshToken(payload);

  store.saveNewRefreshToken(userId, newRefreshToken, { rotatedFrom: "prev" });

  setAuthCookies(res, { accessToken: newAccessToken, refreshToken: newRefreshToken });

  return res.json({
    message: "Refreshed (rotation applied)",
    access: { token: newAccessToken, exp: getExpiryInfo(require("../auth/jwt").verifyAccessToken(newAccessToken)) },
    refresh: { token: "(HttpOnly cookie rotated)" }
  });
});

router.post("/logout", (req, res) => {
  const refreshToken = req.cookies?.[config.COOKIE.refreshName];
  if (refreshToken) {
    try {
      const decoded = verifyRefreshToken(refreshToken);
      store.revokeAll(decoded.sub); // logout all for user in demo
    } catch {
      // ignore
    }
  }
  clearAuthCookies(res);
  return res.json({ message: "Logged out" });
});

router.get("/protected", (req, res) => {
  // endpoint này check access token trực tiếp để minh hoạ
  const accessToken = req.cookies?.[config.COOKIE.accessName];
  if (!accessToken) return res.status(401).json({ message: "No access token" });

  try {
    const decoded = require("../auth/jwt").verifyAccessToken(accessToken);
    return res.json({
      message: "OK (protected data)",
      user: decoded,
      exp: getExpiryInfo(decoded)
    });
  } catch {
    return res.status(401).json({ message: "Access token invalid/expired" });
  }
});

router.get("/debug/store", (req, res) => {
  // cho UI xem trạng thái store
  res.json({
    userId: DEMO_USER.id,
    snapshot: store.debugSnapshot(DEMO_USER.id)
  });
});

module.exports = router;
