package org.ripeness.myutils.utils.objsconf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class objectmapManager {


    // Genel objeyi JSON string'e dönüştüren yardımcı fonksiyon
    public static String serializeObject(Object obj) {
        if (obj instanceof String) {
            return "\"" + escapeString((String) obj) + "\"";
        } else if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        } else if (obj instanceof Map) {
            return serializeMap((Map<String, Object>) obj);
        } else if (obj instanceof List) {
            return serializeList((List<Object>) obj);
        } else {
            // Desteklenmeyen tipler için toString() kullanılıyor.
            return "\"" + escapeString(obj.toString()) + "\"";
        }
    }

    // Map'i JSON benzeri string'e çeviren fonksiyon
    public static String serializeMap(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append("\"").append(escapeString(entry.getKey())).append("\":");
            sb.append(serializeObject(entry.getValue()));
        }
        sb.append("}");
        return sb.toString();
    }

    // List'i JSON benzeri string'e çeviren fonksiyon
    public static String serializeList(List<Object> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (Object element : list) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append(serializeObject(element));
        }
        sb.append("]");
        return sb.toString();
    }

    // Gerekli kaçış işlemi: yalnızca çift tırnak ve ters bölü işliyoruz
    private static String escapeString(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Genel bir deserialize işlemi yapar: tipine göre yönlendirir
    public static Object deserializeValue(String s) {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return unescapeString(s.substring(1, s.length() - 1));
        } else if (s.equals("true") || s.equals("false")) {
            return Boolean.parseBoolean(s);
        } else if (s.startsWith("{") && s.endsWith("}")) {
            return deserializeMap(s);
        } else if (s.startsWith("[") && s.endsWith("]")) {
            return deserializeList(s);
        } else {
            try {
                if (s.contains(".")) {
                    return Double.parseDouble(s);
                } else {
                    return Integer.parseInt(s);
                }
            } catch (NumberFormatException e) {
                return s;
            }
        }
    }

    // JSON benzeri string'i Map'e çeviren fonksiyon
    public static Map<String, Object> deserializeMap(String json) {
        Map<String, Object> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
        }
        List<String> pairs = splitElements(json);
        for (String pair : pairs) {
            int colonIndex = pair.indexOf(":");
            if (colonIndex < 0) continue;
            String key = pair.substring(0, colonIndex).trim();
            String value = pair.substring(colonIndex + 1).trim();
            if (key.startsWith("\"") && key.endsWith("\"")) {
                key = unescapeString(key.substring(1, key.length() - 1));
            }
            map.put(key, deserializeValue(value));
        }
        return map;
    }

    // JSON benzeri string'i List'e çeviren fonksiyon
    public static List<Object> deserializeList(String json) {
        List<Object> list = new ArrayList<>();
        json = json.trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1);
        }
        List<String> elements = splitElements(json);
        for (String element : elements) {
            list.add(deserializeValue(element));
        }
        return list;
    }

    // Gerekli ters kaçış işlemi
    private static String unescapeString(String input) {
        return input.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    // İç içe yapıdaki virgüllerden ayırmak için, stringi düzgün şekilde bölen yardımcı fonksiyon
    private static List<String> splitElements(String s) {
        List<String> result = new ArrayList<>();
        int depth = 0;
        int start = 0;
        boolean inQuotes = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"' && (i == 0 || s.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            }
            if (!inQuotes) {
                if (c == '{' || c == '[') {
                    depth++;
                } else if (c == '}' || c == ']') {
                    depth--;
                } else if (c == ',' && depth == 0) {
                    result.add(s.substring(start, i));
                    start = i + 1;
                }
            }
        }
        if (start < s.length()) {
            result.add(s.substring(start));
        }
        return result;
    }

    // Test amaçlı main metodu
//    public static void main(String[] args) {
//        // Örnek Map oluşturulması
//        Map<String, Object> config = new HashMap<>();
//        config.put("key1", "value1");
//        config.put("key2", 123);
//        config.put("key3", true);
//
//        // List örneği oluşturulması
//        List<Object> list = new ArrayList<>();
//        list.add("item1");
//        list.add(45);
//        list.add(false);
//        // Map'e list ekleniyor
//        config.put("listKey", list);
//
//        String serialized = serializeMap(config);
//        System.out.println("Serialized: " + serialized);
//
//        Map<String, Object> deserialized = deserializeMap(serialized);
//        System.out.println("Deserialized: " + deserialized);
//    }

}
