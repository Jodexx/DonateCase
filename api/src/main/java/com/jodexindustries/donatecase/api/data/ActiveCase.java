package com.jodexindustries.donatecase.api.data;

/**
 * Class for saving active cases data
 */
public class ActiveCase<B> {
    /**
     * Case block
     */
    private final B block;

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
    public ActiveCase(B block, String caseType) {
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
     * Get case block
     *
     * @return case block
     * @since 2.2.5.8
     */
    public B getBlock() {
        return block;
    }
}