package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import com.jodexindustries.donatecase.api.manager.GUIManager;
import com.jodexindustries.donatecase.gui.CaseGuiBukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GUIManagerImpl implements GUIManager<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit> {

    /**
     * Players, who opened cases (open gui)
     */
    public final static Map<UUID, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>> playersGui = new ConcurrentHashMap<>();

    private final Addon addon;

    public GUIManagerImpl(Addon addon) {
        this.addon = addon;
    }


    @Override
    public void open(@NotNull Player player, @NotNull CaseDataBukkit caseData, @NotNull Location location) {
        if (caseData.getGui() != null) {
            if (!playersGui.containsKey(player.getUniqueId())) {
                playersGui.put(player.getUniqueId(), new CaseGuiBukkit(player, caseData.clone(), location));
            } else {
                addon.getLogger().warning("Player " + player.getName() + " already opened case: " + caseData.getCaseType());
            }
        } else {
            addon.getLogger().warning("Player " + player.getName() + " trying to open case: " + caseData.getCaseType() + " without GUI!");
        }
    }

    @Override
    public Map<UUID, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>> getPlayersGUI() {
        return playersGui;
    }
}
