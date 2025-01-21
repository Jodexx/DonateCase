package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.api.config.CaseStorage;
import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.Map;

public class CaseStorageImpl implements CaseStorage {

    private final Config config;
    private ConfigurationNode node;

    public CaseStorageImpl(Config config) {
        this.config = config;
    }

    @Override
    public void load() {
        node = config.get("Cases.yml");
    }

    @Override
    public void save(@NotNull CaseInfo caseInfo) throws ConfigurateException {
        // TODO Hologram manager support
        ConfigurationNode current = node.node("DonateCase", "Cases", caseInfo.getName());
        current.set(CaseInfo.class, caseInfo);
        config.save("Cases.yml");
    }

    @Override
    public void delete(String name) {
        node.node("DonateCase", "Cases").removeChild(name);
        config.save("Cases.yml");
    }

    @Override
    public @NotNull Map<String, CaseInfo> get() {
        Map<String, CaseInfo> map = new HashMap<>();
        ConfigurationNode parent =  node.node("DonateCase", "Cases");
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : parent.childrenMap().entrySet()) {
            String name = String.valueOf(entry.getKey());
            CaseInfo caseInfo = null;
            try {
                caseInfo = entry.getValue().get(CaseInfo.class);
            } catch (SerializationException ignored) {}

            if(caseInfo != null) map.put(name, caseInfo);
        }

        return map;
    }

    @Override
    public boolean has(String name) {
        return node.hasChild("DonateCase", "Cases", name);
    }

    @Override
    public CaseInfo get(String name) {
        try {
            return node.node("DonateCase", "Cases", name).get(CaseInfo.class);
        } catch (SerializationException ignored) {}

        return null;
    }

    @Override
    public CaseInfo get(CaseLocation location) {
        ConfigurationNode parent =  node.node("DonateCase", "Cases");
        for (ConfigurationNode value : parent.childrenMap().values()) {
            CaseInfo caseInfo = null;

            try {
                caseInfo = value.get(CaseInfo.class);
            } catch (SerializationException ignored) {}

            if(caseInfo != null) if(location.equals(caseInfo.getLocation())) return caseInfo;
        }

        return null;
    }


}
