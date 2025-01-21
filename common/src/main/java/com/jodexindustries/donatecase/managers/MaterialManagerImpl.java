package com.jodexindustries.donatecase.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.api.manager.MaterialManager;
import com.jodexindustries.donatecase.api.platform.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialManagerImpl implements MaterialManager {
    /**
     * Map of all registered materials
     */
    private static final Map<String, CaseMaterial> registeredMaterials = new HashMap<>();

    private final Platform platform;

    public MaterialManagerImpl(DCAPI api) {
        this.platform = api.getPlatform();
    }

    @Override
    public void registerMaterial(String id, MaterialHandler materialHandler, String description, Addon addon) {
        if (!isRegistered(id)) {
            CaseMaterial caseMaterial = new CaseMaterial(materialHandler, addon, id, description);
            registeredMaterials.put(id, caseMaterial);
        } else {
            platform.getLogger().warning("CaseMaterial with id " + id + " already registered");
        }
    }

    @Override
    public void unregisterMaterial(String id) {
        if (isRegistered(id)) {
            registeredMaterials.remove(id);
        } else {
            platform.getLogger().warning("CaseMaterial with id " + id + " already unregistered!");
        }
    }

    @Override
    public void unregisterMaterials() {
        List<String> list = new ArrayList<>(registeredMaterials.keySet());
        list.forEach(this::unregisterMaterial);
    }

    @Override
    public boolean isRegistered(String id) {
        return registeredMaterials.containsKey(id);
    }

    @Nullable
    @Override
    public CaseMaterial getRegisteredMaterial(@NotNull String id) {
        return registeredMaterials.get(id);
    }

    @Override
    public @NotNull Map<String, CaseMaterial> getRegisteredMaterials() {
        return registeredMaterials;
    }

    @Nullable
    @Override
    public String getByStart(@NotNull final String string) {
        return registeredMaterials.keySet().stream().filter(string::startsWith).findFirst().orElse(null);
    }
}