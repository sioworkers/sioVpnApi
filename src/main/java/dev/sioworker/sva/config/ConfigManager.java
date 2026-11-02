package dev.sioworker.sva.config;

import dev.sioworker.sva.SvaPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ConfigManager {

    private final SvaPlugin plugin;
    private final File configFile;
    private final Map<String, Object> defaults = new LinkedHashMap<>();
    private FileConfiguration config;

    public ConfigManager(SvaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    public void addEntry(String path, Object defaultValue) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Config path cannot be blank");
        }

        defaults.put(path, Objects.requireNonNull(defaultValue, "defaultValue"));

        if (config != null && !hasValidValue(config, path, defaultValue)) {
            config.set(path, defaultValue);
            save();
        }
    }

    public void load() {
        FileConfiguration loaded = new YamlConfiguration();
        boolean regenerate = false;

        if (configFile.isFile()) {
            try {
                loaded.load(configFile);
            } catch (IOException | InvalidConfigurationException exception) {
                plugin.getLogger().warning("Invalid config.yml; regenerating it from registered defaults.");
                regenerate = true;
            }
        }

        if (regenerate) {
            loaded = new YamlConfiguration();
            defaults.forEach(loaded::set);
            config = loaded;
            save();
            return;
        }

        boolean changed = false;
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            if (!hasValidValue(loaded, entry.getKey(), entry.getValue())) {
                loaded.set(entry.getKey(), entry.getValue());
                changed = true;
            }
        }

        config = loaded;
        if (changed || !configFile.exists()) {
            save();
        }
    }

    public void reload() {
        load();
    }

    public void save() {
        if (config == null) {
            return;
        }

        try {
            config.save(configFile);
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not save config.yml: " + exception.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            load();
        }
        return config;
    }

    public String getString(String path) {
        return getConfig().getString(path);
    }

    public String getString(String path, String defaultValue) {
        return getConfig().getString(path, defaultValue);
    }

    public boolean getBoolean(String path) {
        return getConfig().getBoolean(path);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return getConfig().getBoolean(path, defaultValue);
    }

    public int getInt(String path) {
        return getConfig().getInt(path);
    }

    public int getInt(String path, int defaultValue) {
        return getConfig().getInt(path, defaultValue);
    }

    public List<String> getStringList(String path) {
        return getConfig().getStringList(path);
    }

    private boolean hasValidValue(FileConfiguration source, String path, Object defaultValue) {
        if (!source.contains(path)) {
            return false;
        }

        Object value = source.get(path);
        if (defaultValue instanceof Number) {
            return value instanceof Number;
        }
        return value != null && defaultValue.getClass().isInstance(value);
    }
}
