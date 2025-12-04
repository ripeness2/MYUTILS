package org.ripeness.myutils.utils.configirations;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.ConcurrentHashMap;

public class mc {

    private final Plugin plugin;

    public mc(Plugin plugin) {
        this.plugin = plugin;
    }

    private final ConcurrentHashMap<String, multipleConfigurationClass> configurations = new ConcurrentHashMap<>();

    public Plugin getPlugin() {
        return plugin;
    }

    public multipleConfigurationClass getConfigirationGroup(String name) {
        return configurations.computeIfAbsent(
                name,
                n -> new multipleConfigurationClass(n, this)
        );
    }
}
