package org.ripeness.myutils.utils.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MySQLUtil (RNSQLUtil)
 * This Util made from _ripeness
 * this is my discord name: _ripeness
 * you can add friend me =)
 *
 *
 */
public class SQLManager {

    public static void set(Object key, Object value, SQLLocal local) throws SQLException {
        Connection connection = local.connect();
        String table = local.getTable();

        ensureTableExists(local, table);

        // Key ve Value'yi String olarak kaydetmek için formatları belirliyoruz
        String keyString = objectToString(key);
        String valueString = (value == null) ? "\"\"" : objectToString(value); // Eğer value null ise boş string yap

        String query = "INSERT INTO " + table + " (`key`, `value`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, keyString);
        preparedStatement.setString(2, valueString);

        preparedStatement.executeUpdate();
    }

    public static void dropTable(SQLLocal local) throws SQLException {
        Connection connection = local.connect();
        String table = local.getTable();

        String query = "DROP TABLE IF EXISTS " + table;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }

    // Genel get() fonksiyonu - Key'e göre değeri çeker
    public static String get(String key, SQLLocal local) throws SQLException {
        Connection connection = local.connect();
        String table = local.getTable();
        String keyString = objectToString(key);

        String query = "SELECT `value` FROM " + table + " WHERE `key` = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, keyString);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("value");
                }
            }
        }
        return null; // Eğer key bulunmazsa null döndür
    }

    // String olarak dönen veriyi getirir
    public static String getString(String key, SQLLocal local, boolean includeQuotes) throws SQLException {
        String value = get(key, local);
        if (value != null && !includeQuotes) {
            return value.replace("\"", ""); // Tırnakları temizle
        }
        return value; // Tırnaklarla geri döndür
    }


    // Integer olarak dönen veriyi getirir
    public static Integer getInt(String key, SQLLocal local) throws SQLException {
        String value = get(key, local);
        if (value != null) {
            try {
                return Integer.parseInt(value.replace("\"", "")); // Tırnak işaretlerini temizle ve parse et
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null; // Eğer geçersiz bir sayıysa veya boşsa null döndür
    }

    // Boolean olarak dönen veriyi getirir
    public static Boolean getBoolean(String key, SQLLocal local) throws SQLException {
        String value = get(key, local);
        if (value != null) {
            return Boolean.parseBoolean(value.replace("\"", "")); // Tırnak işaretlerini temizle ve parse et
        }
        return null; // Eğer değer bulunmazsa null döndür
    }

    // List<String> olarak dönen veriyi getirir
    public static List<String> getStringList(String key, SQLLocal local) throws SQLException {
        String value = get(key, local);

        // Eğer null ya da boş stringse direkt boş liste döndür
        if (value == null || value.equals("\"\"") || value.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // Eğer köşeli parantez varsa elemanları ayırmaya başla
        if (value.startsWith("[") && value.endsWith("]")) {
            value = value.substring(1, value.length() - 1); // Köşeli parantezleri kaldır
            if (value.trim().isEmpty()) {
                return new ArrayList<>(); // Eğer liste boşsa
            }
            String[] elements = value.split("\",\""); // Elemanları ayır
            List<String> list = new ArrayList<>();
            for (String element : elements) {
                list.add(element.replace("\"", "")); // Tırnak işaretlerini temizle ve listeye ekle
            }
            return list;
        }

        return new ArrayList<>(); // Eğer boş veya geçersizse boş liste döndür
    }


    public static HashMap<String, Object> getHashMap(String key, SQLLocal local) throws SQLException {
        String value = get(key, local);

        // Eğer değer null, boş ya da geçersiz formatta ise boş bir HashMap döndür
        if (value == null || value.equals("\"\"") || value.trim().isEmpty() || !value.startsWith("{") || !value.endsWith("}")) {
            return new HashMap<>(); // Eğer veri boşsa ya da doğru formatta değilse boş bir map döndür
        }

        // "{" ve "}" işaretlerini temizle
        value = value.substring(1, value.length() - 1).trim();

        HashMap<String, Object> resultMap = new HashMap<>();

        // Boş bir map ise direkt geri dön
        if (value.isEmpty()) {
            return resultMap; // Eğer map boşsa, boş HashMap döndür
        }

        // key:value formatında veriyi böl
        String[] entries = value.split(", (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Virgülleri doğru şekilde ayır

        for (String entry : entries) {
            String[] keyValue = entry.split(":", 2);
            if (keyValue.length == 2) {
                String mapKey = keyValue[0].replace("\"", "").trim(); // Tırnakları ve boşlukları temizle
                String mapValue = keyValue[1].trim();

                // Eğer mapValue bir liste ise
                if (mapValue.startsWith("[") && mapValue.endsWith("]")) {   
                    mapValue = mapValue.substring(1, mapValue.length() - 1); // Köşeli parantezleri temizle
                    if (mapValue.trim().isEmpty()) {
                        resultMap.put(mapKey, new ArrayList<>()); // Eğer liste boşsa boş bir liste ekle
                    } else {
                        String[] listItems = mapValue.split("\",\""); // Liste öğelerini ayır
                        List<String> list = new ArrayList<>();
                        for (String item : listItems) {
                            list.add(item.replace("\"", "")); // Her öğedeki tırnakları temizle
                        }
                        resultMap.put(mapKey, list);
                    }
                } else {
                    // Integer, Boolean, veya String gibi temel veri türlerini kontrol et
                    if (mapValue.equals("\"true\"") || mapValue.equals("\"false\"")) {
                        resultMap.put(mapKey, Boolean.parseBoolean(mapValue.replace("\"", "")));
                    } else {
                        try {
                            resultMap.put(mapKey, Integer.parseInt(mapValue.replace("\"", ""))); // Integer ise Integer olarak ekle
                        } catch (NumberFormatException e) {
                            resultMap.put(mapKey, mapValue.replace("\"", "")); // Eğer String ise direkt ekle
                        }
                    }
                }
            }
        }

        return resultMap;
    }



    // Özel objeyi String'e çeviren yardımcı metod
    public static String objectToString(Object obj) {
        if (obj instanceof String) {
            return "\"" + obj + "\"";
        } else if (obj instanceof Boolean) {
            return "\"" + ((Boolean) obj ? "true" : "false") + "\"";
        } else if (obj instanceof Integer) {
            return "\"" + obj + "\"";
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            // List elemanlarını String'e dönüştür ve join işlemi yap
            String listString = list.stream()
                    .map(String::valueOf)  // Her elemanı String'e dönüştür
                    .collect(Collectors.joining("\",\"", "[\"", "\"]")); // Join işlemi
            return listString;
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

    public static boolean hasKey(String keystr, SQLLocal local) throws SQLException {
        Connection connection = local.connect();
        String table = local.getTable();

        // Anahtar (key) değerini hazırlıyoruz
        String query = "SELECT COUNT(*) FROM " + table + " WHERE `key` = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "\""+keystr+"\""); // Verilen anahtarı sorguya yerleştiriyoruz
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // Eğer sonuç varsa true döndür
                }
            }
        }
        return false; // Eğer anahtar bulunmazsa false döndür
    }


    public static void ensureTableExists(SQLLocal local, String tableName) throws SQLException {


        // Tablo var mı kontrol et
        try (Statement stmt = local.connect().createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                    + "`key` VARCHAR(255) NOT NULL PRIMARY KEY, "
                    + "`value` TEXT"
                    + ")";
            stmt.executeUpdate(createTableSQL);
        }
    }

    public static List<String> getAllKeys(SQLLocal local) throws SQLException {
        List<String> keys = new ArrayList<>();
        String table = local.getTable();

        // Tüm anahtarları (keys) çekmek için sorgu
        String query = "SELECT `key` FROM " + table;

        try (Connection connection = local.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String key = resultSet.getString("key");
                if (key != null) {
                    // Anahtarı tırnaklardan arındır
                    keys.add(key.replace("\"", ""));
                }
            }
        }
        return keys;
    }



}
