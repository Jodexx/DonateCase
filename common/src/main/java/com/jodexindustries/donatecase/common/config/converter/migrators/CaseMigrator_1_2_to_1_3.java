package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.common.config.ConfigImpl;
import com.jodexindustries.donatecase.common.config.converter.ConfigMigrator;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class CaseMigrator_1_2_to_1_3 implements ConfigMigrator {

    @Override
    public void migrate(ConfigImpl config) throws SerializationException {
        ConfigurationNode root = config.node();

        for (ConfigurationNode node : root.node("case", "Gui", "Items")
                .childrenMap().values()) {
            migrateGuiItem(node);
        }

        for (ConfigurationNode node : root.node("case", "Items")
                .childrenMap().values()) {
            migrateItem(node);
        }

        root.removeChild("config");

        root.node("config", "version").set(13);
        root.node("config", "type").set(config.type());

        root.node("case", "CooldownBeforeAnimation").set(0);
    }

    private static void migrateItem(ConfigurationNode node) {
        // get
        ConfigurationNode itemNode = node.node("Item").copy();

        // clear
        node.removeChild("Item");

        // set
        node.node("Material").from(itemNode);
    }

    private static void migrateGuiItem(ConfigurationNode node) throws SerializationException {
        // get
        Object displayName = node.node("DisplayName").raw();
        Object enchanted = node.node("Enchanted").raw();
        Object lore = node.node("Lore").raw();
        Object material = node.node("Material").raw();

        // clear
        node.removeChild("DisplayName");
        node.removeChild("Enchanted");
        node.removeChild("Lore");
        node.removeChild("Material");

        // set
        ConfigurationNode materialNode = node.node("Material");
        materialNode.node("ID").set(material);
        materialNode.node("DisplayName").set(displayName);
        materialNode.node("Enchanted").set(enchanted);
        materialNode.node("Lore").set(lore);
    }
}