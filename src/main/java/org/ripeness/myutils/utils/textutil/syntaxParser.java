package org.ripeness.myutils.utils.textutil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class syntaxParser {

    public static Map<String, String> getTags(String input) {
        Map<String, String> tags = new HashMap<>();
        // Sadece <anahtar= kısmını bulmak için basit bir başlangıç
        Pattern pattern = Pattern.compile("<(\\w+)=");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String key = matcher.group(1);
            int startPos = matcher.end(); // Değerin başladığı yer
            int braceCount = 1; // < ile başladık
            StringBuilder value = new StringBuilder();

            // Karakter karakter ilerleyerek doğru kapatma parantezini bulalım
            for (int i = startPos; i < input.length(); i++) {
                char c = input.charAt(i);
                if (c == '<') braceCount++;
                if (c == '>') braceCount--;

                if (braceCount == 0) {
                    // Eşleşen dış parantezi bulduk
                    tags.put(key, value.toString());
                    break;
                } else {
                    value.append(c);
                }
            }
        }
        return tags;
    }
}