package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import com.jodexindustries.donatecase.api.manager.GUITypedItemManager;
import com.jodexindustries.donatecase.api.platform.Platform;
import com.jodexindustries.donatecase.common.gui.items.HISTORYItemHandlerImpl;
import com.jodexindustries.donatecase.common.gui.items.OPENItemClickHandlerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GUITypedItemManagerImpl implements GUITypedItemManager {

    public final Map<String, TypedItem> registeredItems = new ConcurrentHashMap<>();

    private final Platform platform;

    public GUITypedItemManagerImpl(DCAPI api) {
        this.platform = api.getPlatform();

        List<? extends TypedItem> defaultItems = Arrays.asList(
                TypedItem.builder()
                        .id("HISTORY")
                        .addon(platform)
                        .description("Type for displaying the history of case openings")
                        .handler(new HISTORYItemHandlerImpl())
                        .build(),
                TypedItem.builder()
                        .id("OPEN")
                        .addon(platform)
                        .description("Type to open the case")
                        .click(new OPENItemClickHandlerImpl())
                        .updateMeta(true)
                        .loadOnCase(true)
                        .build()
        );

        defaultItems.forEach(this::register);
    }

    @Override
    public boolean register(TypedItem item) {
        String id = item.id().toLowerCase();
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
    public Optional<TypedItem> getFromString(@NotNull final String string) {
        Optional<String> temp = getByStart(string);
        return temp.map(this::get);
    }

}