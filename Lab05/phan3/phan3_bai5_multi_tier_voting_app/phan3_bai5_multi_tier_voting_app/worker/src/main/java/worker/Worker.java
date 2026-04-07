package worker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class Worker {
    public static void main(String[] args) throws Exception {
        String redisHost = System.getenv().getOrDefault("REDIS_HOST", "redis");
        String pgHost = System.getenv().getOrDefault("PGHOST", "db");
        String pgUser = System.getenv().getOrDefault("PGUSER", "postgres");
        String pgPassword = System.getenv().getOrDefault("PGPASSWORD", "postgres");
        String pgDatabase = System.getenv().getOrDefault("PGDATABASE", "votesdb");
        String pgPort = System.getenv().getOrDefault("PGPORT", "5432");
        String jdbcUrl = "jdbc:postgresql://" + pgHost + ":" + pgPort + "/" + pgDatabase;

        while (true) {
            try (Connection conn = DriverManager.getConnection(jdbcUrl, pgUser, pgPassword);
                 Jedis jedis = new Jedis(redisHost, 6379)) {

                initTable(conn);
                System.out.println("Worker connected to Redis and Postgres.");

                while (true) {
                    List<String> item = jedis.blpop(0, "votes");
                    if (item != null && item.size() == 2) {
                        String json = item.get(1);
                        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                        String vote = obj.get("vote").getAsString();
                        String optionName = "a".equals(vote) ? "Cats" : "Dogs";
                        updateVote(conn, optionName);
                        System.out.println("Processed vote for: " + optionName);
                    }
                }
            } catch (Exception e) {
                System.err.println("Worker error: " + e.getMessage());
                Thread.sleep(5000);
            }
        }
    }

    private static void initTable(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS votes (" +
                    "id SERIAL PRIMARY KEY," +
                    "option_name VARCHAR(50) UNIQUE NOT NULL," +
                    "vote_count INTEGER NOT NULL DEFAULT 0" +
                    ");");

            stmt.execute("INSERT INTO votes(option_name, vote_count) VALUES ('Cats', 0), ('Dogs', 0) " +
                    "ON CONFLICT (option_name) DO NOTHING;");
        }
    }

    private static void updateVote(Connection conn, String optionName) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE votes SET vote_count = vote_count + 1 WHERE option_name = ?")) {
            ps.setString(1, optionName);
            ps.executeUpdate();
        }
    }
}
