package com.jodexindustries.donatecase.api.data;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class JavaAnimation implements IAnimation {
    private Player player;
    private Location location;
    private UUID uuid;
    private CaseData caseData;
    private CaseData.Item winItem;
    private ConfigurationSection settings;

    /**
     * @param player   Player who opened case
     * @param uuid     Active case uuid
     * @param location Case location
     * @param caseData Case data
     * @param winItem  winItem
     */
    public final void init(Player player, Location location, UUID uuid, CaseData caseData,
                           CaseData.Item winItem, ConfigurationSection settings) {
        this.player = player;
        this.location = location;
        this.uuid = uuid;
        this.caseData = caseData;
        this.winItem = winItem;
        this.settings = settings;
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

    /**
     * Gets animation settings section
     *
     * @return Section with settings
     * @since 2.2.5.9
     */
    @NotNull
    public final ConfigurationSection getSettings() {
        return settings;
    }
}