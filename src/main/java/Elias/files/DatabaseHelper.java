package Elias.files;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

/**
 * DatabaseHelper — XML-backed storage (replaces the previous SQLite version).
 *
 * Data is stored in two XML files next to the running jar:
 *   guests.xml  — guest accounts
 *   clerks.xml  — clerk / admin accounts
 *
 * Public API is identical to the SQLite version so no other file needs changing.
 *
 * XML structure (clerks.xml example):
 * <clerks>
 *   <clerk>
 *     <id>1712345678901</id>
 *     <fullName>System Admin</fullName>
 *     <username>admin</username>
 *     <password>admin123</password>
 *     <role>admin</role>
 *     <permissions>manage_clerks|view_reports|manage_bookings</permissions>
 *     <createdAt>2026-04-14</createdAt>
 *   </clerk>
 * </clerks>
 */
public class DatabaseHelper {

    // ── File paths ─────────────────────────────────────────────────────────
    private static final String GUESTS_FILE = "guests.xml";
    private static final String CLERKS_FILE = "clerks.xml";

    // Column order returned by readAll / findGuest / findClerk
    // guests : id, fullName, username, password, email, phone, createdAt
    // clerks : id, fullName, username, password, role, permissions, createdAt
    private static final String[] GUEST_FIELDS =
            { "id", "fullName", "username", "password", "email", "phone", "createdAt" };
    private static final String[] CLERK_FIELDS =
            { "id", "fullName", "username", "password", "role", "permissions", "createdAt" };

    // ── init ───────────────────────────────────────────────────────────────

    /**
     * Creates the XML files if they do not exist and seeds the default admin
     * account when the clerks file is brand-new / empty.
     */
    public static void init() {
        ensureFile(GUESTS_FILE, "guests");
        boolean clerksWasEmpty = !new File(CLERKS_FILE).exists() || isFileEmpty(CLERKS_FILE);
        ensureFile(CLERKS_FILE, "clerks");

        if (clerksWasEmpty) {
            addClerk("System Admin", "admin", "admin123", "admin",
                     "manage_clerks|view_reports|manage_bookings");
        }
    }

    // ── Public read helpers ────────────────────────────────────────────────

    /**
     * Returns every row from the requested table as String arrays.
     * tableName may be "guests", "guests.csv", "clerks", or "clerks.csv"
     * (the .csv suffix is accepted for backward-compatibility).
     */
    public static List<String[]> readAll(String tableName) {
        String name = tableName.replace(".csv", "").trim().toLowerCase();
        if (name.equals("guests")) return readAll(GUESTS_FILE, GUEST_FIELDS, "guest");
        if (name.equals("clerks")) return readAll(CLERKS_FILE, CLERK_FIELDS, "clerk");
        return new ArrayList<>();
    }

    // ── Guest operations ───────────────────────────────────────────────────

    public static boolean guestUsernameExists(String username) {
        return findRow(GUESTS_FILE, "guest", "username", username, true) != null;
    }

    public static String[] findGuest(String username, String password) {
        return findByCredentials(GUESTS_FILE, "guest", GUEST_FIELDS, username, password);
    }

    public static void addGuest(String fullName, String username,
                                String password, String email, String phone) {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("id",        String.valueOf(System.currentTimeMillis()));
        data.put("fullName",  fullName);
        data.put("username",  username);
        data.put("password",  password);
        data.put("email",     email);
        data.put("phone",     phone);
        data.put("createdAt", today());
        appendRecord(GUESTS_FILE, "guests", "guest", data);
    }

    // ── Clerk operations ───────────────────────────────────────────────────

    public static boolean clerkUsernameExists(String username) {
        return findRow(CLERKS_FILE, "clerk", "username", username, true) != null;
    }

    public static String[] findClerk(String username, String password) {
        return findByCredentials(CLERKS_FILE, "clerk", CLERK_FIELDS, username, password);
    }

    public static void addClerk(String fullName, String username,
                                String password, String role, String permissions) {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("id",          String.valueOf(System.currentTimeMillis()));
        data.put("fullName",    fullName);
        data.put("username",    username);
        data.put("password",    password);
        data.put("role",        role);
        data.put("permissions", permissions);
        data.put("createdAt",   today());
        appendRecord(CLERKS_FILE, "clerks", "clerk", data);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Private helpers
    // ═══════════════════════════════════════════════════════════════════════

    /** Reads all child elements and returns their fields as String arrays. */
    private static List<String[]> readAll(String file, String[] fields, String tag) {
        List<String[]> result = new ArrayList<>();
        try {
            Document doc = parse(file);
            NodeList nodes = doc.getElementsByTagName(tag);
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                String[] row = new String[fields.length];
                for (int j = 0; j < fields.length; j++) {
                    row[j] = text(el, fields[j]);
                }
                result.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Finds a record whose field value matches.
     * When ignoreCase is true the comparison is case-insensitive.
     */
    private static Element findRow(String file, String tag,
                                   String field, String value, boolean ignoreCase) {
        try {
            Document doc = parse(file);
            NodeList nodes = doc.getElementsByTagName(tag);
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                String v = text(el, field);
                boolean match = ignoreCase ? v.equalsIgnoreCase(value) : v.equals(value);
                if (match) return el;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Finds a record by username + password (exact match on both). */
    private static String[] findByCredentials(String file, String tag,
                                              String[] fields,
                                              String username, String password) {
        try {
            Document doc = parse(file);
            NodeList nodes = doc.getElementsByTagName(tag);
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                if (text(el, "username").equals(username)
                        && text(el, "password").equals(password)) {
                    String[] row = new String[fields.length];
                    for (int j = 0; j < fields.length; j++) {
                        row[j] = text(el, fields[j]);
                    }
                    return row;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Appends a new record element to the XML file and saves it. */
    private static void appendRecord(String file, String rootTag,
                                     String childTag, Map<String, String> data) {
        try {
            Document doc = parse(file);
            Element root = doc.getDocumentElement();

            Element record = doc.createElement(childTag);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                Element field = doc.createElement(entry.getKey());
                field.setTextContent(entry.getValue() == null ? "" : entry.getValue());
                record.appendChild(field);
            }
            root.appendChild(record);
            save(doc, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Creates an XML file with an empty root element if it does not exist yet. */
    private static void ensureFile(String path, String rootTag) {
        File f = new File(path);
        if (f.exists()) return;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document doc = factory.newDocumentBuilder().newDocument();
            doc.appendChild(doc.createElement(rootTag));
            save(doc, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Returns true when the XML file has no child elements under its root. */
    private static boolean isFileEmpty(String path) {
        try {
            Document doc = parse(path);
            NodeList children = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getNodeType() == Node.ELEMENT_NODE) return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    /** Parses an XML file into a DOM Document. */
    private static Document parse(String path) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(null); // suppress default SAX stderr output
        return builder.parse(new File(path));
    }

    /** Serialises a DOM Document back to disk with indented formatting. */
    private static void save(Document doc, String path) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT,    "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.ENCODING,  "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE,"yes");
        transformer.transform(new DOMSource(doc),
                              new StreamResult(new File(path)));
    }

    /** Safely reads the text content of a named child element (never null). */
    private static String text(Element parent, String tagName) {
        NodeList nl = parent.getElementsByTagName(tagName);
        if (nl.getLength() == 0) return "";
        Node n = nl.item(0);
        return n.getTextContent() == null ? "" : n.getTextContent();
    }

    /** Returns today's date formatted as yyyy-MM-dd. */
    private static String today() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
    }
}
