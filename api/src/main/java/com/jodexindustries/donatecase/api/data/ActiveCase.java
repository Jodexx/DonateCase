package com.jodexindustries.donatecase.api.data;

/**
 * Class for saving active cases data
 * @param <B> the type of Block
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

    private boolean keyRemoved;

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
     */
    public B getBlock() {
        return block;
    }

    /**
     * @since 2.0.2.1
     * @return true if key removed from player
     */
    public boolean isKeyRemoved() {
        return keyRemoved;
    }

    /**
     * @since 2.0.2.1
     * @param keyRemoved is removed key from player
     */
    public void setKeyRemoved(boolean keyRemoved) {
        this.keyRemoved = keyRemoved;
    }
}