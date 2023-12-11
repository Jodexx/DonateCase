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

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getName() {
        return name;
    }

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
