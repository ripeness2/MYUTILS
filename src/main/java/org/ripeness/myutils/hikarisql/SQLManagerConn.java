package org.ripeness.myutils.hikarisql;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SQLManagerConn {

    public static void createTable(Connection conn, String table) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "`key` VARCHAR(256) NOT NULL PRIMARY KEY, "
                + "`value` TEXT"
                + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
        }
    }

    public static void set(Connection conn, Object key, Object value, String table) throws SQLException {
        ensureTableExists(conn, table);

        String keyString = objectToString(key);
        String valueString = (value == null) ? "\"\"" : objectToString(value);

        String query = "INSERT INTO " + table + " (`key`, `value`) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, keyString);
            preparedStatement.setString(2, valueString);
            preparedStatement.executeUpdate();
        }
    }

    public static void dropTable(Connection conn, String table) throws SQLException {
        String query = "DROP TABLE IF EXISTS " + table;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        }
    }

    public static String get(String key, Connection conn, String table) throws SQLException {
        String keyString = objectToString(key);

        String query = "SELECT `value` FROM " + table + " WHERE `key` = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, keyString);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("value");
                }
            }
        }
        return null;
    }

    public static String getString(String key, Connection conn, boolean includeQuotes, String table) throws SQLException {
        String value = get(key, conn, table);
        if (value != null && !includeQuotes) {
            return value.replace("\"", "");
        }
        return value;
    }

    public static Integer getInt(String key, Connection conn, String table) throws SQLException {
        String value = get(key, conn, table);
        if (value != null) {
            try {
                return Integer.parseInt(value.replace("\"", ""));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Boolean getBoolean(String key, Connection conn, String table) throws SQLException {
        String value = get(key, conn, table);
        if (value != null) {
            return Boolean.parseBoolean(value.replace("\"", ""));
        }
        return null;
    }

    public static List<String> getStringList(String key, Connection conn, String table) throws SQLException {
        String value = get(key, conn, table);

        if (value == null || value.equals("\"\"") || value.trim().isEmpty()) {
            return new ArrayList<>();
        }

        if (value.startsWith("[") && value.endsWith("]")) {
            value = value.substring(1, value.length() - 1);
            if (value.trim().isEmpty()) {
                return new ArrayList<>();
            }
            String[] elements = value.split("\",\"");
            List<String> list = new ArrayList<>();
            for (String element : elements) {
                list.add(element.replace("\"", ""));
            }
            return list;
        }

        return new ArrayList<>();
    }

    public static HashMap<String, Object> getHashMap(String key, Connection conn, String table) throws SQLException {
        String value = get(key, conn, table);

        if (value == null || value.equals("\"\"") || value.trim().isEmpty() || !value.startsWith("{") || !value.endsWith("}")) {
            return new HashMap<>();
        }

        value = value.substring(1, value.length() - 1).trim();
        HashMap<String, Object> resultMap = new HashMap<>();

        if (value.isEmpty()) return resultMap;

        String[] entries = value.split(", (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        for (String entry : entries) {
            String[] keyValue = entry.split(":", 2);
            if (keyValue.length == 2) {
                String mapKey = keyValue[0].replace("\"", "").trim();
                String mapValue = keyValue[1].trim();

                if (mapValue.startsWith("[") && mapValue.endsWith("]")) {
                    mapValue = mapValue.substring(1, mapValue.length() - 1);
                    if (mapValue.trim().isEmpty()) {
                        resultMap.put(mapKey, new ArrayList<>());
                    } else {
                        String[] listItems = mapValue.split("\",\"");
                        List<String> list = new ArrayList<>();
                        for (String item : listItems) {
                            list.add(item.replace("\"", ""));
                        }
                        resultMap.put(mapKey, list);
                    }
                } else {
                    if (mapValue.equals("\"true\"") || mapValue.equals("\"false\"")) {
                        resultMap.put(mapKey, Boolean.parseBoolean(mapValue.replace("\"", "")));
                    } else {
                        try {
                            resultMap.put(mapKey, Integer.parseInt(mapValue.replace("\"", "")));
                        } catch (NumberFormatException e) {
                            resultMap.put(mapKey, mapValue.replace("\"", ""));
                        }
                    }
                }
            }
        }

        return resultMap;
    }

    public static String objectToString(Object obj) {
        if (obj instanceof String) {
            return "\"" + obj + "\"";
        } else if (obj instanceof Boolean) {
            return "\"" + ((Boolean) obj ? "true" : "false") + "\"";
        } else if (obj instanceof Integer) {
            return "\"" + obj + "\"";
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            return list.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining("\",\"", "[\"", "\"]"));
        } else if (obj instanceof HashMap) {
            StringBuilder mapString = new StringBuilder("{");
            HashMap<?, ?> map = (HashMap<?, ?>) obj;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String mapKey = objectToString(entry.getKey());
                String mapValue = objectToString(entry.getValue());
                if (mapString.length() > 1) {
                    mapString.append(", ");
                }
                mapString.append(mapKey).append(":").append(mapValue);
            }
            mapString.append("}");
            return mapString.toString();
        } else {
            throw new IllegalArgumentException("Unsupported data type");
        }
    }

    public static boolean hasKey(String keystr, Connection conn, String table) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + table + " WHERE `key` = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, "\"" + keystr + "\"");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public static void ensureTableExists(Connection conn, String tableName) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "`key` VARCHAR(255) NOT NULL PRIMARY KEY, "
                + "`value` TEXT"
                + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
        }
    }

    public static List<String> getAllKeys(Connection conn, String table) throws SQLException {
        List<String> keys = new ArrayList<>();
        String query = "SELECT `key` FROM " + table;

        try (PreparedStatement preparedStatement = conn.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String key = resultSet.getString("key");
                if (key != null) {
                    keys.add(key.replace("\"", ""));
                }
            }
        }
        return keys;
    }

    public static boolean existsTable(Connection conn, String table) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, table, null)) {
            return rs.next();
        }
    }
}
