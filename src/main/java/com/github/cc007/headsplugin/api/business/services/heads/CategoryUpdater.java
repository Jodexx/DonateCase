package com.github.cc007.headsplugin.api.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.exceptions.LockingException;

import java.util.Collection;

public interface CategoryUpdater {

    /**
     * Update the specified category
     *
     * @param categoryName the name of the category to update
     * @throws IllegalArgumentException when an unknown category name is provided.
     * @throws LockingException when the current thread is unable to obtain the lock for starting a transaction
     */
    void updateCategory(String categoryName) throws IllegalArgumentException;

    /**
     * Update all categories.
     *
     * @throws LockingException when the current thread is unable to obtain the lock for starting a transaction
     */
    void updateCategories();

    /**
     * Update all categories that haven't recently been updated.
     * This depends on the property headsplugin.categories.update.interval in config.yml
     *
     * @throws LockingException when the current thread is unable to obtain the lock for starting a transaction
     */
    void updateCategoriesIfNecessary();

    /**
     * Update all categories that haven't recently been updated asynchronously.
     * This depends on the property headsplugin.categories.update.interval in config.yml
     *
     * @throws LockingException when the current thread is unable to obtain the lock for starting a transaction
     */
    void updateCategoriesIfNecessaryAsync();

    /**
     * Provides a list of updatable categories
     *
     * @param necessaryOnly return only the categories that are outside of the update interval
     * @return the updatable categories
     */
    Collection<String> getUpdatableCategoryNames(boolean necessaryOnly);

}
