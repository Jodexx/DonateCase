package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.data.casedata.gui.GUITypedItem;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import com.jodexindustries.donatecase.api.manager.GUITypedItemManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUITypedItemManagerImpl implements GUITypedItemManager<CaseDataMaterialBukkit, CaseGui, CaseGuiClickEvent> {
    /**
     * Map of all registered items
     */
    public final static Map<String, GUITypedItem<CaseDataMaterialBukkit, CaseGui, CaseGuiClickEvent>> registeredItems = new HashMap<>();
    private final Addon addon;

    /**
     * Default constructor
     *
     * @param addon An addon that will manage items
     * @since 2.2.4.9
     */
    public GUITypedItemManagerImpl(Addon addon) {
        this.addon = addon;
    }

    @NotNull
    @Override
    public GUITypedItem.Builder<CaseDataMaterialBukkit, CaseGui, CaseGuiClickEvent> builder(String id) {
        return new GUITypedItem.Builder<>(id, addon);
    }

    @Override
    public boolean registerItem(GUITypedItem<CaseDataMaterialBukkit, CaseGui, CaseGuiClickEvent> item) {
        String id = item.getId().toLowerCase();
        if (registeredItems.get(id) == null) {
            registeredItems.put(id, item);
            return true;
        } else {
            addon.getLogger().warning("Typed item " + id + " already registered!");
        }

        return false;
    }

    @Override
    public void unregisterItem(String id) {
        if (registeredItems.get(id) != null) {
            registeredItems.remove(id);
        } else {
            addon.getLogger().warning("Typed item " + id + " not registered!");
        }
    }

    @Override
    public void unregisterItems() {
        List<String> items = new ArrayList<>(registeredItems.keySet());
        items.forEach(this::unregisterItem);
    }

    @Nullable
    @Override
    public GUITypedItem<CaseDataMaterialBukkit, CaseGui, CaseGuiClickEvent> getRegisteredItem(@NotNull String id) {
        return registeredItems.get(id.toLowerCase());
    }

    @Nullable
    @Override
    public String getByStart(@NotNull final String string) {
        return registeredItems.keySet().stream().filter(string.toLowerCase()::startsWith).findFirst().orElse(null);
    }

    @Nullable
    @Override
    public GUITypedItem<CaseDataMaterialBukkit, CaseGui, CaseGuiClickEvent> getFromString(@NotNull final String string) {
        String temp = getByStart(string);
        return temp != null ? getRegisteredItem(temp) : null;
    }
}