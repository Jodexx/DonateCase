package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.CaseMaterialException;
import com.jodexindustries.donatecase.api.manager.MaterialManager;
import com.jodexindustries.donatecase.api.platform.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MaterialManagerImpl implements MaterialManager {

    public final Map<String, CaseMaterial> registeredMaterials = new ConcurrentHashMap<>();

    private final Platform platform;

    public MaterialManagerImpl(DCAPI api) {
        this.platform = api.getPlatform();
    }

    @Override
    public void register(CaseMaterial material) throws CaseMaterialException {
        if (isRegistered(material.id()))
            throw new CaseMaterialException("Material with id " + material.id() + " already registered!");

        registeredMaterials.put(material.id(), material);
    }

    @Override
    public void unregister(String id) {
        if (isRegistered(id)) {
            registeredMaterials.remove(id);
        } else {
            platform.getLogger().warning("CaseMaterial with id " + id + " already unregistered!");
        }
    }

    @Override
    public void unregister() {
        List<String> list = new ArrayList<>(registeredMaterials.keySet());
        list.forEach(this::unregister);
    }

    @Override
    public boolean isRegistered(String id) {
        return registeredMaterials.containsKey(id);
    }

    @Nullable
    @Override
    public CaseMaterial get(@NotNull String id) {
        return registeredMaterials.get(id);
    }

    @Override
    public @NotNull Map<String, CaseMaterial> getMap() {
        return registeredMaterials;
    }

}