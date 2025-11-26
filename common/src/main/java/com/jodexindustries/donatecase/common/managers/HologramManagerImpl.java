package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseSettings;
import com.jodexindustries.donatecase.api.data.hologram.HologramDriver;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.data.storage.CaseWorld;
import com.jodexindustries.donatecase.api.manager.HologramManager;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class HologramManagerImpl implements HologramManager {

    private HologramDriver driver;

    private final Map<@NotNull String, @NotNull HologramDriver> drivers = new ConcurrentHashMap<>();
    private final DCAPI api;

    public HologramManagerImpl(DCAPI api) {
        this.api = api;
    }

    @Override
    public void register(@NotNull String name, @NotNull HologramDriver driver) {
        if (drivers.containsKey(name)) return;

        drivers.put(name, driver);
    }

    @Override
    public void unregister(@NotNull String name) {
        drivers.remove(name);
    }

    @Override
    public Map<String, HologramDriver> get() {
        return drivers;
    }

    @Override
    public void set(@NotNull String name) {
        driver = drivers.get(name);
    }

    @Override
    public void load() {
        String name = api.getConfigManager().getConfig().hologramDriver().toLowerCase();
        set(name);
        if (driver == null) {
            Iterator<String> it = drivers.keySet().iterator();
            if (it.hasNext()) {
                String fallback = it.next();
                api.getPlatform().getLogger().info("Hologram driver '" + name + "' was not found! Using '" + fallback + "' instead");
                set(fallback);
            } else {
                return;
            }
        } else {
            api.getPlatform().getLogger().info("Using '" + name + "' as hologram driver");
        }

        remove();

        for (Map.Entry<String, CaseInfo> entry : api.getConfigManager().getCaseStorage().get().entrySet()) {
            CaseInfo info = entry.getValue();

            String caseType = info.type();

            Optional<CaseDefinition> optional = api.getCaseManager().getByType(caseType);
            if (!optional.isPresent()) continue;

            CaseSettings.Hologram hologram = optional.get().settings().hologram();

            if (!hologram.enabled()) continue;

            CaseLocation location = info.location();
            CaseWorld world = location.getWorld();

            if (api.getAnimationManager().isLocked(location)) continue;

            if (world == null || !api.getPlatform().isWorldLoaded(world.name())) {
                api.getPlatform().getLogger().warning("Hologram creation error. World is null for case name: " + entry.getKey());
                continue;
            }

            create(location, hologram);
        }
    }

    @Override
    public void forceCreate(@NotNull CaseLocation block, CaseSettings.@NotNull Hologram hologram) {
        try {
            if (driver != null) driver.forceCreate(block, hologram);
        } catch (Exception e) {
            api.getPlatform().getLogger().log(Level.WARNING, "Error with creating hologram: ", e);
        }
    }

    @Override
    public void remove(@NotNull CaseLocation block) {
        if (driver != null) driver.remove(block);
    }

    @Override
    public void remove() {
        if (driver != null) driver.remove();
    }
}