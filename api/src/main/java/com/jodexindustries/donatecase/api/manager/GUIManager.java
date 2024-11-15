package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.gui.CaseGui;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public interface GUIManager<Inventory, L, Player, C, M> {

    /**
     * Open case gui
     *
     * @param player Player who opened
     * @param caseData Case data
     * @param location Location where opened
     */
    void open(@NotNull Player player, @NotNull C caseData, @NotNull L location);

    Map<UUID, CaseGui<Inventory, L, Player, C, M>> getPlayersGUI();
}
