package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.config.CaseStorage;
import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

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
    public void save(@NotNull String name, @NotNull CaseInfo caseInfo) throws ConfigurateException {
        ConfigurationNode current = node.node("DonateCase", "Cases", name);
        current.set(CaseInfo.class, caseInfo);
        config.save("Cases.yml");
    }

    @Override
    public void delete(String name) {
        node.node("DonateCase", "Cases").removeChild(name);
        config.save("Cases.yml");
    }

    @Override
    public boolean delete(CaseLocation location) {
        ConfigurationNode parent = node.node("DonateCase", "Cases");
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : parent.childrenMap().entrySet()) {
            try {
                CaseInfo caseInfo = entry.getValue().get(CaseInfo.class);
                if (caseInfo == null) continue;

                if (location.equals(caseInfo.getLocation())) {
                    parent.removeChild(entry.getKey());
                    config.save("Cases.yml");
                    return true;
                }
            } catch (SerializationException ignored) {}

        }

        return false;
    }

    @Override
    public @NotNull Map<String, CaseInfo> get() {
        Map<String, CaseInfo> map = new HashMap<>();
        ConfigurationNode parent = node.node("DonateCase", "Cases");
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : parent.childrenMap().entrySet()) {
            String name = String.valueOf(entry.getKey());
            CaseInfo caseInfo = get(entry.getValue());

            if (caseInfo != null) map.put(name, caseInfo);
        }

        return map;
    }

    @Override
    public boolean has(String name) {
        return node.hasChild("DonateCase", "Cases", name);
    }

    @Override
    public CaseInfo get(String name) {
        return get(node.node("DonateCase", "Cases", name));
    }

    @Override
    public CaseInfo get(CaseLocation location) {
        ConfigurationNode parent = node.node("DonateCase", "Cases");
        for (ConfigurationNode value : parent.childrenMap().values()) {

            CaseInfo caseInfo = get(value);
            if (caseInfo != null) if (location.equals(caseInfo.getLocation())) return caseInfo;
        }

        return null;
    }

    @Nullable
    private CaseInfo get(ConfigurationNode node) {
        try {
            return node.get(CaseInfo.class);
        } catch (SerializationException e) {
            DCAPI.getInstance().getPlatform().getLogger().log(Level.WARNING, "Error with getting info about case: " + node.key(), e);
        }

        return null;
    }

}