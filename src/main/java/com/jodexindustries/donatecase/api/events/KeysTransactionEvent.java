package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.DatabaseType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when keys are issued or keys are taken away from a player
 * @since 2.2.6.1
 */
public class KeysTransactionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String caseType;
    private final String playerName;
    private final int amount;
    private final int from;
    private final int to;
    private final TransactionType transactionType;

    public KeysTransactionEvent(@NotNull final String caseType, @NotNull final String playerName,
                                final int to, final int from) {
        super(true);
        this.caseType = caseType;
        this.playerName = playerName;
        this.from = from;
        this.to = to;

        if (from == to) {
            amount = 0;
            transactionType = TransactionType.NOTHING;
        } else if (from > to) {
            amount = from - to;
            transactionType = TransactionType.REMOVE;
        } else {
            amount = to - from;
            transactionType = TransactionType.ADD;
        }
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public String caseType() {
        return caseType;
    }

    @NotNull
    public String playerName() {
        return playerName;
    }

    public int amount() {
        return amount;
    }

    @NotNull
    public TransactionType transactionType() {
        return transactionType;
    }

    public DatabaseType type() {
        return Case.getInstance().databaseType;
    }

    /**
     * Gets number of keys before transaction
     * @return number of keys
     */
    public int from() {
        return from;
    }

    /**
     * Gets number of keys after transaction
     * @return number of keys
     */
    public int to() {
        return to;
    }

    public enum TransactionType {
        ADD,
        REMOVE,
        NOTHING
    }
}