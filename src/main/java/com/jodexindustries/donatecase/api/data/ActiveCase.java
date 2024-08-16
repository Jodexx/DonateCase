package com.jodexindustries.donatecase.api.data;

import org.bukkit.Location;

/**
 * Class for saving active cases data
 */
public class ActiveCase {
    /**
     * Case location
     */
    private final Location location;

    /**
     * Case type
     */
    private final String caseType;

    /**
     * Default constructor
     *
     * @param location Case location
     * @param caseType Case type
     */
    public ActiveCase(Location location, String caseType) {
        this.location = location;
        this.caseType = caseType;
    }

    /**
     * Get case type
     *
     * @return case type
     * @deprecated This method does not match the field name
     * <p> Use {@link ActiveCase#getCaseType()} instead</p>
     */
    @Deprecated
    public String getName() {
        return caseType;
    }

    /**
     * Get case type
     *
     * @return case type
     */
    public String getCaseType() {
        return caseType;
    }

    /**
     * Get case location
     *
     * @return case location
     */

    public Location getLocation() {
        return location;
    }
}