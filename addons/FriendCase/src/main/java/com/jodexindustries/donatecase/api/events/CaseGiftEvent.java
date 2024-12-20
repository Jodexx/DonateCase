package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class CaseGiftEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private final Player receiver;
    private final CaseDataBukkit caseData;
    private final int keys;

    public CaseGiftEvent(@NotNull Player who, Player receiver, CaseDataBukkit caseData, int keys) {
        super(who);
        this.receiver = receiver;
        this.caseData = caseData;
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
        return caseData.getCaseType();
    }

    public CaseDataBukkit getCaseData() {
        return caseData;
    }

    public int getKeys() {
        return keys;
    }
}
