package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.gui.GUITypedItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for managing gui typed items
 * @since 2.2.4.9
 */
public class GUITypedItemManager {
    private final static Map<String, GUITypedItem> registeredItems = new HashMap<>();
    private final Addon addon;

    /**
     * Default constructor
     *
     * @param addon An addon that will manage items
     * @since 2.2.4.9
     */
    public GUITypedItemManager(Addon addon) {
        this.addon = addon;
    }

    /**
     * Gets Builder for creating GUI typed item
     *
     * @param id Typed item id to create
     * @return GUITypedItem Builder
     * @see #registerItem(GUITypedItem)
     * @since 2.2.4.9
     */
    @NotNull
    public GUITypedItem.Builder builder(String id) {
        return new GUITypedItem.Builder(id, addon);
    }

    /**
     * Register GUI typed item
     *
     * @param item GUITypedItem object
     * @return true, if successful
     * @see #builder(String)
     * @since 2.2.4.9
     */
    public boolean registerItem(GUITypedItem item) {
        String id = item.getId().toLowerCase();
        if (registeredItems.get(id) == null) {
            registeredItems.put(id, item);
            return true;
        } else {
            addon.getLogger().warning("Typed item " + id + " already registered!");
        }

        return false;
    }

    /**
     * Unregister GUI typed item
     *
     * @param id Item id
     * @since 2.2.4.9
     */
    public void unregisterItem(String id) {
        if (registeredItems.get(id) != null) {
            registeredItems.remove(id);
        } else {
            addon.getLogger().warning("Typed item " + id + " not registered!");
        }
    }

    /**
     * Unregister all typed items
     *
     * @since 2.2.4.9
     */
    public void unregisterItems() {
        List<String> items = new ArrayList<>(registeredItems.keySet());
        items.forEach(this::unregisterItem);
    }

    /**
     * Get all registered items
     *
     * @return Map of typed items
     * @since 2.2.4.9
     */
    public static Map<String, GUITypedItem> getRegisteredItems() {
        return registeredItems;
    }

    /**
     * Get registered item
     *
     * @param id GUITypedItem id
     * @return GUITypedItem object
     * @since 2.2.4.9
     */
    @Nullable
    public static GUITypedItem getRegisteredItem(@NotNull String id) {
        return registeredItems.get(id.toLowerCase());
    }

    /**
     * Get registered item by string start
     *
     * @param string String to be parsed
     * @return GUITypedItem id
     * @since 2.2.4.9
     */
    public static @Nullable String getByStart(@NotNull final String string) {
        return registeredItems.keySet().stream().filter(string.toLowerCase()::startsWith).findFirst().orElse(null);
    }

    /**
     * Get registered GUITypedItem by string
     * @param string String to be parsed
     * @return GUITypedItem object
     * @since 2.2.5.5
     */
    public static @Nullable GUITypedItem getFromString(@NotNull final String string) {
        String temp = GUITypedItemManager.getByStart(string);
        return temp != null ? GUITypedItemManager.getRegisteredItem(temp) : null;
    }

}
