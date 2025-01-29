package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import com.jodexindustries.donatecase.api.manager.GUITypedItemManager;
import com.jodexindustries.donatecase.api.platform.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUITypedItemManagerImpl implements GUITypedItemManager {

    private final static Map<String, TypedItem> registeredItems = new HashMap<>();

    private final Platform platform;

    public GUITypedItemManagerImpl(DCAPI api) {
        this.platform = api.getPlatform();
    }

    @Override
    public boolean register(TypedItem item) {
        String id = item.getId().toLowerCase();
        if (registeredItems.get(id) == null) {
            registeredItems.put(id, item);
            return true;
        } else {
            platform.getLogger().warning("Typed item " + id + " already registered!");
        }

        return false;
    }

    @Override
    public void unregister(String id) {
        if (registeredItems.get(id) != null) {
            registeredItems.remove(id);
        } else {
            platform.getLogger().warning("Typed item " + id + " not registered!");
        }
    }

    @Override
    public void unregister() {
        List<String> items = new ArrayList<>(registeredItems.keySet());
        items.forEach(this::unregister);
    }

    @Nullable
    @Override
    public TypedItem get(@NotNull String id) {
        return registeredItems.get(id.toLowerCase());
    }

    @Override
    public @NotNull Map<String, TypedItem> getMap() {
        return registeredItems;
    }

    @Nullable
    @Override
    public String getByStart(@NotNull final String string) {
        return registeredItems.keySet().stream().filter(string.toLowerCase()::startsWith).findFirst().orElse(null);
    }

    @Nullable
    @Override
    public TypedItem getFromString(@NotNull final String string) {
        String temp = getByStart(string);
        return temp != null ? get(temp) : null;
    }

}