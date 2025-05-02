package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.stream.Collectors;

@Accessors(fluent = true)
@Setter
@Getter
public class CaseGui implements Cloneable {
    private String title;
    private int size;
    private int updateRate;
    private transient Map<String, Item> items;
    private transient PageData pageData;
    private int itemsPerPage = 28;
    private List<Integer> paginationSlots = Arrays.asList(45, 46, 47, 48, 49, 50, 51, 52, 53);

    @Nullable
    public String getItemTypeBySlot(int slot) {
        for (Item item : items.values()) {
            if (item.slots.contains(slot)) return item.type;
        }
        return null;
    }

    public void initPages() {
        pageData = new PageData();
        if (items == null || items.isEmpty()) return;

        List<Item> regularItems = items.values().stream()
                .filter(item -> !item.type.startsWith("PAGINATION_"))
                .collect(Collectors.toList());

        int pageNum = 0;
        int itemCount = 0;
        Map<String, Item> currentPageItems = new HashMap<>();

        for (Item item : regularItems) {
            currentPageItems.put(item.type, item);
            itemCount += item.slots.size();

            if (itemCount >= itemsPerPage) {
                pageData.getPages().put(pageNum, new HashMap<>(currentPageItems));
                currentPageItems.clear();
                itemCount = 0;
                pageNum++;
            }
        }

        if (!currentPageItems.isEmpty()) {
            pageData.getPages().put(pageNum, currentPageItems);
        }

        pageData.setTotalPages(Math.max(1, pageData.getPages().size()));
    }

    private Map<String, Item> cloneItemsMap(Map<String, Item> originalMap) {
        return originalMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().clone()
                ));
    }

    @Override
    public CaseGui clone() {
        try {
            CaseGui clone = (CaseGui) super.clone();
            if (this.items != null) clone.items = cloneItemsMap(this.items);
            if (this.pageData != null) {
                clone.pageData = new PageData();
                clone.pageData.setCurrentPage(this.pageData.getCurrentPage());
                clone.pageData.setTotalPages(this.pageData.getTotalPages());
                clone.pageData.setPages(new HashMap<>(this.pageData.getPages()));
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public PageData getPageData() {
        return pageData;
    }

    public void setPageData(PageData pageData) {
        this.pageData = pageData;
    }

    public List<Integer> getPaginationSlots() {
        return paginationSlots;
    }

    public Map<String, Item> getItems() {
        return items;
    }

    @Accessors(fluent = true)
    @Getter
    @Setter
    public static class Item implements Cloneable {
        private ConfigurationNode node;
        private String type;
        private CaseDataMaterial material;
        private transient List<Integer> slots;

        @Override
        public Item clone() {
            try {
                Item cloned = (Item) super.clone();
                if (material != null) cloned.material = material.clone();
                if (slots != null) cloned.slots = new ArrayList<>(slots);
                return cloned;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}