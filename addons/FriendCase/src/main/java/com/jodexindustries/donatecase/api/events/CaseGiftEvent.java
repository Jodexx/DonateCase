package com.jodexindustries.donatecase.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class CaseGiftEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private final Player receiver;
    private final String caseType;
    private final int keys;

    public CaseGiftEvent(@NotNull Player who, Player receiver, String caseType, int keys) {
        super(who);
        this.receiver = receiver;
        this.caseType = caseType;
        this.keys = keys;
    }


    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getReceiver() {
        return receiver;
    }

    public String getCaseType() {
        return caseType;
    }

    public int getKeys() {
        return keys;
    }
}
