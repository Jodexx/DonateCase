package com.jodexindustries.donatecase.api.data;

import java.util.UUID;

/**
 * Class for saving active cases data
 * @param <B> the type of Block
 * @param <P> the type of Player
 * @param <I> the type of case data item
 */
public class ActiveCase<B, P, I> {
    private final UUID uuid;
    private final B block;
    private final P player;
    private final I winItem;
    private final String caseType;
    private boolean isLocked;

    private boolean keyRemoved;

    /**
     * Default constructor
     *
     * @param block    Case block
     * @param caseType Case type
     */
    public ActiveCase(UUID uuid, B block, P player, I winItem, String caseType) {
        this.uuid = uuid;
        this.block = block;
        this.player = player;
        this.winItem = winItem;
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

    public P getPlayer() {
        return player;
    }

    public I getWinItem() {
        return winItem;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }
}