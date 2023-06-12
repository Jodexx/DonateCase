package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.Case;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PreOpenCaseEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected boolean cancel;
    String caseType;

    public PreOpenCaseEvent(@NotNull final Player who, @NotNull final String caseType) {
        super(who);
        this.caseType = caseType;
        cancel = false;
    }
    public String getCaseType() {
        return Case.getCaseByTitle(caseType);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
