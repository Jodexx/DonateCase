package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for managing item materials
 * @since 2.2.4.8
 */
public class MaterialManager {
    private static final Map<String, CaseMaterial> registeredMaterials = new HashMap<>();
    private final Addon addon;

    /**
     * Default constructor
     * @param addon An addon that will manage materials
     * @since 2.2.4.8
     */
    public MaterialManager(Addon addon) {
        this.addon = addon;
    }

    /**
     * Register case material
     * @param id Material id, like: "BASE64"
     * @param materialHandler Material handler
     * @param description Material description
     * @since 2.2.4.8
     */
    public void registerMaterial(String id, MaterialHandler materialHandler, String description) {
        if(!isRegistered(id)) {
            CaseMaterial caseMaterial = new CaseMaterial(materialHandler, addon, id, description);
            registeredMaterials.put(id, caseMaterial);
        } else {
            addon.getLogger().warning("CaseMaterial with id " + id + " already registered");
        }
    }

    /**
     * Unregister case material
     * @param id Material id
     * @since 2.2.4.8
     */
    public void unregisterMaterial(String id) {
        if(isRegistered(id)) {
            registeredMaterials.remove(id);
        } else {
            addon.getLogger().warning("CaseMaterial with id " + id + " already unregistered!");
        }
    }

    /**
     * Unregister all case materials
     * @since 2.2.4.8
     */
    public void unregisterMaterials() {
        List<String> list = new ArrayList<>(registeredMaterials.keySet());
        list.forEach(this::unregisterMaterial);
    }

    /**
     * Check for material registration
     * @param id material id
     * @return boolean
     * @since 2.2.4.8
     */
    public static boolean isRegistered(String id) {
        return registeredMaterials.containsKey(id);
    }

    /**
     * Get all registered materials
     * @return map with registered materials
     * @since 2.2.4.8
     */
    public static Map<String, CaseMaterial> getRegisteredMaterials() {
        return registeredMaterials;
    }

    /**
     * Get registered material
     * @param id CaseMaterial id
     * @return CaseMaterial object
     * @since 2.2.4.8
     */
    @Nullable
    public static CaseMaterial getRegisteredMaterial(@NotNull String id) {
        return registeredMaterials.get(id);
    }

    /**
     * Get registered materials by string start
     * @param string String to be parsed
     * @return Case material id
     * @since 2.2.4.8
     */
    public static @Nullable String getByStart(@NotNull final String string) {
        return registeredMaterials.keySet().stream().filter(string::startsWith).findFirst().orElse(null);
    }
}
