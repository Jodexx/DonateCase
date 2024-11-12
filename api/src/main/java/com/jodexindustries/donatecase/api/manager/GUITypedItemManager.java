package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.casedata.gui.GUITypedItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for managing GUI-typed items associated with case data materials, enabling
 * registration, retrieval, and management of GUI elements.
 *
 * @param <C> The type extending {@code CaseDataMaterial} associated with GUI items in this manager
 */
public interface GUITypedItemManager<C extends CaseDataMaterial, G, E> {

    /**
     * Provides a builder for creating a new GUI typed item with a specified ID.
     *
     * @param id the unique identifier for the GUI typed item to create
     * @return a {@code GUITypedItem.Builder} instance for creating the typed item
     * @see #registerItem(GUITypedItem)
     */
    @NotNull
    GUITypedItem.Builder<C, G, E> builder(String id);

    /**
     * Registers a GUI typed item.
     *
     * @param item the {@code GUITypedItem} object to register
     * @return true if the registration is successful, false otherwise
     * @see #builder(String)
     */
    boolean registerItem(GUITypedItem<C, G, E> item);

    /**
     * Unregisters a GUI typed item by its ID.
     *
     * @param id the unique identifier of the GUI typed item to unregister
     */
    void unregisterItem(String id);

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
    GUITypedItem<C, G, E> getRegisteredItem(@NotNull String id);

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
    GUITypedItem<C, G, E> getFromString(@NotNull final String string);
}
