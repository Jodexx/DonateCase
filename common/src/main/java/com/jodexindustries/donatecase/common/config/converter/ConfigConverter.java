package com.jodexindustries.donatecase.common.config.converter;

import com.jodexindustries.donatecase.common.config.ConfigImpl;
import com.jodexindustries.donatecase.common.config.ConfigManagerImpl;
import org.spongepowered.configurate.ConfigurateException;

import java.util.logging.Level;

public class ConfigConverter {

    private final ConfigManagerImpl configManager;

    public ConfigConverter(ConfigManagerImpl configManager) {
        this.configManager = configManager;
    }

    public void convert() {
        for (ConfigImpl config : this.configManager.get().values()) {
            try {
                if (config.type() != ConfigType.UNKNOWN_CUSTOM) convert(config);
            } catch (ConfigurateException e) {
                this.configManager.getPlatform().getLogger().log(Level.WARNING, "Error with converting configuration: " + config, e);
            }
        }
    }

    private void convert(ConfigImpl config) throws ConfigurateException, IllegalArgumentException {
        int version = config.version();
        ConfigType type = config.type();

        if (version == type.getLatestVersion() && !type.isPermanent()) return;

        while (version < type.getLatestVersion() || type.isPermanent()) {
            ConfigMigrator migrator = type.getMigrator(version);
            if (migrator == null) break;

            this.configManager.getPlatform().getLogger().info(config + " converting...");
            migrator.migrate(config);
            if(type.isPermanent()) {
                this.configManager.getPlatform().getLogger().info(config + " converted permanently from UNKNOWN to " + config.type());
                convert(config);
                break;
            }
            this.configManager.getPlatform().getLogger().info(config + " converted from " + version + " to " + ++version);
        }

        config.save();
    }
}