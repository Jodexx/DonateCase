package com.jodexindustries.donatecase.api.data;

import org.bukkit.Location;

public class ActiveCase {
    /**
     * Case location
     */
    private final Location location;

    /**
     *  Case type
     */
    private final String caseType;

    public ActiveCase(Location location, String caseType) {
        this.location = location;
        this.caseType = caseType;
    }

    /**
     * Get case type
     * @deprecated
     * This method does not match the field name
     * <p> Use {@link ActiveCase#getCaseType()} instead</p>
     * @return case type
     *
     */
    public String getName() {
        return caseType;
    }

    /**
     * Get case type
     * @return case type
     */
    public String getCaseType() {
        return caseType;
    }

    /**
     * Get location
     * @return location
     */

    public Location getLocation() {
        return location;
    }
}
