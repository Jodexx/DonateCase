package com.jodexindustries.donatecase.common.config;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.Loadable;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.common.DonateCase;
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

        for (Map.Entry<String, Config> entry : api.getConfigManager().getConfigCases().getMap().entrySet()) {
            String caseType = entry.getKey();

            ConfigurationNode caseSection = entry.getValue().node("case");

            if (caseSection == null || caseSection.isNull()) {
                api.getPlatform().getLogger().warning("Case " + caseType + " has a broken case section, skipped.");
                continue;
            }

            try {
                CaseData caseData = caseSection.get(CaseData.class);
                if(caseData == null) {
                    api.getPlatform().getLogger().warning("Something wrong with case \"" + caseType + "\" loading!");
                    continue;
                }
                caseData.setCaseType(caseType);

                api.getCaseManager().getMap().put(caseType, caseData);
                count++;
            } catch (SerializationException e) {
                api.getPlatform().getLogger().log(Level.WARNING, "Error with loading case \"" + caseType + "\"", e);
            }

        }

        api.getEventBus().post(new DonateCaseReloadEvent(DonateCaseReloadEvent.Type.CASES));
        api.getPlatform().getLogger().info("Loaded " + count + " cases!");
    }

}