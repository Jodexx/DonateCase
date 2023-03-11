package com.github.cc007.headsplugin.api.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.domain.exceptions.LockingException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HeadSearcher {
    /**
     * Get the number of times a certain search term is used.
     *
     * @param searchTerm the search term
     * @return the number of times the search term is used
     * @throws LockingException when the current thread is unable to obtain the lock for starting a transaction
     */
    long getSearchCount(String searchTerm);

    /**
     * Get the {@link Head} for the provided head owner {@link UUID}, as {@link Optional}
     * @param headOwner the head owner UUID
     * @return the optional head
     * @throws LockingException when the current thread is unable to obtain the lock for starting a transaction
     */
    Optional<Head> getHead(UUID headOwner);

    /**
     * Get all {@link Head}s for a given search term.
     * This method will also update the cached heads for the search term if necessary.
     *
     * @param searchTerm the search term to use when searching for heads
     * @return a list of all heads that match the search term.
     * @throws LockingException when the current thread is unable to obtain the lock for starting a transaction
     */
    List<Head> getHeads(String searchTerm);
}