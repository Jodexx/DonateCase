package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import com.jodexindustries.donatecase.api.config.converter.ConvertOrder;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.common.DonateCase;
import com.jodexindustries.donatecase.common.database.CaseDatabaseImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class OldKeysMigrator implements ConfigMigrator {

    @Override
    public void migrate(Config config) throws ConfigurateException {
        ConfigurationNode root = getRoot(config);
        if (root.empty()) {
            config.delete();
            return;
        }

        DonateCase donateCase = ((DonateCase) DonateCase.getInstance());
        Logger logger = donateCase.getPlatform().getLogger();

        CaseDatabaseImpl database = donateCase.getDatabase();

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : root.childrenMap().entrySet()) {
            String caseType = String.valueOf(entry.getKey());

            Map<String, Integer> playerKeysMap = new HashMap<>();

            for (Map.Entry<Object, ? extends ConfigurationNode> playerEntry : entry.getValue().childrenMap().entrySet()) {
                String player = String.valueOf(playerEntry.getKey());
                int keys = playerEntry.getValue().getInt();

                playerKeysMap.put(player, keys);
            }

            database.setKeysBulk(caseType, playerKeysMap).thenAccept(status -> {
                if (status != DatabaseStatus.COMPLETE)
                    logger.warning("Couldn't convert the case type: " + caseType + " keys to " + database.getType() + " database!");
            });
        }

        config.delete();
    }

    @Override
    public @NotNull ConvertOrder order() {
        return ConvertOrder.ON_DATABASE;
    }

    @Override
    public boolean canMigrate() {
        DCAPI api = DCAPI.getInstance();
        boolean can = api.getConfigManager().getConfig().converter().keys();
        if (!can) {
            api.getPlatform().getLogger().warning(
                    "Keys.yml conversion is disabled in Config.yml (converter.keys) " +
                            "If you do not want to convert it, simply delete the Keys.yml file to avoid seeing this message."
            );
        }

        return can;
    }

    private ConfigurationNode getRoot(Config config) {
        ConfigurationNode donateCase = config.node("DonateCase", "Cases");
        if (!donateCase.virtual()) return donateCase;

        return config.node("DonatCase", "Cases");
    }
}
