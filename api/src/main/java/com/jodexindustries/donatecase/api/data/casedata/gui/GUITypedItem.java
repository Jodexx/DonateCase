package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class for creating GUI typed item
 * @see TypedItemHandler
 * @see TypedItemClickHandler
 * @since 2.2.4.9
 */
public class GUITypedItem<M extends CaseDataMaterial, G, E> {
    private final String id;
    private final Addon addon;
    private final boolean updateMeta;
    private final boolean loadOnCase;
    private final String description;
    private final TypedItemHandler<M, G> itemHandler;
    private final TypedItemClickHandler<E> itemClickHandler;

    public GUITypedItem(String id, Addon addon, boolean updateMeta, boolean loadOnCase,
                        String description, TypedItemHandler<M, G> itemHandler,
                        TypedItemClickHandler<E> itemClickHandler) {
        this.id = id;
        this.addon = addon;
        this.updateMeta = updateMeta;
        this.loadOnCase = loadOnCase;
        this.description = description;
        this.itemHandler = itemHandler;
        this.itemClickHandler = itemClickHandler;
    }

    /**
     * Gets item id, like: HISTORY, OPEN
     *
     * @return item id
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * Gets item description
     *
     * @return item description
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Gets addon which registered this item
     *
     * @return item addon
     */
    @NotNull
    public Addon getAddon() {
        return addon;
    }

    /**
     * Gets item handler
     *
     * @return item handler
     */
    @Nullable
    public TypedItemHandler<M, G> getItemHandler() {
        return itemHandler;
    }

    /**
     * Gets item click handler
     *
     * @return item click handler
     */
    @Nullable
    public TypedItemClickHandler<E> getItemClickHandler() {
        return itemClickHandler;
    }

    public boolean isUpdateMeta() {
        return updateMeta;
    }

    public boolean isLoadOnCase() {
        return loadOnCase;
    }

    public Builder<M, G, E> toBuilder() {
        Builder<M, G, E> builder = new Builder<>(id, addon);
        builder.updateMeta = updateMeta;
        builder.description = description;
        builder.itemHandler = itemHandler;
        builder.itemClickHandler = itemClickHandler;
        return builder;
    }

    public static class Builder<M extends CaseDataMaterial, G, E> {
        private final Addon addon;
        private final String id;

        private TypedItemHandler<M, G> itemHandler;
        private TypedItemClickHandler<E> itemClickHandler;
        private String description;
        private boolean updateMeta = false;
        private boolean loadOnCase = false;

        /**
         * Default constructor of this builder
         *
         * @param id    Item id, like: HISTORY, OPEN
         * @param addon An addon that will register this item
         */
        public Builder(String id, Addon addon) {
            this.addon = addon;
            this.id = id;
        }

        /**
         * Set item handler
         *
         * @param handler handling item creating
         * @return this builder
         */
        public GUITypedItem.Builder<M, G, E> handler(TypedItemHandler<M, G> handler) {
            this.itemHandler = handler;
            return this;
        }

        /**
         * Set item click handler
         *
         * @param clickHandler handling click on item
         * @return this builder
         */
        public GUITypedItem.Builder<M, G, E> click(TypedItemClickHandler<E> clickHandler) {
            this.itemClickHandler = clickHandler;
            return this;
        }

        /**
         * Set item description
         *
         * @param description information about this item
         * @return this builder
         */
        public GUITypedItem.Builder<M, G, E> description(String description) {
            this.description = description;
            return this;
        }

        public GUITypedItem.Builder<M, G, E> setUpdateMeta(boolean updateMeta) {
            this.updateMeta = updateMeta;
            return this;
        }

        public GUITypedItem.Builder<M, G, E> setLoadOnCase(boolean loadOnCase) {
            this.loadOnCase = loadOnCase;
            return this;
        }

        /**
         * Complete this builder
         *
         * @return completed typed item object
         */
        public GUITypedItem<M, G, E> build() {
            return new GUITypedItem<>(id, addon, updateMeta, loadOnCase, description,
                    itemHandler, itemClickHandler);
        }
    }
}
