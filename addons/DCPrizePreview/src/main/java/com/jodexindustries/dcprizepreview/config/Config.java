package com.jodexindustries.dcprizepreview.config;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.addon.external.ExternalAddon;
import com.jodexindustries.donatecase.api.addon.internal.InternalAddon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Config {
    private final File configFile;
    private final YamlConfiguration config = new YamlConfiguration();
    private final Addon addon;
    public final Map<String, CasePreview> previewMap = new HashMap<>();

    public Config(Addon addon) {
        this.addon = addon;
        configFile = new File(addon.getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            if(addon instanceof InternalAddon) {
                ((InternalAddon) addon).saveResource("config.yml", false);
            } else if(addon instanceof ExternalAddon) {
                ((ExternalAddon) addon).getPlugin().saveResource("config.yml", false);
            }
        }
        reload();
    }

    public void load() {
        previewMap.clear();

        ConfigurationSection section = config.getConfigurationSection("cases");
        if(section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection caseSection = section.getConfigurationSection(key);
            if(caseSection == null) continue;

            CasePreview casePreview = CasePreview.deserialize(caseSection);
            previewMap.put(key, casePreview);
        }
    }

    public void reload() {
        reload(false);
    }

    public void reload(boolean message) {
        try {
            config.load(configFile);
            load();
            if(message) addon.getLogger().info("Config reloaded");
        } catch (IOException | InvalidConfigurationException e) {
            addon.getLogger().log(Level.SEVERE, "Failed to reload config", e);
        }
    }
}
