package com.jodexindustries.donatecase.common.config;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.Loadable;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.common.DonateCase;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        for (Map.Entry<String, List<Config>> entry : getCases().entrySet()) {
            String caseType = entry.getKey();

            for (Config config : entry.getValue()) {
                ConfigurationNode caseSection = config.node("case");

                if (caseSection == null || caseSection.isNull()) {
                    api.getPlatform().getLogger().warning("Case " + caseType + " has a broken case section, skipped.");
                    continue;
                }

                try {
                    CaseData caseData = caseSection.get(CaseData.class);
                    if (caseData == null) {
                        api.getPlatform().getLogger().warning("Something wrong with case \"" + caseType + "\" loading!");
                        continue;
                    }
                    caseData.caseType(caseType);

                    api.getCaseManager().getMap().put(caseType, caseData);
                } catch (SerializationException e) {
                    api.getPlatform().getLogger().log(Level.WARNING, "Error with loading case \"" + caseType + "\"", e);
                }
            }

        }

        api.getEventBus().post(new DonateCaseReloadEvent(DonateCaseReloadEvent.Type.CASES));
        api.getPlatform().getLogger().info("Loaded " + api.getCaseManager().getMap().size() + " cases!");
    }

    private Map<String, List<Config>> getCases() {
        Map<String, List<Config>> cases = new HashMap<>();

        for (Map.Entry<String, ? extends Config> entry : api.getConfigManager().get().entrySet()) {
            String path = entry.getKey();

            // plugins/DonateCase/cases/case_type/another_files.yml
            String[] parts = path.split("/");

            if (parts.length >= 4) {
                if (!parts[2].equals("cases")) continue;
                String caseType = parts[3];

                cases.computeIfAbsent(caseType, k -> new ArrayList<>()).add(entry.getValue());
            }
        }

        return cases;
    }

}