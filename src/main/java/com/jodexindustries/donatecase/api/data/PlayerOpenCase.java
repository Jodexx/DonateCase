package com.jodexindustries.donatecase.api.data;

import org.bukkit.Location;

import java.util.UUID;

public class PlayerOpenCase {
    /**
     * Case location
     */
    private final Location location;
    /**
     * Case data
     */
    private final CaseData caseData;
    /**
     * Player UUID
     */
    private final UUID playerUUID;

    /**
     * Default constructor
     *
     * @param location Location of opened case
     * @param caseData Case data
     * @param uuid     Player UUID
     */
    public PlayerOpenCase(Location location, CaseData caseData, UUID uuid) {
        this.location = location;
        this.caseData = caseData;
        this.playerUUID = uuid;
    }

    /**
     * Player UUID
     *
     * @return UUID
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * Get case data
     *
     * @return case data
     */
    public CaseData getCaseData() {
        return caseData;
    }

    /**
     * Get case type
     *
     * @return case type
     */
    @Deprecated
    public String getCaseType() {
        return caseData.getCaseType();
    }

    /**
     * Get location
     *
     * @return location
     */
    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "OpenCase{" +
                "location=" + location +
                ", caseData='" + caseData + '\'' +
                ", playerUUID=" + playerUUID +
                '}';
    }
}
