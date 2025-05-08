package org.ripeness.myutils.utils.objsconf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class objectmapManager {

    // Benzersiz tokenlar
    private static final String ENTRY_DELIM = ",,,,❌😅☠️🔓🔓YY£";
    private static final String KV_DELIM = "::::❌😅☠️🔓🔓YY£";
    private static final String LBRACE_TOKEN = "<<<<❌😅☠️🔓🔓YY£";
    private static final String RBRACE_TOKEN = "❌😅☠️🔓🔓YY£>>>>";
    private static final String LBRACKET_TOKEN = "[[[[❌😅☠️🔓🔓YY£";
    private static final String RBRACKET_TOKEN = "❌😅☠️🔓🔓YY£]]]]";

    // Genel objeyi string'e dönüştüren yardımcı fonksiyon
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
            return "\"" + escapeString(obj.toString()) + "\"";
        }
    }

    // Map'i özel format string'e çevir
    public static String serializeMap(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append(LBRACE_TOKEN);
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(ENTRY_DELIM);
            first = false;
            sb.append("\"").append(escapeString(entry.getKey())).append("\"")
              .append(KV_DELIM)
              .append(serializeObject(entry.getValue()));
        }
        sb.append(RBRACE_TOKEN);
        return sb.toString();
    }

    // List'i özel format string'e çevir
    public static String serializeList(List<Object> list) {
        StringBuilder sb = new StringBuilder();
        sb.append(LBRACKET_TOKEN);
        boolean first = true;
        for (Object element : list) {
            if (!first) sb.append(ENTRY_DELIM);
            first = false;
            sb.append(serializeObject(element));
        }
        sb.append(RBRACKET_TOKEN);
        return sb.toString();
    }

    private static String escapeString(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    //----------------- DESERIALIZE -----------------

    public static Object deserializeValue(String s) {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return unescapeString(s.substring(1, s.length() - 1));
        } else if (s.equals("true") || s.equals("false")) {
            return Boolean.parseBoolean(s);
        } else if (s.startsWith(LBRACE_TOKEN) && s.endsWith(RBRACE_TOKEN)) {
            return deserializeMap(s);
        } else if (s.startsWith(LBRACKET_TOKEN) && s.endsWith(RBRACKET_TOKEN)) {
            return deserializeList(s);
        } else {
            try {
                if (s.contains(".")) return Double.parseDouble(s);
                else return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return s;
            }
        }
    }

    public static Map<String, Object> deserializeMap(String tokenized) {
        String json = tokenized.substring(LBRACE_TOKEN.length(), tokenized.length() - RBRACE_TOKEN.length());
        List<String> pairs = splitElements(json);
        Map<String, Object> map = new HashMap<>();
        for (String pair : pairs) {
            int idx = pair.indexOf(KV_DELIM);
            if (idx < 0) continue;
            String rawKey = pair.substring(0, idx).trim();
            String rawValue = pair.substring(idx + KV_DELIM.length()).trim();
            String key = rawKey.substring(1, rawKey.length() - 1);
            map.put(key, deserializeValue(rawValue));
        }
        return map;
    }

    public static List<Object> deserializeList(String tokenized) {
        String inner = tokenized.substring(LBRACKET_TOKEN.length(), tokenized.length() - RBRACKET_TOKEN.length());
        List<String> elements = splitElements(inner);
        List<Object> list = new ArrayList<>();
        for (String el : elements) {
            list.add(deserializeValue(el));
        }
        return list;
    }

    private static String unescapeString(String input) {
        return input.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    // Benzersiz ENTRY_DELIM bazlı bölme
    private static List<String> splitElements(String s) {
        List<String> result = new ArrayList<>();
        int depth = 0;
        boolean inQuotes = false;
        int start = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"' && (i == 0 || s.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            }
            if (!inQuotes) {
                // token giriş/çıkış kontrolü
                if (s.startsWith(LBRACE_TOKEN, i) || s.startsWith(LBRACKET_TOKEN, i)) {
                    depth++;
                } else if (s.startsWith(RBRACE_TOKEN, i) || s.startsWith(RBRACKET_TOKEN, i)) {
                    depth--;
                }
                // entry delimiter kontrolü
                if (depth == 0 && s.startsWith(ENTRY_DELIM, i)) {
                    result.add(s.substring(start, i));
                    start = i + ENTRY_DELIM.length();
                    i = start - 1;
                }
            }
        }
        if (start <= s.length()) result.add(s.substring(start));
        return result;
    }
}
