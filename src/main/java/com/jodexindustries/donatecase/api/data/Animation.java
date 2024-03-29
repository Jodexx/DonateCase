package com.jodexindustries.donatecase.api.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * An interface to help create animations, <a href="https://wiki.jodexindustries.space/docs/DonateCase/API/register-animations">like this</a>
 */

public interface Animation {
    /**
     * Get animation name
     * @return animation name
     */
    String getName();

    /**
     * Method to start animation
     *
     * @param player   Player who opened case
     * @param location Case location
     * @param c        Case name
     * @param winItem  winItem
     */
    void start(Player player, Location location, CaseData c, CaseData.Item winItem);
}
