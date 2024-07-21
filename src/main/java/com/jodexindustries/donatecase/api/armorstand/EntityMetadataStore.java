package com.jodexindustries.donatecase.api.armorstand;

import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * An EntityMetadataStore stores metadata values for all {@link Entity} classes an their descendants.
 */
public class EntityMetadataStore extends MetadataStoreBase<UUID> implements MetadataStore<UUID> {
    /**
     * Generates a unique metadata key for an {@link Entity} UUID.
     *
     * @param uuid the entity uuid
     * @param metadataKey The name identifying the metadata value
     * @return a unique metadata key
     * @see MetadataStoreBase#disambiguate(Object, String)
     */
    @Override
    protected @NotNull String disambiguate(@NotNull UUID uuid, @NotNull String metadataKey) {
        return uuid + ":" + metadataKey;
    }

}