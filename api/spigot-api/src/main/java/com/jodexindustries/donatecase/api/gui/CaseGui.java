package com.jodexindustries.donatecase.api.gui;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataHistory;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.data.casedata.gui.GUI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CaseGui {
    /**
     * Loads all items asynchronously
     *
     * @return Void future
     */
    CompletableFuture<Void> load();

    /**
     * Gets GUI Inventory
     *
     * @return inventory
     */
    @NotNull
    Inventory getInventory();

    /**
     * Gets location where GUI opened
     *
     * @return GUI location
     */
    @NotNull
    Location getLocation();

    /**
     * Gets player who opened GUI
     *
     * @return player who opened
     */
    @NotNull
    Player getPlayer();

    /**
     * Gets GUI CaseData. Can be modified, cause this is clone of original {@link Case#getCase(String)}
     *
     * @return data
     */
    @NotNull
    CaseDataBukkit getCaseData();

    /**
     * Gets temporary GUI. Used for updating placeholders, if UpdateRate enabled
     *
     * @return GUI
     */
    @NotNull
     GUI<CaseDataMaterialBukkit> getTempGUI();

    /**
     * Gets GUI global history data
     *
     * @return global history data
     */
    @NotNull
    List<CaseDataHistory> getGlobalHistoryData();
}
