package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.casedata.gui.GuiTypedItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Interface for managing GUI-typed items associated with case data materials, enabling
 * registration, retrieval, and management of GUI elements.
 *
 */
public interface GUITypedItemManager{

    /**
     * Provides a builder for creating a new GUI typed item with a specified ID.
     *
     * @param id the unique identifier for the GUI typed item to create
     * @return a {@code GUITypedItem.Builder} instance for creating the typed item
     * @see #registerItem(GuiTypedItem)
     */
    @NotNull
    GuiTypedItem.Builder builder(String id, Addon addon);

    /**
     * Registers a GUI typed item.
     *
     * @param item the {@code GUITypedItem} object to register
     * @return true if the registration is successful, false otherwise
     * @see #builder(String, Addon)
     */
    boolean registerItem(GuiTypedItem item);

    /**
     * Unregisters a GUI typed item by its ID.
     *
     * @param id the unique identifier of the GUI typed item to unregister
     */
    void unregisterItem(String id);

    default void unregisterItems(Addon addon) {
        List<GuiTypedItem> list = new ArrayList<>(getRegisteredItems(addon));
        list.stream().map(GuiTypedItem::getId).forEach(this::unregisterItem);
    }

    /**
     * Unregisters all registered GUI typed items.
     */
    void unregisterItems();

    /**
     * Retrieves a registered GUI typed item by its ID.
     *
     * @param id the unique identifier of the GUI typed item
     * @return the {@code GUITypedItem} object if found, or null otherwise
     */
    @Nullable
    GuiTypedItem getRegisteredItem(@NotNull String id);

    default List<GuiTypedItem> getRegisteredItems(Addon addon) {
        return getRegisteredItems().values().stream().filter(item ->
                item.getAddon().equals(addon)).collect(Collectors.toList());
    }

    @NotNull
    Map<String, GuiTypedItem> getRegisteredItems();


    /**
     * Retrieves the ID of a registered GUI typed item that matches the start of a given string.
     *
     * @param string the prefix string to match against GUI typed item IDs
     * @return the ID of the matched GUI typed item if found, or null otherwise
     */
    @Nullable
    String getByStart(@NotNull final String string);

    /**
     * Retrieves a registered GUI typed item by parsing a string.
     *
     * @param string the string to parse and match to a GUI typed item
     * @return the {@code GUITypedItem} object if found, or null otherwise
     */
    @Nullable
    GuiTypedItem getFromString(@NotNull final String string);
}
