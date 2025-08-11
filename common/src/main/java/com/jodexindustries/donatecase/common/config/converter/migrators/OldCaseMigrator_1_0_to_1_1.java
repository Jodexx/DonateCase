package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Arrays;
import java.util.List;

public class OldCaseMigrator_1_0_to_1_1 implements ConfigMigrator {

    @Override
    public void migrate(Config config) throws SerializationException {
        ConfigurationNode root = config.node();

        List<String> list = Arrays.asList(
                "[message] &cYou don't have keys for this case. You can buy them here >>> &6www.jodexindustries.xyz",
                "[sound] ENTITY_ENDERMAN_TELEPORT"
        );

        root.node("case", "NoKeyActions").set(list);
    }

}
