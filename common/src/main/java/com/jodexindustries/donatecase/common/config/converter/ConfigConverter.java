package com.jodexindustries.donatecase.common.config.converter;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import com.jodexindustries.donatecase.api.config.converter.ConfigType;
import com.jodexindustries.donatecase.common.config.ConfigManagerImpl;
import org.spongepowered.configurate.ConfigurateException;

import java.util.ArrayList;
import java.util.logging.Level;

public class ConfigConverter {

    private final ConfigManagerImpl configManager;

    public ConfigConverter(ConfigManagerImpl configManager) {
        this.configManager = configManager;
    }

    public void convert() {
        for (Config config : new ArrayList<>(this.configManager.get().values())) {
            try {
                convert(config);
            } catch (ConfigurateException e) {
                this.configManager.getPlatform().getLogger().log(Level.WARNING, "Error with converting configuration: " + config, e);
            }
        }
    }

    public void convert(Config config) throws ConfigurateException, IllegalArgumentException {
        int version = config.version();
        ConfigType currentType = config.type();

        if (version == currentType.getLatestVersion() && !currentType.isPermanent()) return;

        while (version < currentType.getLatestVersion() || currentType.isPermanent()) {
            ConfigMigrator migrator = currentType.getMigrator(version);
            if (migrator == null) break;

            this.configManager.getPlatform().getLogger().info(config + " converting...");
            migrator.migrate(config);
            if(currentType.isPermanent()) {
                this.configManager.getPlatform().getLogger().info(config + " converted permanently from " + currentType + " to " + config.type());
                convert(config);
                break;
            }
            this.configManager.getPlatform().getLogger().info(config + " converted from " + version + " to " + ++version);
        }

        config.save();
    }
}