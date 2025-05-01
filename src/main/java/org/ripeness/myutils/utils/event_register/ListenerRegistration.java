package org.ripeness.myutils.utils.event_register;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.jar.JarFile;

//SATIR = 10
public class ListenerRegistration {
    private final Plugin plugin;
    private final String packageName;
    private boolean initialized = false; // Tekrar yüklemeyi önlemek için kontrol bayrağı

    public ListenerRegistration(Plugin plugin, String packageName, boolean initialized) {
        this.plugin = plugin;
        this.packageName = packageName;
        this.initialized = initialized; // Tekrar yüklemeyi önlemek için kontrol bayrağı
    }

    public void registerListeners() {
        if (initialized) {
            plugin.getLogger().warning("Listeners have already been registered! / for -> " + plugin.getName());
            return; // Zaten yüklüyse işlemi sonlandır
        }

        initialized = true; // Tekrar çalışmayı önlemek için işaretle

        try {
            File jarFile = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

            if (jarFile.isFile()) {
                try (JarFile jar = new JarFile(jarFile)) {
                    jar.entries().asIterator().forEachRemaining(entry -> {
                        String name = entry.getName();
                        if (name.endsWith(".class") && name.startsWith(packageName.replace('.', '/'))) {
                            String className = name.replace('/', '.').substring(0, name.length() - 6);

                            try {
                                Class<?> clazz = Class.forName(className);
                                if (Listener.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                                    Listener listener = (Listener) clazz.getDeclaredConstructor().newInstance();
                                    plugin.getServer().getPluginManager().registerEvents(listener, plugin);
                                    plugin.getLogger().info("Registered listener: " + className + " / for -> " + plugin.getName());
                                }
                            } catch (Exception e) {
                                plugin.getLogger().warning("Failed to register listener " + className + ": " + e.getMessage() + " / for -> " + plugin.getName());
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register listeners: " + e.getMessage() + " / for -> " + plugin.getName());
            e.printStackTrace();
        }
    }
}
