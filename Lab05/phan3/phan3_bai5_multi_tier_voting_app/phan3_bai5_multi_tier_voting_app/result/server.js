const express = require("express");
const { Client } = require("pg");

const app = express();
const port = process.env.PORT || 5001;

const client = new Client({
  host: process.env.PGHOST || "db",
  user: process.env.PGUSER || "postgres",
  password: process.env.PGPASSWORD || "postgres",
  database: process.env.PGDATABASE || "votesdb",
  port: Number(process.env.PGPORT || 5432),
});

async function initDb() {
  await client.connect();
  await client.query(
    "CREATE TABLE IF NOT EXISTS votes (" +
    "id SERIAL PRIMARY KEY," +
    "option_name VARCHAR(50) UNIQUE NOT NULL," +
    "vote_count INTEGER NOT NULL DEFAULT 0" +
    ");"
  );

  await client.query(
    "INSERT INTO votes(option_name, vote_count) VALUES ('Cats', 0), ('Dogs', 0) " +
    "ON CONFLICT (option_name) DO NOTHING;"
  );
}

app.get("/", async (req, res) => {
  try {
    const result = await client.query("SELECT option_name, vote_count FROM votes ORDER BY option_name ASC");
    const rows = result.rows;

    const cats = rows.find(r => r.option_name === "Cats")?.vote_count || 0;
    const dogs = rows.find(r => r.option_name === "Dogs")?.vote_count || 0;

    res.send(`
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Voting Result</title>
        <style>
          body { font-family: Arial, sans-serif; background: #f6f6f6; padding: 40px; }
          .box { max-width: 700px; margin: auto; background: #fff; padding: 30px; border-radius: 12px; box-shadow: 0 4px 18px rgba(0,0,0,.08); }
          .row { display: flex; justify-content: space-between; margin: 16px 0; font-size: 20px; }
          a { display: inline-block; margin-top: 20px; }
        </style>
      </head>
      <body>
        <div class="box">
          <h1>Voting Result</h1>
          <div class="row"><strong>Cats</strong><span>${cats}</span></div>
          <div class="row"><strong>Dogs</strong><span>${dogs}</span></div>
          <a href="http://localhost:5000">Back to Vote</a>
        </div>
      </body>
      </html>
    `);
  } catch (err) {
    res.status(500).send("Database error: " + err.message);
  }
});

initDb()
  .then(() => {
    app.listen(port, () => {
      console.log(`Result app running on port ${port}`);
    });
  })
  .catch((err) => {
    console.error("Failed to start result app:", err);
    process.exit(1);
  });
