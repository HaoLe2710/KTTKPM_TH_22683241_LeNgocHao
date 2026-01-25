const crypto = require("crypto");

function sha256(input) {
  return crypto.createHash("sha256").update(input).digest("hex");
}

/**
 * In-memory store:
 * userId -> { currentRefreshHash, revokedHashes:Set, sessions:[] }
 */
const store = new Map();

function ensureUser(userId) {
  if (!store.has(userId)) {
    store.set(userId, {
      currentRefreshHash: null,
      revokedHashes: new Set(),
      sessions: []
    });
  }
  return store.get(userId);
}

function saveNewRefreshToken(userId, refreshToken, meta = {}) {
  const u = ensureUser(userId);
  const h = sha256(refreshToken);

  // rotation: token mới sẽ là current
  u.currentRefreshHash = h;
  u.sessions.push({
    hash: h,
    createdAt: new Date().toISOString(),
    meta
  });

  return h;
}

function revokeRefreshToken(userId, refreshToken) {
  const u = ensureUser(userId);
  const h = sha256(refreshToken);
  u.revokedHashes.add(h);

  if (u.currentRefreshHash === h) u.currentRefreshHash = null;
}

function isRefreshTokenValid(userId, refreshToken) {
  const u = ensureUser(userId);
  const h = sha256(refreshToken);

  if (u.revokedHashes.has(h)) return false;
  // chỉ cho phép refresh token "current" (để thấy rotation hoạt động)
  return u.currentRefreshHash === h;
}

function revokeAll(userId) {
  const u = ensureUser(userId);
  if (u.currentRefreshHash) u.revokedHashes.add(u.currentRefreshHash);
  u.currentRefreshHash = null;
}

function debugSnapshot(userId) {
  const u = ensureUser(userId);
  return {
    currentRefreshHash: u.currentRefreshHash,
    revokedCount: u.revokedHashes.size,
    sessions: u.sessions.slice(-10) // show 10 gần nhất
  };
}

module.exports = {
  saveNewRefreshToken,
  revokeRefreshToken,
  isRefreshTokenValid,
  revokeAll,
  debugSnapshot,
};
