package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class JavaAnimationBukkit extends JavaAnimation<CaseDataMaterialBukkit, ItemStack, CaseDataBukkit> {

    private DCAPIBukkit api;
    private Player player;
    private Location location;
    private ConfigurationSection settings;

    /**
     * @param player   Player who opened case
     * @param uuid     Active case uuid
     * @param location Case location
     * @param caseData Case data
     * @param winItem  winItem
     */
    public final void init(DCAPIBukkit api, Player player, Location location, UUID uuid, CaseDataBukkit caseData,
                           CaseDataItem<CaseDataMaterialBukkit, ItemStack> winItem, ConfigurationSection settings) {
        init(uuid, caseData, winItem);
        this.api = api;
        this.player = player;
        this.location = location;
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
     * @since 2.0.2.1
     * @return AnimationManager's DCAPIBukkit instance
     */
    public DCAPIBukkit getApi() {
        return api;
    }
}