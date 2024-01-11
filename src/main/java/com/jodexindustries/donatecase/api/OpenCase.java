package com.jodexindustries.donatecase.api;

import org.bukkit.Location;

import java.util.UUID;

public class OpenCase {
    /**
     * Case location
     */
    private final Location location;
    /**
     *  Case name
     */
    private final String name;
    /**
     * Player UUID
     */
    private final UUID playerUUID;

    public OpenCase(Location location, String name, UUID uuid) {
        this.location = location;
        this.name = name;
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
     * Get player name
     * @return name
     */

    public String getName() {
        return name;
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
                ", name='" + name + '\'' +
                ", playerUUID=" + playerUUID +
                '}';
    }
}
