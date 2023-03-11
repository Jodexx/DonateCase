package com.github.cc007.headsplugin.api.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.domain.exceptions.LockingException;

import java.util.Set;

public interface CategorySearcher {
    /**
     * get a set of all available categories
     * @return the set of all available categories
     */
    Set<Category> getCategories();

    /**
     * get all heads from a certain category, based on the provided category name
     *
     * @param categoryName the name of the category to get heads from
     * @return the set of all heads from the specified category
     * @throws LockingException when the current thread is unable to obtain the lock for starting a transaction
     */
    Set<Head> getCategoryHeads(String categoryName);

    /**
     * get all heads from a certain {@link Category}
     *
     * @param category the category to get heads from
     * @return the set of all heads from the specified category
     * @throws LockingException when the current thread is unable to obtain the lock for starting a transaction
     */
    default Set<Head> getCategoryHeads(Category category) {
        return getCategoryHeads(category.getName());
    }
}
