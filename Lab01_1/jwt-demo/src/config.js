module.exports = {
  PORT: 3001,

  JWT_ACCESS_SECRET: "dev_access_secret_change_me",
  JWT_REFRESH_SECRET: "dev_refresh_secret_change_me",

  // demo cho dễ thấy hết hạn
  ACCESS_TOKEN_TTL: "20s",      // access token sống 20 giây
  REFRESH_TOKEN_TTL: "10m",     // refresh token sống 10 phút

  COOKIE: {
    // cookie cho refresh token (HttpOnly)
    refreshName: "rt",
    // cookie cho access token (demo UI cho dễ nhìn; thực tế nên để memory/header)
    accessName: "at",
  }
};
