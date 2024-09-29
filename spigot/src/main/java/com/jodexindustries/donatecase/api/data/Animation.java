package com.jodexindustries.donatecase.api.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Old interface for custom animations implementation
 * @deprecated use {@link JavaAnimation}
 */
@Deprecated
public interface Animation {
    /**
     * Method to start animation
     *
     * @param player   Player who opened case
     * @param uuid     Active case uuid
     * @param location Case location
     * @param caseData Case name
     * @param winItem  winItem
     */
    void start(Player player, Location location, UUID uuid, CaseData caseData, CaseData.Item winItem);
}