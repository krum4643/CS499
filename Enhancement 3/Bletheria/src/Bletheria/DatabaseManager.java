package Bletheria;

/**
 * DatabaseManager handles all SQLite interactions:
 * - User registration and authentication with hashed passwords
 * - Saving and loading player state (room, inventory, attribute)
 *
 */
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:bletheria.db";
    private Connection conn;

    public DatabaseManager() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }

    //defines tables
    private void createTables() {
        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL
            );
            """;

        String createState = """
            CREATE TABLE IF NOT EXISTS player_state (
                user_id INTEGER PRIMARY KEY,
                current_room TEXT NOT NULL,
                inventory TEXT,
                attribute TEXT NOT NULL,
                FOREIGN KEY(user_id) REFERENCES users(user_id)
            );
            """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsers);
            stmt.execute(createState);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    //register new user
    public int registerUser(String username, String plainPassword) {
        String sql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(plainPassword));
            int affected = ps.executeUpdate();
            if (affected == 0) {
                return -1;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            // likely a duplicate username or other DB error
            System.out.println("Registration error: " + e.getMessage());
        }
        return -1;
    }

   //authenticates existing user
    public int authenticateUser(String username, String plainPassword) {
        String sql = "SELECT user_id, password_hash FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return -1; // username not found
                }
                String storedHash = rs.getString("password_hash");
                String providedHash = hashPassword(plainPassword);
                if (storedHash.equals(providedHash)) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Authentication error: " + e.getMessage());
        }
        return -1;
    }

    //saves current state of player
    public void savePlayerState(Player player) {
        if (conn == null) return;

        String inventoryCsv = String.join(",", player.getInventory());

        // First attempt to update an existing row
        String update = """
            UPDATE player_state
            SET current_room = ?, inventory = ?, attribute = ?
            WHERE user_id = ?;
            """;

        // Insert if no row exists
        String insert = """
            INSERT INTO player_state(user_id, current_room, inventory, attribute)
            VALUES(?, ?, ?, ?);
            """;

        try (PreparedStatement psUpdate = conn.prepareStatement(update)) {
            psUpdate.setString(1, player.getCurrentRoom());
            psUpdate.setString(2, inventoryCsv);
            psUpdate.setString(3, player.getAttributeChoice());
            psUpdate.setInt(4, player.getUserId());

            int affected = psUpdate.executeUpdate();
            if (affected == 0) {
                try (PreparedStatement psInsert = conn.prepareStatement(insert)) {
                    psInsert.setInt(1, player.getUserId());
                    psInsert.setString(2, player.getCurrentRoom());
                    psInsert.setString(3, inventoryCsv);
                    psInsert.setString(4, player.getAttributeChoice());
                    psInsert.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saving player state: " + e.getMessage());
        }
    }

    //loads saved state if it exists with username
    public PlayerState loadPlayerState(int userId) {
        String sql = "SELECT current_room, inventory, attribute FROM player_state WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PlayerState state = new PlayerState();
                    state.currentRoom = rs.getString("current_room");
                    state.inventoryCsv = rs.getString("inventory");
                    state.attribute = rs.getString("attribute");
                    return state;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading player state: " + e.getMessage());
        }
        return null;
    }

    //container for saved states
    public static class PlayerState {
        public String currentRoom;
        public String inventoryCsv;
        public String attribute;
    }

    //hashes password
    private String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            // Fallback: store plaintext if hashing fails (not ideal, but avoids crashes)
            System.out.println("Hashing algorithm not found, storing plaintext password.");
            return plainPassword;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
