package com.jodexindustries.donatecase.api.data;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Class for saving active cases data
 */
public class ActiveCase {
    /**
     * Case block
     */
    private final Block block;

    /**
     * Case type
     */
    private final String caseType;

    /**
     * Default constructor
     *
     * @param block    Case block
     * @param caseType Case type
     */
    public ActiveCase(Block block, String caseType) {
        this.block = block;
        this.caseType = caseType;
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
        return block.getLocation();
    }

    /**
     * Get case block
     *
     * @return case block
     * @since 2.2.5.8
     */
    public Block getBlock() {
        return block;
    }
}