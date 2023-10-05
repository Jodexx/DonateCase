package com.jodexindustries.donatecase.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class OpenCaseEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    protected boolean cancel;
    String caseType;
    Block block;

    public OpenCaseEvent(@NotNull final Player who, @NotNull final String caseType, Block block) {
        super(who);
        this.caseType = caseType;
        this.block = block;
        cancel = false;
    }
    public String getCaseType() {
        return caseType;
    }
    public Block getBlock() {
        return block;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
