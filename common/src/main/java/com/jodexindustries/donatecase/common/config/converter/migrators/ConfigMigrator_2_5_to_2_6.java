package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigMigrator_2_5_to_2_6 implements ConfigMigrator {

    private final Map<String, String> renameMap = new LinkedHashMap<>();

    public ConfigMigrator_2_5_to_2_6() {
        renameMap.put("UpdateChecker", "update-checker");
        renameMap.put("MySql", "mysql");
        renameMap.put("Languages", "language");
        renameMap.put("HologramDriver", "hologram-driver");
        renameMap.put("LevelGroups", "level-groups");
        renameMap.put("DateFormat", "date-format");
        renameMap.put("AddonsHelp", "addons-help");
        renameMap.put("UsePackets", "use-packets");
        renameMap.put("Caching", "caching");
        renameMap.put("CheckPlayerName", "format-player-name");

        // MySQL nested
        renameMap.put("Enabled", "enabled");
        renameMap.put("Host", "host");
        renameMap.put("Port", "port");
        renameMap.put("DataBase", "database");
        renameMap.put("User", "username");
        renameMap.put("Password", "password");
    }

    @Override
    public void migrate(Config config) throws ConfigurateException {
        ConfigurationNode donateCase = config.node("DonateCase").copy();
        if (!donateCase.virtual()) {
            donateCase.removeChild("DisableSpawnProtection");
            renameKeysRecursively(donateCase);
        }

        ConfigurationNode root = config.node();
        root.removeChild("DonateCase");

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : donateCase.childrenMap().entrySet()) {
            root.node(entry.getKey()).set(entry.getValue());
        }

        root.node("config", "version").set(26);
    }

    private void renameKeysRecursively(ConfigurationNode node) {
        Map<Object, ? extends ConfigurationNode> originalChildren = node.childrenMap();
        Map<String, ConfigurationNode> newChildren = new LinkedHashMap<>();

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : originalChildren.entrySet()) {
            String originalKey = entry.getKey().toString();
            ConfigurationNode originalChild = entry.getValue();

            String newKey = renameMap.getOrDefault(originalKey, originalKey);
            ConfigurationNode newChild = originalChild.copy();

            if (!originalChild.childrenMap().isEmpty()) {
                renameKeysRecursively(newChild);
            }

            newChildren.put(newKey, newChild);
        }

        node.raw(null);

        for (Map.Entry<String, ConfigurationNode> entry : newChildren.entrySet()) {
            node.node(entry.getKey()).raw(entry.getValue().raw());
        }
    }

}
