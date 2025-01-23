package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.config.Loadable;
import com.jodexindustries.donatecase.api.data.casedata.*;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Map;
import java.util.logging.Level;

public class CaseLoader implements Loadable {

    private final DonateCase api;

    public CaseLoader(DonateCase api) {
        this.api = api;
    }

    @Override
    public void load() {
        api.getCaseManager().getMap().clear();
        int count = 0;

        for (Map.Entry<String, ConfigurationNode> entry : api.getConfig().getConfigCases().getMap().entrySet()) {
            String caseType = entry.getKey();
            ConfigurationNode node = entry.getValue();

            ConfigurationNode caseSection = node.node("case");

            if (caseSection == null) {
                api.getPlatform().getLogger().warning("Case " + caseType + " has a broken case section, skipped.");
                continue;
            }

            CaseData caseData;
            try {
                caseData = caseSection.get(CaseData.class);
            } catch (SerializationException e) {
                api.getPlatform().getLogger().log(Level.WARNING, "Error with loading case \"" + caseType + "\"", e);
                continue;
            }

            api.getCaseManager().getMap().put(caseType, caseData);
            count++;
        }

        api.getPlatform().getLogger().info("Loaded " + count + " cases!");
    }

}