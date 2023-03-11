package com.github.cc007.headsplugin.api.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.domain.exceptions.LockingException;

import org.bukkit.entity.Player;

import java.util.Map;

public interface HeadCreator {
    /**
     * Create a head based on a skin from a given Player and head name.
     * The head will also be stored in the local database.
     * This method returns a map, because different head databases could give the head a different UUID
     *
     * @param player      the player who's skin will be used to create the head
     * @param newHeadName the name of the newly created head
     * @return the map of newly created heads (with database name as key)
     * @throws LockingException when the current thread is unable to obtain the lock for starting a transaction
     */
    Map<String, Head> createHead(Player player, String newHeadName);
}
