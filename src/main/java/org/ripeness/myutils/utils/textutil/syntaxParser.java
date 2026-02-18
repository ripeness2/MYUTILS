package org.ripeness.myutils.utils.textutil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class syntaxParser {

    public static Map<String, String> getTags(String input) {
        Map<String, String> tags = new HashMap<>();
        Pattern pattern = Pattern.compile("<(\\w+)=([^>]+)>");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            tags.put(matcher.group(1), matcher.group(2));
        }
        return tags;
    }
}