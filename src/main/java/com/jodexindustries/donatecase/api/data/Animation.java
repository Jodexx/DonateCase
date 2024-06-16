package com.jodexindustries.donatecase.api.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * An interface to help create animations, <a href="https://wiki.jodexindustries.xyz/docs/DonateCase/API/register-animations">like this</a>
 */

public interface Animation {
    /**
     * @deprecated This is not used anymore!!!
     * Get animation name
     *
     * @return animation name
     */
    @Deprecated
    default String getName() {
        return null;
    }

    /**
     * Method to start animation
     *
     * @param player   Player who opened case
     * @param uuid Active case uuid
     * @param location Case location
     * @param caseData        Case name
     * @param winItem  winItem
     */
    void start(Player player, Location location, UUID uuid, CaseData caseData, CaseData.Item winItem);
}
