package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.casedata.CCloneable;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 *
 * Interface for implement gui management
 * @param <IY>       The type of inventory representation.
 * @param <L>        The type representing a location in the game world.
 * @param <P>        The type representing the player interacting with the system.
 * @param <C>        The type of case data structure.
 * @param <M>        The type of {@link CaseDataMaterial} representing materials associated with cases.
 */
public interface GUIManager<IY, L, P, C, M extends CCloneable> {

    /**
     * Open case gui
     *
     * @param player   Player who opened
     * @param caseData Case data
     * @param location Location where opened
     */
    void open(@NotNull P player, @NotNull C caseData, @NotNull L location);

    /**
     * Gets map of case gui
     * @return map of case gui
     */
    Map<UUID, CaseGui<IY, L, P, C, M>> getPlayersGUI();
}