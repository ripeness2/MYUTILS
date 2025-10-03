package org.ripeness.myutils.utils.textutil;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lang {

    private final FileConfiguration config;
    private final String prefix;

    // Constructor
    public Lang(FileConfiguration config, String prefix) {
        this.config = config;
        this.prefix = prefix;
    }

    // getText method
    public listed getText(String cfloc) {
        cfloc = prefix + "." + cfloc;
        if (config.contains(cfloc)) {
            List<String> stringList = config.getStringList(cfloc);
            if (!config.isList(cfloc) && config.isSet(cfloc)) {
                stringList = Collections.singletonList(config.getString(cfloc));
            }
            return new listed(stringList);
        }
        return new listed(new ArrayList<>());
    }


}
