package com.jodexindustries.donatecase.common.entity.meta;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class Metadata {

    private final int entityId;
    private final HashMap<Byte, EntityData<?>> notNotifiedChanges = new HashMap<>();
    private final ConcurrentHashMap<Byte, EntityData<?>> metadataMap = new ConcurrentHashMap<>();

    public Metadata(int entityId) {
        this.entityId = entityId;
    }

    public void copyTo(Metadata other) {
        other.clear();
        synchronized (other.notNotifiedChanges) {
            other.notNotifiedChanges.putAll(notNotifiedChanges);
        }
        other.metadataMap.putAll(metadataMap);
    }

    public void copyFrom(Metadata other) {
        other.copyTo(this); // Scuffed pepelaugh
    }

    /**
     * Clears the internal metadata map, is not responsible for informing the clients entity view with the newly reset information
     */
    public void clear() {
        this.metadataMap.clear();
        this.notNotifiedChanges.clear();
    }

    public <T> T getIndex(byte index, @Nullable T defaultValue) {
        EntityData<?> value = this.metadataMap.get(index);
        return value != null ? (T) value.getValue() : defaultValue;
    }

    public <T> void setIndex(byte index, @NotNull EntityDataType<T> dataType, T value) {
        final EntityData<?> entry = new EntityData<>(index, dataType, value);
        this.metadataMap.put(index, entry);
    }

    public void setMetaFromPacket(WrapperPlayServerEntityMetadata wrapper) {
        for (EntityData<?> data : wrapper.getEntityMetadata()) {
            metadataMap.put((byte) data.getIndex(), data);
        }
    }

    @NotNull List<EntityData<?>> getEntries() {
        return Collections.unmodifiableList(new ArrayList<>(metadataMap.values()));
    }

    public WrapperPlayServerEntityMetadata createPacket() {
        return new WrapperPlayServerEntityMetadata(entityId, getEntries());
    }

}