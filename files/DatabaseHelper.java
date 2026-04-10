import java.sql.*;
import java.util.*;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:hotel_system.db";

    public static void init() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Create guests table
            stmt.execute("CREATE TABLE IF NOT EXISTS guests (" +
                    "id TEXT PRIMARY KEY, " +
                    "fullName TEXT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "email TEXT, " +
                    "phone TEXT, " +
                    "createdAt TEXT)");

            // Create clerks table
            stmt.execute("CREATE TABLE IF NOT EXISTS clerks (" +
                    "id TEXT PRIMARY KEY, " +
                    "fullName TEXT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "role TEXT, " +
                    "permissions TEXT, " +
                    "createdAt TEXT)");

            // Seed admin account if clerks table is empty
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM clerks");
            if (rs.next() && rs.getInt(1) == 0) {
                String id = String.valueOf(System.currentTimeMillis());
                String createdAt = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                String sql = "INSERT INTO clerks (id, fullName, username, password, role, permissions, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, id);
                    pstmt.setString(2, "System Admin");
                    pstmt.setString(3, "admin");
                    pstmt.setString(4, "admin123");
                    pstmt.setString(5, "admin");
                    pstmt.setString(6, "manage_clerks|view_reports|manage_bookings");
                    pstmt.setString(7, createdAt);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> readAll(String tableName) {
        // tableName might be "guests.csv" or "clerks.csv" from old code
        String actualTable = tableName.replace(".csv", "");
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT * FROM " + actualTable;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public static boolean guestUsernameExists(String username) {
        String sql = "SELECT 1 FROM guests WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String[] findGuest(String username, String password) {
        String sql = "SELECT * FROM guests WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    String[] row = new String[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        row[i - 1] = rs.getString(i);
                    }
                    return row;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addGuest(String fullName, String username, String password, String email, String phone) {
        String id = String.valueOf(System.currentTimeMillis());
        String createdAt = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        String sql = "INSERT INTO guests (id, fullName, username, password, email, phone, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, fullName);
            pstmt.setString(3, username);
            pstmt.setString(4, password);
            pstmt.setString(5, email);
            pstmt.setString(6, phone);
            pstmt.setString(7, createdAt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean clerkUsernameExists(String username) {
        String sql = "SELECT 1 FROM clerks WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String[] findClerk(String username, String password) {
        String sql = "SELECT * FROM clerks WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    String[] row = new String[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        row[i - 1] = rs.getString(i);
                    }
                    return row;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addClerk(String fullName, String username, String password, String role, String permissions) {
        String id = String.valueOf(System.currentTimeMillis());
        String createdAt = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        String sql = "INSERT INTO clerks (id, fullName, username, password, role, permissions, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, fullName);
            pstmt.setString(3, username);
            pstmt.setString(4, password);
            pstmt.setString(5, role);
            pstmt.setString(6, permissions);
            pstmt.setString(7, createdAt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
