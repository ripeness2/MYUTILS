package org.ripeness.myutils.hikarisql;

import org.bukkit.Bukkit;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SQLManagerConn {
    // Sabitler
    private static final String LIST_PREFIX = "[::[::[";
    private static final String LIST_SUFFIX = "]::]::]";
    private static final String LIST_DELIM = "\",,..,,\""; // literal delimiter between quoted items
    private static final String MAP_PREFIX = "{::{::{";
    private static final String MAP_SUFFIX = "}::}::}";
    private static final String MAP_ENTRY_SEP = ",,..,, "; // entry separator between key-value pairs
    private static final String MAP_KV_SEP = "::..::...::";

    // ---------- DB yardımcıları ----------
    private static String sanitizeTableName(String table) throws SQLException {
        if (table == null || !table.matches("[A-Za-z0-9_]+")) {
            throw new SQLException("Invalid table name: " + table);
        }
        return table;
    }

    public static void ensureTableExists(Connection conn, String tableName) throws SQLException {
        String t = sanitizeTableName(tableName);
        String createTableSQL = "CREATE TABLE IF NOT EXISTS `" + t + "` ("
                + "`key` VARCHAR(255) NOT NULL PRIMARY KEY, "
                + "`value` TEXT"
                + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
        }
    }

    public static void createTable(Connection conn, String table) throws SQLException {
        ensureTableExists(conn, table);
    }

    public static void dropTable(Connection conn, String table) throws SQLException {
        String t = sanitizeTableName(table);
        String query = "DROP TABLE IF EXISTS `" + t + "`";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        }
    }

    // ---------- CRUD ----------
    public static void set(Connection conn, Object key, Object value, String table) throws SQLException {
        ensureTableExists(conn, table);
        String t = sanitizeTableName(table);

        String keyString = objectToString(key);
        String valueString = (value == null) ? "\"\"" : objectToString(value);

        String query = "INSERT INTO `" + t + "` (`key`, `value`) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, keyString);
            ps.setString(2, valueString);
            ps.executeUpdate();
        }
    }

    public static String get(Object key, Connection conn, String table) throws SQLException {
        // 1. Tablo adı kontrolü ve güvenli hale getirme
        if (table == null || table.trim().isEmpty()) {
//            Bukkit.getLogger().warning("[PluginAdin] Veritabanı hatası: Tablo adı boş olamaz!");
            return null;
        }

        String t = table.replaceAll("[^a-zA-Z0-9_]", "");

        // Temizleme işleminden sonra tablo adı tamamen silindiyse işlemi durdur
        if (t.isEmpty()) {
//            Bukkit.getLogger().warning("[PluginAdin] Veritabanı hatası: Geçersiz tablo adı (" + table + ")");
            return null;
        }

        String keyString = String.valueOf(key);

        // İsteğe bağlı: Sadece geliştirici modunda veya debug için konsola yazdır
        // Bukkit.getLogger().info("Sorgulanan tablo: " + t);

        // LIMIT 1 ile optimizasyon (MySQL ve SQLite için geçerlidir)
        String query = "SELECT `value` FROM `" + t + "` WHERE `key` = ? LIMIT 1";

        // 2. Try-with-resources kullanımı (Zaten çok iyi yapmışsın, aynen koruyoruz)
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, keyString);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String val = rs.getString("value");
                    // Eğer val null ise boş string dön, değilse veriyi dön
                    return val != null ? val : "";
                }
            }
        }

        // Veri bulunamadı
        return null;
    }

    // Asenkron çağırmak için CompletableFuture kullanımı
    public static CompletableFuture<String> getStringAsync(Object key, Connection conn, String table) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return get(key, conn, table);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static String getString(Object key, Connection conn, boolean includeQuotes, String table) throws SQLException {
        String value = get(key, conn, table);
        if (value == null) return null;
        if (!includeQuotes) {
            return stripSurroundingQuotesAndUnescape(value);
        }
        return value;
    }

    public static Integer getInt(Object key, Connection conn, String table) throws SQLException {
        String value = get(key, conn, table);
        if (value == null) return null;
        String raw = stripSurroundingQuotesAndUnescape(value);
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Boolean getBoolean(Object key, Connection conn, String table) throws SQLException {
        String value = get(key, conn, table);
        if (value == null) return null;
        String raw = stripSurroundingQuotesAndUnescape(value).toLowerCase(Locale.ROOT);
        if ("true".equals(raw)) return true;
        if ("false".equals(raw)) return false;
        return null; // stricter: invalid boolean -> null
    }

    public static boolean hasKey(Object key, Connection conn, String table) throws SQLException {
        String t = sanitizeTableName(table);
        String query = "SELECT COUNT(*) FROM `" + t + "` WHERE `key` = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, objectToString(key));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public static List<String> getAllKeys(Connection conn, String table) throws SQLException {
        String t = sanitizeTableName(table);
        List<String> keys = new ArrayList<>();
        String query = "SELECT `key` FROM `" + t + "`";
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String key = rs.getString("key");
                if (key != null) {
                    keys.add(stripSurroundingQuotesAndUnescape(key));
                }
            }
        }
        return keys;
    }

    public static boolean existsTable(Connection conn, String table) throws SQLException {
        String t = sanitizeTableName(table);
        try (ResultSet rs = conn.getMetaData().getTables(null, null, t, null)) {
            return rs.next();
        }
    }

    // ---------- Parsing helpers (list / map / generic parse) ----------
    public static List<String> getStringList(Object key, Connection conn, String table) throws SQLException {
        String value = get(key, conn, table);
        if (value == null || value.equals("\"\"") || value.trim().isEmpty()) {
            return new ArrayList<>();
        }

        if (value.startsWith(LIST_PREFIX) && value.endsWith(LIST_SUFFIX)) {
            String body = value.substring(LIST_PREFIX.length(), value.length() - LIST_SUFFIX.length());
            if (body.trim().isEmpty()) return new ArrayList<>();
            String[] elements = body.split(Pattern.quote(LIST_DELIM), -1);
            List<String> list = new ArrayList<>();
            for (String el : elements) {
                list.add(stripSurroundingQuotesAndUnescape(el));
            }
            return list;
        }

        return new ArrayList<>();
    }

    public static HashMap<String, Object> getHashMap(Object key, Connection conn, String table) throws SQLException {
        String value = get(key, conn, table);
        if (value == null || value.equals("\"\"") || value.trim().isEmpty()) {
            return new HashMap<>();
        }
        if (!(value.startsWith(MAP_PREFIX) && value.endsWith(MAP_SUFFIX))) {
            return new HashMap<>();
        }
        return parseMap(value);
    }

    // ---------- Serialization (object -> string) ----------
    public static String objectToString(Object obj) {
        if (obj == null) return "\"\"";
        if (obj instanceof String) {
            return "\"" + escapeString((String) obj) + "\"";
        } else if (obj instanceof Boolean) {
            return "\"" + obj.toString() + "\"";
        } else if (obj instanceof Integer || obj instanceof Long) {
            return "\"" + obj.toString() + "\"";
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            // each element is serialized as a quoted & escaped string
            String joined = list.stream()
                    .map(x -> "\"" + escapeString(String.valueOf(x)) + "\"")
                    .collect(Collectors.joining(LIST_DELIM));
            return LIST_PREFIX + joined + LIST_SUFFIX;
        } else if (obj instanceof HashMap) {
            StringBuilder mapString = new StringBuilder(MAP_PREFIX);
            HashMap<?, ?> map = (HashMap<?, ?>) obj;
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) mapString.append(MAP_ENTRY_SEP);
                first = false;
                String mapKey = objectToString(entry.getKey());   // key serialized (usually a quoted string)
                String mapValue = objectToString(entry.getValue()); // value serialized
                mapString.append(mapKey).append(MAP_KV_SEP).append(mapValue);
            }
            mapString.append(MAP_SUFFIX);
            return mapString.toString();
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + obj.getClass());
        }
    }

    // ---------- Low level parsing utilities ----------
    private static HashMap<String, Object> parseMap(String serialized) {
        // serialized starts with MAP_PREFIX and ends MAP_SUFFIX
        String body = serialized.substring(MAP_PREFIX.length(), serialized.length() - MAP_SUFFIX.length());
        HashMap<String, Object> result = new HashMap<>();
        if (body.trim().isEmpty()) return result;

        String[] entries = body.split(Pattern.quote(MAP_ENTRY_SEP), -1);
        for (String entry : entries) {
            String[] kv = entry.split(Pattern.quote(MAP_KV_SEP), 2);
            if (kv.length != 2) continue;
            String mapKeyRaw = kv[0].trim();
            String mapValRaw = kv[1].trim();

            String mapKey = stripSurroundingQuotesAndUnescape(mapKeyRaw);
            Object parsedVal = parseValue(mapValRaw);
            result.put(mapKey, parsedVal);
        }
        return result;
    }

    private static Object parseValue(String raw) {
        if (raw == null) return null;
        if (raw.startsWith(MAP_PREFIX) && raw.endsWith(MAP_SUFFIX)) {
            return parseMap(raw);
        }
        if (raw.startsWith(LIST_PREFIX) && raw.endsWith(LIST_SUFFIX)) {
            String body = raw.substring(LIST_PREFIX.length(), raw.length() - LIST_SUFFIX.length());
            if (body.trim().isEmpty()) return new ArrayList<String>();
            String[] items = body.split(Pattern.quote(LIST_DELIM), -1);
            List<String> list = new ArrayList<>();
            for (String it : items) {
                list.add(stripSurroundingQuotesAndUnescape(it));
            }
            return list;
        }
        // plain value: expected quoted string like "true" or "123" or "foo"
        String unq = stripSurroundingQuotesAndUnescape(raw);
        String lowered = unq.toLowerCase(Locale.ROOT);
        if ("true".equals(lowered)) return Boolean.TRUE;
        if ("false".equals(lowered)) return Boolean.FALSE;
        try {
            return Integer.parseInt(unq);
        } catch (NumberFormatException e) {
            return unq;
        }
    }

    // Escape/unescape routines (basic: backslash and double-quote)
    private static String escapeString(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String unescapeString(String s) {
        if (s == null) return "";
        // reverse of above: handle escaped quotes and backslashes
        // first replace escaped backslash then escaped quote
        // need to process backslash escapes safely
        StringBuilder sb = new StringBuilder();
        boolean esc = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (esc) {
                sb.append(c);
                esc = false;
            } else if (c == '\\') {
                esc = true;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String stripSurroundingQuotesAndUnescape(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.length() >= 2 && t.startsWith("\"") && t.endsWith("\"")) {
            String inner = t.substring(1, t.length() - 1);
            return unescapeString(inner);
        }
        // not quoted: return unescaped raw
        return unescapeString(t);
    }
}
