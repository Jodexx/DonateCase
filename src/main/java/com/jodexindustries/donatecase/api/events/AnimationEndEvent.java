package com.jodexindustries.donatecase.api.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class AnimationEndEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    String caseType;
    Location location;
    String animation;
    String winGroup;

    public AnimationEndEvent(@NotNull Player who, String animation, String caseType, Location location, String winGroup) {
        super(who);
        this.caseType = caseType;
        this.location = location;
        this.animation = animation;
        this.winGroup = winGroup;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }
    @NotNull
    public String getCaseType() {
        return caseType;
    }
    @NotNull
    public String getAnimation() {
        return animation;
    }
    @NotNull
    public String getWinGroup() {
        return winGroup;
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
