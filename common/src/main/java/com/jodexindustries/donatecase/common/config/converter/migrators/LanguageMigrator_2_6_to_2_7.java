package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageMigrator_2_6_to_2_7 implements ConfigMigrator {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(%\\w+)(?!%)");

    @Override
    public void migrate(Config config) throws SerializationException {
        updatePlaceholders(config.node());

        config.node("config", "version").set(27);
    }

    private static void updatePlaceholders(ConfigurationNode node) throws SerializationException {
        if (node.isMap()) {
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
                updatePlaceholders(entry.getValue());
            }
        } else if (node.isList()) {
            for (ConfigurationNode child : node.childrenList()) {
                updatePlaceholders(child);
            }
        } else {
            Object rawValue = node.raw();
            if (rawValue instanceof String) {
                String value = (String) rawValue;

                Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
                String updatedValue = matcher.replaceAll("$1%");

                if (!value.equals(updatedValue)) {
                    node.set(updatedValue);
                }
            }
        }
    }
}
