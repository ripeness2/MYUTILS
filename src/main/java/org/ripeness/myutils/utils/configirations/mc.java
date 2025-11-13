package org.ripeness.myutils.utils.configirations;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.ConcurrentHashMap;

public class mc {

    private Plugin plugin = null;

    public mc(Plugin p) {
        plugin = p;
    }

    public final ConcurrentHashMap<String, multipleConfigurationClass> configurations = new ConcurrentHashMap<>();

    public void init(Plugin pl) {
        plugin = pl;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public multipleConfigurationClass getConfigirationGroup(String name) {
        if (plugin == null) return null;
        if (configurations.containsKey(name)) {
            return configurations.get(name);
        } else {
            return registerMC(name);
        }
    }

    public multipleConfigurationClass registerMC(String name) {
        multipleConfigurationClass m = null;
        if (!configurations.containsKey(name)) {
            m = configurations.put(name, new multipleConfigurationClass(name, this));
        }
        return m;
    }


}
