package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.api.manager.MaterialManager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialManagerImpl implements MaterialManager<ItemStack> {
    /**
     * Map of all registered materials
     */
    public static final Map<String, CaseMaterial<ItemStack>> registeredMaterials = new HashMap<>();
    private final Addon addon;

    /**
     * Default constructor
     *
     * @param addon An addon that will manage materials
     * @since 2.2.4.8
     */
    public MaterialManagerImpl(Addon addon) {
        this.addon = addon;
    }

    @Override
    public void registerMaterial(String id, MaterialHandler<ItemStack> materialHandler, String description) {
        if (!isRegistered(id)) {
            CaseMaterial<ItemStack> caseMaterial = new CaseMaterial<>(materialHandler, addon, id, description);
            registeredMaterials.put(id, caseMaterial);
        } else {
            addon.getLogger().warning("CaseMaterial with id " + id + " already registered");
        }
    }

    @Override
    public void unregisterMaterial(String id) {
        if (isRegistered(id)) {
            registeredMaterials.remove(id);
        } else {
            addon.getLogger().warning("CaseMaterial with id " + id + " already unregistered!");
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
    public CaseMaterial<ItemStack> getRegisteredMaterial(@NotNull String id) {
        return registeredMaterials.get(id);
    }

    @Nullable
    @Override
    public String getByStart(@NotNull final String string) {
        return registeredMaterials.keySet().stream().filter(string::startsWith).findFirst().orElse(null);
    }
}