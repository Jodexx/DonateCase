package com.jodexindustries.donatecase.api.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    public void init(Player player, Location location, UUID uuid, CaseData caseData,
                     CaseData.Item winItem) {
        this.player = player;
        this.location = location;
        this.uuid = uuid;
        this.caseData = caseData;
        this.winItem = winItem;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getUuid() {
        return uuid;
    }

    public CaseData getCaseData() {
        return caseData;
    }

    public CaseData.Item getWinItem() {
        return winItem;
    }
}