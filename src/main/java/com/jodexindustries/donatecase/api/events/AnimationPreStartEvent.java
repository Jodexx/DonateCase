package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class AnimationPreStartEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    CaseData caseData;
    Location location;
    String animation;
    CaseData.Item winItem;

    public AnimationPreStartEvent(@NotNull Player who, String animation, CaseData c, Location location, CaseData.Item winItem) {
        super(who);
        this.caseData = c;
        this.location = location;
        this.animation = animation;
        this.winItem = winItem;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }
    @NotNull
    public CaseData getCaseData() {
        return caseData;
    }
    @NotNull
    public String getAnimation() {
        return animation;
    }
    @NotNull
    public CaseData.Item getWinGroup() {
        return winItem;
    }
    public void setWinGroup(CaseData.Item winItem) {
        this.winItem = winItem;
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
