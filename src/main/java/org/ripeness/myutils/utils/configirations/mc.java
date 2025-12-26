package org.ripeness.myutils.utils.configirations;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

public class mc {

    private final Plugin plugin;
    private String basePath = null;

    public mc(Plugin plugin, @Nullable String basePath) {
        this.plugin = plugin;
        if (basePath != null) {
            this.basePath = basePath;
        } else {
            this.basePath = "myconfigs/" + plugin.getName() + "/";
        }
    }

    public mc(Plugin plugin) {
        this.plugin = plugin;
        this.basePath = "myconfigs/" + plugin.getName() + "/";
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

    public String getBasePath() {
        return basePath;
    }
}
