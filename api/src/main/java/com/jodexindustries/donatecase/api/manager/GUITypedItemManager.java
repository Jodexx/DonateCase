package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Interface for managing GUI-typed items associated with case data materials, enabling
 * registration, retrieval, and management of GUI elements.
 *
 */
public interface GUITypedItemManager {

    /**
     * Registers a GUI typed item.
     *
     * @param item the {@code GUITypedItem} object to register
     * @return true if the registration is successful, false otherwise
     */
    boolean register(TypedItem item);

    /**
     * Unregisters a GUI typed item by its ID.
     *
     * @param id the unique identifier of the GUI typed item to unregister
     */
    void unregister(String id);

    default void unregister(Addon addon) {
        List<TypedItem> list = new ArrayList<>(get(addon));
        list.stream().map(TypedItem::id).forEach(this::unregister);
    }

    /**
     * Unregisters all registered GUI typed items.
     */
    void unregister();

    /**
     * Retrieves a registered GUI typed item by its ID.
     *
     * @param id the unique identifier of the GUI typed item
     * @return the {@code GUITypedItem} object if found, or null otherwise
     */
    @Nullable
    TypedItem get(@NotNull String id);

    default List<TypedItem> get(Addon addon) {
        return getMap().values().stream().filter(item ->
                item.addon().equals(addon)).collect(Collectors.toList());
    }

    @NotNull
    Map<String, TypedItem> getMap();


    /**
     * Retrieves the ID of a registered GUI typed item that matches the start of a given string.
     *
     * @param string the prefix string to match against GUI typed item IDs
     * @return the ID of the matched GUI typed item if found
     */
    default Optional<String> getByStart(@NotNull final String string) {
        return getMap().keySet().stream().filter(string.toLowerCase()::startsWith).findFirst();
    }

    /**
     * Retrieves a registered GUI typed item by parsing a string.
     *
     * @param string the string to parse and match to a GUI typed item
     * @return the {@code GUITypedItem} object if found
     */
    Optional<TypedItem> getFromString(@NotNull final String string);

}