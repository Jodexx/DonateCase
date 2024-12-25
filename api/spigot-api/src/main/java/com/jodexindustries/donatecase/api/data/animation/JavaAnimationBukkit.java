package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class JavaAnimationBukkit extends JavaAnimation<CaseDataMaterialBukkit, CaseDataBukkit> {

    private DCAPIBukkit api;
    private Player player;
    private Location location;
    private ConfigurationSection settings;

    /**
     * Method for animation initialization
     * @param api      DonateCase API
     * @param player   Player who opened case
     * @param uuid     Active case uuid
     * @param location Case location
     * @param caseData Case data
     * @param winItem  Win item
     * @param settings Animation settings
     */
    public final void init(DCAPIBukkit api, Player player, Location location, UUID uuid, CaseDataBukkit caseData,
                           CaseDataItem<CaseDataMaterialBukkit> winItem, ConfigurationSection settings) {
        init(uuid, caseData, winItem);
        this.api = api;
        this.player = player;
        this.location = location;
        this.settings = settings;
    }

    /**
     * Gets player who opened the case
     * @return the player object
     */
    @NotNull
    public final Player getPlayer() {
        return player;
    }

    /**
     * Gets animation start location
     * @return animation location
     */
    @NotNull
    public final Location getLocation() {
        return location;
    }

    /**
     * @deprecated since 2.0.2.1
     * @return CaseData associated with Bukkit type
     */
    @Deprecated
    @NotNull
    public final CaseDataBukkit getCaseDataBukkit() {
        return getCaseData();
    }

    /**
     * Gets animation settings section
     *
     * @return Section with settings
     */
    @NotNull
    public final ConfigurationSection getSettings() {
        return settings;
    }

    /**
     * Returns DonateCase API
     * @since 2.0.2.1
     * @return AnimationManager's DCAPIBukkit instance
     */
    public DCAPIBukkit getApi() {
        return api;
    }

    public final void preEnd() {
        api.getAnimationManager().animationPreEnd(getUuid());
    }

    public final void end() {
        api.getAnimationManager().animationEnd(getUuid());
    }
}