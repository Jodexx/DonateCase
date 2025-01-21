package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class CaseGuiWrapper {

    protected final DCPlayer player;
    protected final CaseData caseData;
    protected final CaseLocation location;
    protected final CaseGui temporary;

    public CaseGuiWrapper(@NotNull DCPlayer player, @NotNull CaseData caseData, @NotNull CaseLocation location) {
        this.player = player;
        this.caseData = caseData;
        this.location = location;
        this.temporary = caseData.getCaseGui().clone();
    }

    /**
     * Loads all items asynchronously
     *
     * @return Void future
     */
    public abstract CompletableFuture<Void> load();

    /**
     * Gets GUI Inventory
     *
     * @return inventory
     */
    @NotNull
    public abstract Object getInventory();

    /**
     * Gets location where GUI opened
     *
     * @return GUI location
     */
    @NotNull
    public CaseLocation getLocation() {
        return location;
    }

    /**
     * Gets player who opened GUI
     *
     * @return player who opened
     */
    @NotNull
    public DCPlayer getPlayer() {
        return player;
    }

    /**
     * Gets GUI CaseData. Can be modified, cause this is clone of original {@link com.jodexindustries.donatecase.api.manager.CaseManager#getCase(String)}
     *
     * @return data
     */
    @NotNull
    public CaseData getCaseData() {
        return caseData;
    }

    /**
     * Gets temporary GUI. Used for updating placeholders, if UpdateRate enabled
     *
     * @return GUI
     */
    @NotNull
    public CaseGui getTemporary() {
        return temporary;
    }

    /**
     * Gets GUI global history data
     *
     * @return global history data
     */
    @NotNull
    public abstract List<CaseData.CaseDataHistory> getGlobalHistoryData();
}
