package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 *
 * Interface for implement gui management
 */
public interface GUIManager {

    /**
     * Open case gui
     *
     * @param player   Player who opened
     * @param caseData Case data
     * @param location Location where opened
     */
    void open(@NotNull DCPlayer player, @NotNull CaseData caseData, @NotNull CaseLocation location);

    /**
     * Gets map of case gui
     * @return map of case gui
     */
    Map<UUID, CaseGuiWrapper> getPlayersGUI();
}