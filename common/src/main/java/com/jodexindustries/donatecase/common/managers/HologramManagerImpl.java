package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.hologram.HologramDriver;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.manager.HologramManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class HologramManagerImpl implements HologramManager {

    private HologramDriver driver;

    private final Map<String, HologramDriver> drivers = new HashMap<>();
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
        String name = api.getConfig().getConfig().node("DonateCase", "HologramDriver").getString("decentholograms").toLowerCase();
        set(name);
        if (driver == null) return;

        api.getPlatform().getLogger().info("Using " + name + " as hologram driver");

        remove();

        for (Map.Entry<String, CaseInfo> entry : api.getConfig().getCaseStorage().get().entrySet()) {
            CaseInfo info = entry.getValue();

            String caseType = info.getType();

            CaseData caseData = api.getCaseManager().get(caseType);

            if (caseData == null || !caseData.getHologram().isEnabled()) continue;

            CaseLocation location = info.getLocation();

            if (!api.getPlatform().isWorldLoaded(location.getWorld())) {
                api.getPlatform().getLogger().warning("Hologram creation error. World " + location.getWorld() + " is null for case name: " + entry.getKey());
                continue;
            }

            create(location, caseData.getHologram());
        }
    }

    @Override
    public void create(CaseLocation block, CaseData.Hologram caseHologram) {
        try {
            if (driver != null) driver.create(block, caseHologram);
        } catch (Exception e) {
            api.getPlatform().getLogger().log(Level.WARNING, "Error with creating hologram: ", e);
        }
    }

    @Override
    public void remove(CaseLocation block) {
        if (driver != null) driver.remove(block);
    }

    @Override
    public void remove() {
        if (driver != null) driver.remove();
    }
}