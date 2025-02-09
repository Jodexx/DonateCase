package com.jodexindustries.donatecase.common.config.converter;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.common.config.ConfigManagerImpl;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Map;
import java.util.logging.Level;

public class ConfigConverter {

    private final ConfigManagerImpl config;

    public ConfigConverter(ConfigManagerImpl config) {
        this.config = config;
    }

    public void convert() {

        // Convert cases
        for (Map.Entry<String, Config> entry : config.getConfigCases().getMap().entrySet()) {
            try {
                convert("Case: " + entry.getKey(), ConfigType.CASE, entry.getValue());
            } catch (ConfigurateException e) {
                config.getPlatform().getLogger().log(Level.WARNING, "Error with converting case: " + entry.getKey(), e);
            }
        }
    }

    private void convert(String name, ConfigType type, Config config) throws ConfigurateException, IllegalArgumentException {
        ConfigurationNode versionNode = config.node("config");
        int version = getVersion(versionNode);

        if (version == type.getLatestVersion()) return;

        while (version < type.getLatestVersion()) {
            ConfigMigrator migrator = type.getMigrator(version);
            if (migrator == null) break;

            this.config.getPlatform().getLogger().info(name + " converting...");
            migrator.migrate(config.node());
            this.config.getPlatform().getLogger().info(name + " converted from " + version + " to " + version++);
        }

        versionNode.set(version);
        config.save();
    }

    private int getVersion(ConfigurationNode versionNode) {
        String version = versionNode.getString();
        if(version == null) return versionNode.getInt();

        if(version.contains(".")) version = version.replace(".", "");
        return Integer.parseInt(version);
    }
}
