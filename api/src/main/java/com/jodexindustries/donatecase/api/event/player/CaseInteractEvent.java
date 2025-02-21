package com.jodexindustries.donatecase.api.event.player;

import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.kyori.event.Cancellable;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
@Data
public class CaseInteractEvent extends DCEvent implements Cancellable {

    @NotNull
    private final DCPlayer player;
    @NotNull
    private final CaseInfo caseInfo;
    @NotNull
    private final Action action;

    private boolean cancelled;

    @Override
    public boolean cancelled() {
        return cancelled;
    }

    @Override
    public void cancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum Action {

        RIGHT,
        LEFT
    }
}
