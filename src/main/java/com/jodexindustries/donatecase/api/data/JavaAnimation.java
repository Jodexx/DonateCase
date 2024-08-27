package com.jodexindustries.donatecase.api.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class JavaAnimation implements IAnimation {
    private Player player;
    private Location location;
    private UUID uuid;
    private CaseData caseData;
    private CaseData.Item winItem;

    /**
     * @param player   Player who opened case
     * @param uuid     Active case uuid
     * @param location Case location
     * @param caseData Case data
     * @param winItem  winItem
     */
    public final void init(Player player, Location location, UUID uuid, CaseData caseData,
                     CaseData.Item winItem) {
        this.player = player;
        this.location = location;
        this.uuid = uuid;
        this.caseData = caseData;
        this.winItem = winItem;
    }

    @NotNull
    public final Player getPlayer() {
        return player;
    }

    @NotNull
    public final Location getLocation() {
        return location;
    }

    @NotNull
    public final UUID getUuid() {
        return uuid;
    }

    @NotNull
    public final CaseData getCaseData() {
        return caseData;
    }

    @NotNull
    public final CaseData.Item getWinItem() {
        return winItem;
    }
}