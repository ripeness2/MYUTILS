package org.ripeness.myutils.utils.etc;

import org.bukkit.plugin.Plugin;

public class pluginsUtil {

    public static boolean checkPlugin(String pluginName, Plugin plugin) {
        return plugin.getServer().getPluginManager().getPlugin(pluginName) != null;
    }

    public static boolean isPluginActive(String pluginName, Plugin plugin) {
        return plugin.getServer().getPluginManager().isPluginEnabled(pluginName);
    }

}
