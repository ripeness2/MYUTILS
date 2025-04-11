package org.ripeness.myutils.utils.textutil;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class Lang {

    private final FileConfiguration config;

    // Constructor
    public Lang(FileConfiguration config) {
        this.config = config;
    }

    // getText method
    public listed getText(String cfloc) {
        if (config.contains(cfloc)) {
            return new listed(config.getStringList(cfloc));
        }
        return new listed(new ArrayList<>());
    }


}
