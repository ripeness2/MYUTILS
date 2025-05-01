package org.ripeness.myutils.utils.event_register;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class ListenerRegistration {
    private final Plugin plugin;
    private final String packageName;
    private boolean initialized = false;
    // eklenen: kayıtlı listener'ları saklayacak liste
    private final List<Listener> registered = new ArrayList<>();

    public ListenerRegistration(Plugin plugin, String packageName) {
        this.plugin = plugin;
        this.packageName = packageName;
    }

    public void registerListeners() {
        if (initialized) {
            plugin.getLogger().warning("Listeners already registered for " + plugin.getName());
            return;
        }
        initialized = true;

        try {
            File jarFile = new File(plugin.getClass().getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            if (jarFile.isFile()) {
                try (JarFile jar = new JarFile(jarFile)) {
                    jar.entries().asIterator().forEachRemaining(entry -> {
                        String name = entry.getName();
                        if (name.endsWith(".class") &&
                                name.startsWith(packageName.replace('.', '/'))) {
                            String className = name
                                    .replace('/', '.')
                                    .substring(0, name.length() - 6);

                            try {
                                Class<?> clazz = Class.forName(className);
                                if (Listener.class.isAssignableFrom(clazz) &&
                                        !clazz.isInterface()) {
                                    Listener listener = (Listener) clazz
                                            .getDeclaredConstructor().newInstance();
                                    // register & kaydet
                                    plugin.getServer()
                                            .getPluginManager()
                                            .registerEvents(listener, plugin);
                                    registered.add(listener);
                                    plugin.getLogger()
                                            .info("Registered listener: " + className);
                                }
                            } catch (Exception e) {
                                plugin.getLogger()
                                        .warning("Failed to register listener " +
                                                className + ": " + e.getMessage());
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            plugin.getLogger()
                    .severe("Failed to register listeners: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Sunucu kapanırken ya da plugin unload olurken çağrılabilir */
    public void unregisterListeners() {
        for (Listener listener : registered) {
            HandlerList.unregisterAll(listener);
            plugin.getLogger()
                    .info("Unregistered listener: " + listener.getClass().getName());
        }
        registered.clear();
        initialized = false;
    }
}

