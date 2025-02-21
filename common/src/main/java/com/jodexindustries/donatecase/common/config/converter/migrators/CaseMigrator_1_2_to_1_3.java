package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.common.config.converter.ConfigMigrator;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Map;

public class CaseMigrator_1_2_to_1_3 implements ConfigMigrator {

    @Override
    public void migrate(ConfigurationNode root) throws SerializationException {

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : root.node("case", "Gui", "Items")
                .childrenMap().entrySet()) {

            // get
            ConfigurationNode node = entry.getValue();
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

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : root.node("case", "Items")
                .childrenMap().entrySet()) {
            ConfigurationNode node = entry.getValue();

            // get
            ConfigurationNode itemNode = node.node("Item");

            // clear
            node.removeChild("Item");

            // set
            node.node("Material").set(itemNode);
        }

        root.node("config").set("1.3");
    }
}