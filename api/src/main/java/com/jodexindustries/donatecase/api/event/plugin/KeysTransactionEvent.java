package com.jodexindustries.donatecase.api.event.plugin;

import com.jodexindustries.donatecase.api.event.DCEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kyori.event.Cancellable;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class KeysTransactionEvent extends DCEvent implements Cancellable {

    @NotNull
    private final String caseType;

    @NotNull
    private final String source;

    private int amount;
    private final int before;
    private int after;

    @NotNull
    private TransactionType transactionType;

    private boolean cancelled = false;

    public KeysTransactionEvent(@NotNull final String caseType, @NotNull final String source,
                                final int after, final int before) {
        this.caseType = caseType;
        this.source = source;
        this.before = before;
        after(after);
    }

    public void after(int after) {
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

    @Override
    public boolean cancelled() {
        return cancelled;
    }

    @Override
    public void cancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Represents the type of transaction.
     */
    public enum TransactionType {

        /**
         * A transaction that adds keys
         */
        ADD,
        /**
         * A transaction that removes keys
         */
        REMOVE,
        /**
         * A transaction where no action is performed, and the number of keys remains unchanged
         */
        NOTHING
    }

}