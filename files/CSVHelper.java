import java.io.*;
import java.util.*;

public class CSVHelper {

    // ── File paths ────────────────────────────────────────────────────────────
    private static final String GUESTS_FILE  = "guests.csv";
    private static final String CLERKS_FILE  = "clerks.csv";

    private static final String GUEST_HEADER = "id,fullName,username,password,email,phone,createdAt";
    private static final String CLERK_HEADER = "id,fullName,username,password,role,permissions,createdAt";

    // ── Init: create CSV files with headers if they don't exist ───────────────
    public static void init() {
        ensureFile(GUESTS_FILE, GUEST_HEADER);
        ensureFile(CLERKS_FILE, CLERK_HEADER);

        // Seed one admin account if clerks.csv is empty
        if (readAll(CLERKS_FILE).isEmpty()) {
            String id        = String.valueOf(System.currentTimeMillis());
            String createdAt = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
            appendRow(CLERKS_FILE,
                id + ",System Admin,admin,admin123,admin,manage_clerks|view_reports|manage_bookings," + createdAt);
        }
    }

    private static void ensureFile(String path, String header) {
        File f = new File(path);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println(header);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ── Generic read ──────────────────────────────────────────────────────────
    public static List<String[]> readAll(String file) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // skip header
                if (!line.trim().isEmpty()) rows.add(line.split(",", -1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }

    // ── Generic append ────────────────────────────────────────────────────────
    public static void appendRow(String file, String row) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            pw.println(row);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Guest helpers ─────────────────────────────────────────────────────────
    public static boolean guestUsernameExists(String username) {
        for (String[] row : readAll(GUESTS_FILE))
            if (row.length > 2 && row[2].equalsIgnoreCase(username)) return true;
        return false;
    }

    /** Returns guest row or null */
    public static String[] findGuest(String username, String password) {
        for (String[] row : readAll(GUESTS_FILE))
            if (row.length > 3 && row[2].equals(username) && row[3].equals(password))
                return row;
        return null;
    }

    public static void addGuest(String fullName, String username,
                                String password, String email, String phone) {
        String id        = String.valueOf(System.currentTimeMillis());
        String createdAt = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
        appendRow(GUESTS_FILE,
            id + "," + fullName + "," + username + "," + password + ","
            + email + "," + phone + "," + createdAt);
    }

    // ── Clerk helpers ─────────────────────────────────────────────────────────
    public static boolean clerkUsernameExists(String username) {
        for (String[] row : readAll(CLERKS_FILE))
            if (row.length > 2 && row[2].equalsIgnoreCase(username)) return true;
        return false;
    }

    /** Returns clerk row or null */
    public static String[] findClerk(String username, String password) {
        for (String[] row : readAll(CLERKS_FILE))
            if (row.length > 3 && row[2].equals(username) && row[3].equals(password))
                return row;
        return null;
    }

    public static void addClerk(String fullName, String username,
                                String password, String role, String permissions) {
        String id        = String.valueOf(System.currentTimeMillis());
        String createdAt = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
        appendRow(CLERKS_FILE,
            id + "," + fullName + "," + username + "," + password + ","
            + role + "," + permissions + "," + createdAt);
    }
}
