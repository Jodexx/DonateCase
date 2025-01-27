package com.jodexindustries.donatecase.api.event.player;

import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Getter;
import net.kyori.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class CaseInteractEvent extends DCEvent implements Cancellable {

    @Getter
    @NotNull
    private final DCPlayer player;
    @Getter
    @NotNull
    private final CaseInfo caseInfo;
    @Getter
    @NotNull
    private final Action action;

    private boolean cancelled;

    public CaseInteractEvent(@NotNull DCPlayer player, @NotNull CaseInfo caseInfo, @NotNull Action action) {
        this.player = player;
        this.caseInfo = caseInfo;
        this.action = action;
    }

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
