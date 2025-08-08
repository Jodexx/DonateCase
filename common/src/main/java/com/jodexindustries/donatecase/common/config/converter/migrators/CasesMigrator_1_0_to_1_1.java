package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.Map;

public class CasesMigrator_1_0_to_1_1 implements ConfigMigrator {

    @Override
    public void migrate(Config config) throws SerializationException {
        ConfigurationNode root = config.node("DonateCase", "Cases");

        Map<String, CaseInfo> cases = new HashMap<>();

        for (ConfigurationNode node : root.childrenMap().values()) {
            String key = String.valueOf(node.key());
            String caseType = node.node("type").getString();
            String[] location = node.node("location").getString("").split(";");

            if (location.length < 4) continue;

            CaseInfo caseInfo = getCaseInfo(location, caseType);

            cases.put(key, caseInfo);
        }

        config.node().removeChild("DonateCase");

        for (Map.Entry<String, CaseInfo> entry : cases.entrySet()) {
            root.node(entry.getKey()).set(entry.getValue());
        }

        config.node("config", "version").set(11);
    }

    private static @NotNull CaseInfo getCaseInfo(String[] location, String caseType) {
        String world = location[0];
        double x = Double.parseDouble(location[1]);
        double y = Double.parseDouble(location[2]);
        double z = Double.parseDouble(location[3]);
        float pitch = location.length > 4 ? Float.parseFloat(location[4]) : 0;
        float yaw = location.length > 5 ? Float.parseFloat(location[5]) : 0;

        CaseLocation caseLocation = new CaseLocation(world, x, y, z, pitch, yaw);

        return new CaseInfo(caseType, caseLocation);
    }
}
