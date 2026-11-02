package dev.sioworker.sva;

import dev.sioworker.sva.config.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SvaPlugin extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.addEntry("debug", false);
        configManager.load();
        getLogger().info("SVA Enabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
