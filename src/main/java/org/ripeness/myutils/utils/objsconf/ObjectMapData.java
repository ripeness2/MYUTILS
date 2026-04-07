package org.ripeness.myutils.utils.objsconf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectMapData {

    // --- YENİ TOKENLAR (Tek Karakterlik Görünmez ASCII Kontrol Karakterleri) ---
    // Performans için sadece 1 karakterlik özel ASCII karakterleri kullanıyoruz.
    private static final char ENTRY_DELIM_CHAR = '\u001F'; // Unit Separator
    private static final char KV_DELIM_CHAR = '\u001E';    // Record Separator
    private static final char LBRACE_CHAR = '\u0001';      // Start of Heading
    private static final char RBRACE_CHAR = '\u0002';      // Start of Text
    private static final char LBRACKET_CHAR = '\u0003';    // End of Text
    private static final char RBRACKET_CHAR = '\u0004';    // End of Transmission

    private static final String ENTRY_DELIM = String.valueOf(ENTRY_DELIM_CHAR);
    private static final String KV_DELIM = String.valueOf(KV_DELIM_CHAR);
    private static final String LBRACE_TOKEN = String.valueOf(LBRACE_CHAR);
    private static final String RBRACE_TOKEN = String.valueOf(RBRACE_CHAR);
    private static final String LBRACKET_TOKEN = String.valueOf(LBRACKET_CHAR);
    private static final String RBRACKET_TOKEN = String.valueOf(RBRACKET_CHAR);

    // --- ESKİ TOKENLAR (Sadece Migration İçin Kullanılacak) ---
    private static final String OLD_ENTRY_DELIM = ",,,,❌😅☠️🔓🔓YY£";
    private static final String OLD_KV_DELIM = "::::❌😅☠️🔓🔓YY£";
    private static final String OLD_LBRACE_TOKEN = "<<<<❌😅☠️🔓🔓YY£";
    private static final String OLD_RBRACE_TOKEN = "❌😅☠️🔓🔓YY£>>>>";
    private static final String OLD_LBRACKET_TOKEN = "[[[[❌😅☠️🔓🔓YY£";
    private static final String OLD_RBRACKET_TOKEN = "❌😅☠️🔓🔓YY£]]]]";

    /**
     * Eski emojili formatta kaydedilmiş verileri yeni performansı yüksek
     * formata geçirmek için kullanacağın metot.
     */
    public static String migrateOldDataToNewFormat(String oldData) {
        if (oldData == null || oldData.isEmpty()) return oldData;
        return oldData
                .replace(OLD_ENTRY_DELIM, ENTRY_DELIM)
                .replace(OLD_KV_DELIM, KV_DELIM)
                .replace(OLD_LBRACE_TOKEN, LBRACE_TOKEN)
                .replace(OLD_RBRACE_TOKEN, RBRACE_TOKEN)
                .replace(OLD_LBRACKET_TOKEN, LBRACKET_TOKEN)
                .replace(OLD_RBRACKET_TOKEN, RBRACKET_TOKEN);
    }

    //----------------- SERIALIZE -----------------

    @SuppressWarnings("unchecked")
    public static String serializeObject(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof String) {
            return "\"" + escapeString((String) obj) + "\"";
        } else if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        } else if (obj instanceof Map) {
            return serializeMap((Map<?, ?>) obj);
        } else if (obj instanceof List) {
            return serializeList((List<?>) obj);
        } else {
            return "\"" + escapeString(obj.toString()) + "\"";
        }
    }

    public static String serializeMap(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        sb.append(LBRACE_TOKEN);
        
        // Boş map kontrolü (Bug fix)
        if (map.isEmpty()) {
            sb.append(RBRACE_TOKEN);
            return sb.toString();
        }

        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(ENTRY_DELIM);
            first = false;
            
            // Key ne olursa olsun String'e çevirip öyle escape ediyoruz (Tip güvenliği fix)
            String keyStr = entry.getKey() == null ? "null" : entry.getKey().toString();
            
            sb.append("\"").append(escapeString(keyStr)).append("\"")
              .append(KV_DELIM)
              .append(serializeObject(entry.getValue()));
        }
        sb.append(RBRACE_TOKEN);
        return sb.toString();
    }

    public static String serializeList(List<?> list) {
        StringBuilder sb = new StringBuilder();
        sb.append(LBRACKET_TOKEN);
        
        // Boş liste kontrolü (Bug fix)
        if (list.isEmpty()) {
            sb.append(RBRACKET_TOKEN);
            return sb.toString();
        }

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
        // Yeni satır vb. karakterleri de escape ediyoruz
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }

    //----------------- DESERIALIZE -----------------

    public static Object deserializeValue(String s) {
        s = s.trim();
        if (s.equals("null")) {
            return null;
        } else if (s.startsWith("\"") && s.endsWith("\"")) {
            return unescapeString(s.substring(1, s.length() - 1));
        } else if (s.equals("true") || s.equals("false")) {
            return Boolean.parseBoolean(s);
        } else if (s.startsWith(LBRACE_TOKEN) && s.endsWith(RBRACE_TOKEN)) {
            return deserializeMap(s);
        } else if (s.startsWith(LBRACKET_TOKEN) && s.endsWith(RBRACKET_TOKEN)) {
            return deserializeList(s);
        } else {
            // Veri Kaybı Engellendi: Önce Long deniyoruz, taşıyorsa Double deniyoruz.
            try {
                if (s.contains(".")) {
                    return Double.parseDouble(s);
                } else {
                    return Long.parseLong(s); 
                }
            } catch (NumberFormatException e) {
                // Çok büyükse veya bozuksa string olarak geri ver
                return s;
            }
        }
    }

    public static Map<String, Object> deserializeMap(String tokenized) {
        String inner = tokenized.substring(LBRACE_TOKEN.length(), tokenized.length() - RBRACE_TOKEN.length());
        Map<String, Object> map = new HashMap<>();
        
        if (inner.isEmpty()) return map; // Boş durum kontrolü

        List<String> pairs = splitElements(inner);
        for (String pair : pairs) {
            int idx = pair.indexOf(KV_DELIM);
            if (idx < 0) continue;
            String rawKey = pair.substring(0, idx).trim();
            String rawValue = pair.substring(idx + KV_DELIM.length()).trim();
            
            // Tırnakları kaldır
            String key = rawKey;
            if (rawKey.startsWith("\"") && rawKey.endsWith("\"")) {
                 key = unescapeString(rawKey.substring(1, rawKey.length() - 1));
            }
            
            map.put(key, deserializeValue(rawValue));
        }
        return map;
    }

    public static List<Object> deserializeList(String tokenized) {
        String inner = tokenized.substring(LBRACKET_TOKEN.length(), tokenized.length() - RBRACKET_TOKEN.length());
        List<Object> list = new ArrayList<>();
        
        if (inner.isEmpty()) return list; // Boş durum kontrolü

        List<String> elements = splitElements(inner);
        for (String el : elements) {
            list.add(deserializeValue(el));
        }
        return list;
    }

    private static String unescapeString(String input) {
        return input.replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
    }

    // Benzersiz ENTRY_DELIM bazlı bölme (Performans Sorunu Çözüldü: O(N) Karmaşıklık)
    private static List<String> splitElements(String s) {
        List<String> result = new ArrayList<>();
        if (s.isEmpty()) return result;

        int depth = 0;
        boolean inQuotes = false;
        int start = 0;
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            
            if (c == '"' && (i == 0 || s.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            }
            
            if (!inQuotes) {
                // Token giriş/çıkış kontrolü - sadece char kıyaslaması olduğu için ışık hızında!
                if (c == LBRACE_CHAR || c == LBRACKET_CHAR) {
                    depth++;
                } else if (c == RBRACE_CHAR || c == RBRACKET_CHAR) {
                    depth--;
                } 
                // Entry delimiter kontrolü
                else if (depth == 0 && c == ENTRY_DELIM_CHAR) {
                    String element = s.substring(start, i);
                    if (!element.isEmpty()) {
                        result.add(element);
                    }
                    start = i + 1;
                }
            }
        }
        
        if (start < s.length()) {
            String remainder = s.substring(start);
            if (!remainder.isEmpty()) {
                result.add(remainder);
            }
        }
        return result;
    }
}