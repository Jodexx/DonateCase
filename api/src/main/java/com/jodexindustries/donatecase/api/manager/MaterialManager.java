package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Interface for managing item materials, allowing registration, retrieval, and unregistration of case materials.
 *
 */
public interface MaterialManager{

    /**
     * Registers a new case material with a specified ID, handler, and description.
     *
     * @param id              the unique identifier for the material, e.g., "BASE64"
     * @param materialHandler the handler responsible for managing the material's properties and behavior
     * @param description     a description providing details about the material
     */
    void registerMaterial(String id, MaterialHandler materialHandler, String description, Addon addon);

    /**
     * Unregisters a case material by its ID.
     *
     * @param id the unique identifier of the material to unregister
     */
    void unregisterMaterial(String id);

    default void unregisterMaterials(Addon addon) {
        List<CaseMaterial> list = new ArrayList<>(getRegisteredMaterials(addon));
        list.stream().map(CaseMaterial::getId).forEach(this::unregisterMaterial);
    }

    /**
     * Unregisters all registered case materials.
     */
    void unregisterMaterials();

    /**
     * Checks if a case material is registered by ID.
     *
     * @param id the unique identifier of the material to check
     * @return true if the material is registered, false otherwise
     */
    boolean isRegistered(String id);

    /**
     * Retrieves a registered case material by its ID.
     *
     * @param id the unique identifier of the material to retrieve
     * @return the {@code CaseMaterial} instance if found, or null otherwise
     */
    @Nullable
    CaseMaterial getRegisteredMaterial(@NotNull String id);

    default List<CaseMaterial> getRegisteredMaterials(Addon addon) {
        return getRegisteredMaterials().values().stream().filter(material ->
                material.getAddon().equals(addon)).collect(Collectors.toList());
    }

    @NotNull
    Map<String, CaseMaterial> getRegisteredMaterials();

    /**
     * Retrieves a registered material ID that matches the start of a given string.
     *
     * @param string the string to match against material IDs
     * @return the ID of the matched material if found, or null otherwise
     */
    @Nullable
    String getByStart(@NotNull final String string);
}
