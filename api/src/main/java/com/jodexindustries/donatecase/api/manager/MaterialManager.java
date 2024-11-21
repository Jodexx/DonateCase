package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Interface for managing item materials, allowing registration, retrieval, and unregistration of case materials.
 *
 * @param <I> The type representing the material item within the material management context
 */
public interface MaterialManager<I> {

    /**
     * Registers a new case material with a specified ID, handler, and description.
     *
     * @param id              the unique identifier for the material, e.g., "BASE64"
     * @param materialHandler the handler responsible for managing the material's properties and behavior
     * @param description     a description providing details about the material
     */
    void registerMaterial(String id, MaterialHandler<I> materialHandler, String description);

    /**
     * Unregisters a case material by its ID.
     *
     * @param id the unique identifier of the material to unregister
     */
    void unregisterMaterial(String id);

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
    CaseMaterial<I> getRegisteredMaterial(@NotNull String id);

    @NotNull
    Map<String, CaseMaterial<I>> getRegisteredMaterials();

    /**
     * Retrieves a registered material ID that matches the start of a given string.
     *
     * @param string the string to match against material IDs
     * @return the ID of the matched material if found, or null otherwise
     */
    @Nullable
    String getByStart(@NotNull final String string);
}
