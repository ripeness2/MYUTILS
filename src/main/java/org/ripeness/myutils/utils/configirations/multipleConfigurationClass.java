package org.ripeness.myutils.utils.configirations;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ripeness.myutils.utils.objsconf.objectmapManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class multipleConfigurationClass {

    private final String child;      // Alt klasör adı
    private final mc owner;          // Bu config sınıfı hangi mc instance’ına ait?

    private final ConcurrentHashMap<String, FileConfiguration> fileConfigurationMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, File> fileMap = new ConcurrentHashMap<>();

    private final File rootFolder;

    public multipleConfigurationClass(String child, mc owner) {
        this.child = child;
        this.owner = owner;

        // HER PLUGIN İÇİN KENDİ AYRI KLASÖRÜ:
        this.rootFolder = new File(
                owner.getPlugin().getDataFolder(),
                !owner.getBasePath().isEmpty() ? (owner.getBasePath() + "/") : this.child
        );

        rootFolder.mkdirs();
    }


    private File configFile(String key) {
        return new File(rootFolder, key + ".yml");
    }

    public List<String> getAll() {
        File[] list = rootFolder.listFiles();
        if (list == null) return new ArrayList<>();

        List<String> names = new ArrayList<>();
        for (File f : list) {
            if (f.getName().endsWith(".yml"))
                names.add(f.getName().replace(".yml", ""));
        }

        return names;
    }

    public void parseAll(boolean save, BiFunction<String, FileConfiguration, Void> afterParse) {
        if (save) {
            for (Map.Entry<String, FileConfiguration> t : fileConfigurationMap.entrySet()) {
                try {
                    t.getValue().save(fileMap.get(t.getKey()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        fileConfigurationMap.clear();
        fileMap.clear();

        File[] list = rootFolder.listFiles();
        if (list == null) return;

        for (File f : list) {
            if (!f.getName().endsWith(".yml")) continue;

            String key = f.getName().replace(".yml", "");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);

            fileMap.put(key, f);
            fileConfigurationMap.put(key, cfg);

            if (afterParse != null) afterParse.apply(key, cfg);
        }
    }

    public FileConfiguration getConfig(String key) {
        if (fileConfigurationMap.containsKey(key)) {
            return fileConfigurationMap.get(key);
        }

        File file = configFile(key);
        if (!file.exists()) return null;

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        fileConfigurationMap.put(key, cfg);
        fileMap.put(key, file);

        return cfg;
    }

    public boolean notExistCreate(String key, Map<String, Object> data) {
        File f = configFile(key);
        if (f.exists()) return false;

        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileConfiguration cfg = getConfig(key);
        cfg.set("key", key);
        cfg.set("objects", objectmapManager.serializeMap(data));

        try {
            cfg.save(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public void save(String key) {
        FileConfiguration cfg = getConfig(key);
        if (cfg == null) return;

        try {
            cfg.save(configFile(key));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
