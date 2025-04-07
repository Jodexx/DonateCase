package com.jodexindustries.donatecase.common.config;

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

    private final ConfigManagerImpl configManager;
    private Config config;

    public CaseStorageImpl(ConfigManagerImpl configManager) {
        this.configManager = configManager;
    }

    @Override
    public void load() {
        config = configManager.getConfig("Cases.yml");
    }

    @Override
    public void save(@NotNull String name, @NotNull CaseInfo caseInfo) throws ConfigurateException {
        ConfigurationNode current = config.node("DonateCase", "Cases", name);
        current.set(CaseInfo.class, caseInfo);
        config.save();
    }

    @Override
    public void delete(String name) {
        config.node("DonateCase", "Cases").removeChild(name);
        try {
            config.save();
        } catch (ConfigurateException e) {
            configManager.getPlatform().getLogger().log(Level.WARNING, "Error with deleting case: " + name, e);
        }
    }

    @Override
    public boolean delete(CaseLocation location) {
        ConfigurationNode parent = config.node("DonateCase", "Cases");
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : parent.childrenMap().entrySet()) {
            try {
                CaseInfo caseInfo = entry.getValue().get(CaseInfo.class);
                if (caseInfo == null) continue;

                if (location.equals(caseInfo.location())) {
                    parent.removeChild(entry.getKey());
                    config.save();
                    return true;
                }
            } catch (ConfigurateException ignored) {
            }

        }

        return false;
    }

    @Override
    public @NotNull Map<String, CaseInfo> get() {
        Map<String, CaseInfo> map = new HashMap<>();
        ConfigurationNode parent = config.node("DonateCase", "Cases");
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : parent.childrenMap().entrySet()) {
            String name = String.valueOf(entry.getKey());
            CaseInfo caseInfo = get(entry.getValue());

            if (caseInfo != null) map.put(name, caseInfo);
        }

        return map;
    }

    @Override
    public boolean has(String name) {
        return config.node().hasChild("DonateCase", "Cases", name);
    }

    @Override
    public CaseInfo get(String name) {
        return get(config.node("DonateCase", "Cases", name));
    }

    @Override
    public CaseInfo get(CaseLocation location) {
        ConfigurationNode parent = config.node("DonateCase", "Cases");
        for (ConfigurationNode value : parent.childrenMap().values()) {

            CaseInfo caseInfo = get(value);
            if (caseInfo != null) {
                if (location.equals(caseInfo.location())) return caseInfo;
            }
        }

        return null;
    }

    @Nullable
    private CaseInfo get(ConfigurationNode node) {
        try {
            return node.get(CaseInfo.class);
        } catch (SerializationException e) {
            configManager.getPlatform().getLogger().log(Level.WARNING, "Error with getting info about case: " + node.key(), e);
        }

        return null;
    }

}