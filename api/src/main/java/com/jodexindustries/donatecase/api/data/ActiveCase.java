package com.jodexindustries.donatecase.api.data;

import org.bukkit.Location;

public class ActiveCase {
    /**
     * Case location
     */
    private final Location location;
    /**
     *  Case name
     */
    private final String name;

    public ActiveCase(Location location, String name) {
        this.location = location;
        this.name = name;
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
}
