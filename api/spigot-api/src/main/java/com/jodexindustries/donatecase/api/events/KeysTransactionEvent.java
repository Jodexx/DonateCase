package com.jodexindustries.donatecase.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when keys are issued or keys are taken away from a player
 */
public class KeysTransactionEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final String caseType;
    private final String playerName;
    private int amount;
    private final int before;
    private int after;
    private TransactionType transactionType;
    private boolean cancelled = false;

    public KeysTransactionEvent(@NotNull final String caseType, @NotNull final String playerName,
                                final int after, final int before) {
        super(true);
        this.caseType = caseType;
        this.playerName = playerName;
        this.before = before;
        setAfter(after);
    }

    @NotNull
    public String caseType() {
        return caseType;
    }

    @NotNull
    public String playerName() {
        return playerName;
    }

    /**
     * Gets amount of changed keys
     * @return keys
     */
    public int amount() {
        return amount;
    }

    @NotNull
    public TransactionType type() {
        return transactionType;
    }

    /**
     * Gets number of keys before transaction
     * @return number of keys
     */
    public int before() {
        return before;
    }

    /**
     * Gets number of keys after transaction
     * @return number of keys
     */
    public int after() {
        return after;
    }

    /**
     * Sets number of keys after transaction
     * @param after number of keys
     */
    public void setAfter(int after) {
        this.after = after;
        if (this.before == after) {
            this.amount = 0;
            this.transactionType = TransactionType.NOTHING;
        } else if (this.before > after) {
            this.amount = this.before - after;
            this.transactionType = TransactionType.REMOVE;
        } else {
            this.amount = after - this.before;
            this.transactionType = TransactionType.ADD;
        }
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public enum TransactionType {
        ADD,
        REMOVE,
        NOTHING
    }
}