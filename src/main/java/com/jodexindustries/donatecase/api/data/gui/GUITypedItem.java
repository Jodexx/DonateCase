package com.jodexindustries.donatecase.api.data.gui;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.jetbrains.annotations.Nullable;

/**
 * @since 2.2.4.9
 */
public class GUITypedItem {
    private final String id;
    private final Addon addon;

    private String description;
    private TypedItemHandler itemHandler;
    private TypedItemClickHandler itemClickHandler;

    public GUITypedItem(String id, Addon addon) {
        this.id = id;
        this.addon = addon;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Addon getAddon() {
        return addon;
    }

    @Nullable
    public TypedItemHandler getItemHandler() {
        return itemHandler;
    }

    @Nullable
    public TypedItemClickHandler getItemClickHandler() {
        return itemClickHandler;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setItemHandler(TypedItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    public void setItemClickHandler(TypedItemClickHandler itemClickHandler) {
        this.itemClickHandler = itemClickHandler;
    }

    public static class Builder {
        private final Addon addon;
        private final String id;

        private TypedItemHandler itemHandler;
        private TypedItemClickHandler itemClickHandler;
        private String description;

        public Builder(String id, Addon addon) {
            this.addon = addon;
            this.id = id;
        }

        public GUITypedItem.Builder handler(TypedItemHandler handler) {
            this.itemHandler = handler;
            return this;
        }

        public GUITypedItem.Builder click(TypedItemClickHandler clickHandler) {
            this.itemClickHandler = clickHandler;
            return this;
        }

        public GUITypedItem.Builder description(String description) {
            this.description = description;
            return this;
        }

        public GUITypedItem build() {
            GUITypedItem guiTypedItem = new GUITypedItem(id, addon);
            guiTypedItem.setDescription(description);
            guiTypedItem.setItemHandler(itemHandler);
            guiTypedItem.setItemClickHandler(itemClickHandler);
            return guiTypedItem;
        }
    }
}
