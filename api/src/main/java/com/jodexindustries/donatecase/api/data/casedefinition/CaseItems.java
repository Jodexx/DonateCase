package com.jodexindustries.donatecase.api.data.casedefinition;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.MetaUpdater;
import com.jodexindustries.donatecase.api.tools.ProbabilityCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CaseItems implements Cloneable {

    private Map<String, CaseItem> items;

    public CaseItems() {
        this.items = new HashMap<>();
    }

    public CaseItems(Map<String, CaseItem> items) {
        this.items = items;
    }

    @Nullable
    public CaseItem getItem(@NotNull String name) {
        return items.get(name);
    }

    public CaseItem getRandomItem() {
        ProbabilityCollection<CaseItem> collection = new ProbabilityCollection<>();
        for (CaseItem item : items.values()) {
            double chance = item.chance();
            if (chance > 0) collection.add(item, chance);
        }
        return collection.get();
    }

    public void items(Map<String, CaseItem> items) {
        this.items = items;
    }

    public Map<String, CaseItem> items() {
        return Collections.unmodifiableMap(items);
    }

    public void updateItemsMeta() {
        updateItemsMeta(DCAPI.getInstance().getPlatform().getMetaUpdater());
    }

    public void updateItemsMeta(MetaUpdater metaUpdater) {
        items.values().forEach(value -> value.material().updateMeta(metaUpdater));
    }

    /**
     * Checks if the current collection of items contains any "real" items.
     * A "real" item is defined as an item with a chance greater than 0.
     *
     * @return {@code true} if all items in the collection have a chance greater than 0, {@code false} otherwise.
     */
    public boolean hasRealItems() {
        return items.values().stream().anyMatch(item -> item.chance() > 0);
    }

    @Override
    public CaseItems clone() {
        try {
            CaseItems clone = (CaseItems) super.clone();
            clone.items = this.items.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().clone(), (a, b) -> b));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
