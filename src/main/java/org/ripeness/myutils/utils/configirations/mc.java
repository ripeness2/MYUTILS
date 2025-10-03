package org.ripeness.myutils.utils.configirations;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.ConcurrentHashMap;

public class mc {
    private static Plugin plugin = null;
    public static final ConcurrentHashMap<String, multipleConfigurationClass> configurations = new ConcurrentHashMap<>();

    public static void init(Plugin pl) {
        plugin = pl;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static multipleConfigurationClass getConfigirationGroup(String name) {
        if (plugin == null) return null;
        if (configurations.containsKey(name)) {
            return configurations.get(name);
        } else {
            return registerMC(name);
        }
    }

    public static multipleConfigurationClass registerMC(String name) {
        multipleConfigurationClass m = null;
        if (!configurations.containsKey(name)) {
            m = configurations.put(name, new multipleConfigurationClass(name));
        }
        return m;
    }


}
