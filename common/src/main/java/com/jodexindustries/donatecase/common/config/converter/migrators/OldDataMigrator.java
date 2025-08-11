package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import com.jodexindustries.donatecase.api.config.converter.ConvertOrder;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.common.DonateCase;
import com.jodexindustries.donatecase.common.database.CaseDatabaseImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Map;
import java.util.logging.Logger;

public class OldDataMigrator implements ConfigMigrator {

    @Override
    public void migrate(Config config) throws ConfigurateException {
        DonateCase donateCase = ((DonateCase) DonateCase.getInstance());
        Logger logger = donateCase.getPlatform().getLogger();

        CaseDatabaseImpl database = donateCase.getDatabase();

        ConfigurationNode data = config.node("Data");

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : data.childrenMap().entrySet()) {
            String caseType = String.valueOf(entry.getKey());

            for (Map.Entry<Object, ? extends ConfigurationNode> historyEntry : entry.getValue().childrenMap().entrySet()) {
                int index = Integer.parseInt(String.valueOf(historyEntry.getKey()));
                ConfigurationNode value = historyEntry.getValue();

                CaseData.History history = new CaseData.History(
                        value.node("Item").getString(),
                        caseType,
                        value.node("Player").getString(),
                        value.node("Time").getLong(),
                        value.node("Group").getString(),
                        value.node("Action").getString()
                );

                database.setHistoryData(caseType, index, history).thenAccept(status -> {
                    if (status != DatabaseStatus.COMPLETE)
                        logger.warning("Couldn't convert the case type: " + caseType + " history to " + database.getType() + " database!");
                });
            }
        }

        ConfigurationNode open = config.node("Open");

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : open.childrenMap().entrySet()) {
            String player = String.valueOf(entry.getKey());

            for (Map.Entry<Object, ? extends ConfigurationNode> caseEntry : entry.getValue().childrenMap().entrySet()) {
                String caseType = String.valueOf(caseEntry.getKey());
                int openCount = caseEntry.getValue().getInt();

                database.setCount(caseType, player, openCount).thenAccept(status -> {
                    if (status != DatabaseStatus.COMPLETE)
                        logger.warning("Couldn't convert the case type: " + caseType + " open count to " + database.getType() + " database!");
                });
            }
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
        boolean can = api.getConfigManager().getConfig().converter().data();
        if (!can) {
            api.getPlatform().getLogger().warning(
                    "Data.yml conversion is disabled in Config.yml (converter.data) " +
                            "If you do not want to convert it, simply delete the Data.yml file to avoid seeing this message."
            );
        }

        return can;
    }
}
