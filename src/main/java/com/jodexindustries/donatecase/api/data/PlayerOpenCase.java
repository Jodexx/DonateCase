package com.jodexindustries.donatecase.api.data;

import org.bukkit.Location;

import java.util.UUID;

public class PlayerOpenCase {
    /**
     * Case location
     */
    private final Location location;
    /**
     *  Case type
     */
    private final String caseType;
    /**
     * Player UUID
     */
    private final UUID playerUUID;

    public PlayerOpenCase(Location location, String caseType, UUID uuid) {
        this.location = location;
        this.caseType = caseType;
        this.playerUUID = uuid;
    }

    /**
     * Player UUID
     * @return UUID
     */

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * Get case type
     * @return case type
     */

    public String getCaseType() {
        return caseType;
    }

    /**
     * Get case type
     * @deprecated
     * This method does not match the field name
     * <p> Use {@link PlayerOpenCase#getCaseType()} instead</p>
     * @return case type
     */
    @Deprecated
    public String getName() {
        return caseType;
    }

    /**
     * Get location
     * @return location
     */

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "OpenCase{" +
                "location=" + location +
                ", name='" + caseType + '\'' +
                ", playerUUID=" + playerUUID +
                '}';
    }
}
