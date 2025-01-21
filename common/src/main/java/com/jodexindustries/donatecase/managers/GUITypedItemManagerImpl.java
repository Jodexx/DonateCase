package com.jodexindustries.donatecase.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.casedata.gui.GuiTypedItem;
import com.jodexindustries.donatecase.api.manager.GUITypedItemManager;
import com.jodexindustries.donatecase.api.platform.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUITypedItemManagerImpl implements GUITypedItemManager {

    private final static Map<String, GuiTypedItem> registeredItems = new HashMap<>();

    private final Platform platform;

    public GUITypedItemManagerImpl(DCAPI api) {
        this.platform = api.getPlatform();
    }

    @NotNull
    @Override
    public GuiTypedItem.Builder builder(String id, Addon addon) {
        return new GuiTypedItem.Builder(id, addon);
    }

    @Override
    public boolean registerItem(GuiTypedItem item) {
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
    public void unregisterItem(String id) {
        if (registeredItems.get(id) != null) {
            registeredItems.remove(id);
        } else {
            platform.getLogger().warning("Typed item " + id + " not registered!");
        }
    }

    @Override
    public void unregisterItems() {
        List<String> items = new ArrayList<>(registeredItems.keySet());
        items.forEach(this::unregisterItem);
    }

    @Nullable
    @Override
    public GuiTypedItem getRegisteredItem(@NotNull String id) {
        return registeredItems.get(id.toLowerCase());
    }

    @Override
    public @NotNull Map<String, GuiTypedItem> getRegisteredItems() {
        return registeredItems;
    }

    @Nullable
    @Override
    public String getByStart(@NotNull final String string) {
        return registeredItems.keySet().stream().filter(string.toLowerCase()::startsWith).findFirst().orElse(null);
    }

    @Nullable
    @Override
    public GuiTypedItem getFromString(@NotNull final String string) {
        String temp = getByStart(string);
        return temp != null ? getRegisteredItem(temp) : null;
    }
}