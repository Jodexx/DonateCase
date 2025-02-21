package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
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
public interface MaterialManager {

    void register(CaseMaterial material);

    /**
     * Unregisters a case material by its ID.
     *
     * @param id the unique identifier of the material to unregister
     */
    void unregister(String id);

    default void unregister(Addon addon) {
        List<CaseMaterial> list = new ArrayList<>(get(addon));
        list.stream().map(CaseMaterial::id).forEach(this::unregister);
    }

    /**
     * Unregisters all registered case materials.
     */
    void unregister();

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
    CaseMaterial get(@NotNull String id);

    default List<CaseMaterial> get(Addon addon) {
        return getMap().values().stream().filter(material ->
                material.addon().equals(addon)).collect(Collectors.toList());
    }

    @NotNull
    Map<String, CaseMaterial> getMap();

    /**
     * Retrieves a registered material ID that matches the start of a given string.
     *
     * @param string the string to match against material IDs
     * @return the ID of the matched material if found, or null otherwise
     */
    @Nullable
    String getByStart(@NotNull final String string);
}
