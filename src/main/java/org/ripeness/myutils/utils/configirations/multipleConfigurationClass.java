package org.ripeness.myutils.utils.configirations;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ripeness.myutils.utils.objsconf.objectmapManager;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class multipleConfigurationClass {
    private final String child;
    private final ConcurrentHashMap<String, FileConfiguration> fileConfigurationMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, File> fileMap = new ConcurrentHashMap<>();

    // constructor
    public multipleConfigurationClass(String child) {
        this.child = child;
        new File(mc.getPlugin().getDataFolder(), this.child).mkdir();
    }

    public Map<String, File> getFileMap() {
        return fileMap;
    }

    public Map<String, FileConfiguration> getFileConfigurationMap() {
        return fileConfigurationMap;
    }

    private String getChild() {
        return child;
    }

    public List<String> getAll() {
        if (mc.getPlugin() == null) return null;
        List<String> l = new ArrayList<>();
        new File(mc.getPlugin().getDataFolder(), getChild()).mkdir();
        l = Arrays.stream(Objects.requireNonNull(new File(mc.getPlugin().getDataFolder(), getChild() + "/").listFiles())).map(File::getName).collect(Collectors.toList());
        l = l.stream().map(s -> s.replace(".yml", "")).collect(Collectors.toList());
        return l;
    }

    public void parseAll(boolean save, BiFunction<String, FileConfiguration, Void> afterParse) {
        if (mc.getPlugin() == null) return;
        if (save) {
            for (Map.Entry<String, FileConfiguration> t : fileConfigurationMap.entrySet()) {
                String key = t.getKey();
                FileConfiguration value = t.getValue();
                File file = fileMap.get(key);
                if (new File(mc.getPlugin().getDataFolder(), getChild() + "/" + key + ".yml").exists()) {
                    try {
                        value.save(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        fileConfigurationMap.clear();
        fileMap.clear();

        File[] list = new File(mc.getPlugin().getDataFolder(), getChild() + "/").listFiles();
        if (list == null) return;
        for (File file : list) {
            String key = file.getName().replace(".yml", "");
            fileMap.put(key, file);
            fileConfigurationMap.put(key, YamlConfiguration.loadConfiguration(file));
            if (afterParse != null) afterParse.apply(key, fileConfigurationMap.get(key));
        }
    }

    public FileConfiguration getConfig(String key) {
        if (mc.getPlugin() == null) return null;
        FileConfiguration orDefault = fileConfigurationMap.getOrDefault(key, null);
        if (orDefault == null) {
            File file = new File(mc.getPlugin().getDataFolder(), getChild() + "/" + key + ".yml");
            if (!file.exists()) return null;
            fileMap.put(key, file);
            FileConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            fileConfigurationMap.put(key, yamlConfiguration);
            orDefault = yamlConfiguration;
        }
        return orDefault;
    }

    public boolean notExistCreate(String key, Map<String, Object> data) {
        if (mc.getPlugin() == null) return false;
        boolean exists = new File(mc.getPlugin().getDataFolder(), getChild() + "/" + key + ".yml").exists();
        if (!exists) {
            new File(mc.getPlugin().getDataFolder(), getChild()).mkdirs();
            try {
                new File(mc.getPlugin().getDataFolder(), getChild() + "/" + key + ".yml").createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            FileConfiguration cf = getConfig(key);
            cf.set("key", key);
            cf.set("objects", objectmapManager.serializeMap(data));
            try {
                cf.save(new File(mc.getPlugin().getDataFolder(), getChild() + "/" + key + ".yml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    public void save(String key) {
        if (mc.getPlugin() == null) return;
        FileConfiguration cf = fileConfigurationMap.get(key);
        if (cf == null) {
            cf = getConfig(key);
        }
        if (cf == null) return;
        try {
            cf.save(new File(mc.getPlugin().getDataFolder(), getChild() + "/" + key + ".yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
