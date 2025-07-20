package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * Interface responsible for managing and displaying case GUIs.
 */
public interface GUIManager {

    /**
     * Opens a case GUI using legacy {@link CaseData}.
     *
     * @param player   The player who is opening the GUI.
     * @param caseData The case data instance containing configuration and rewards.
     * @param location The physical or logical location where the case is being opened.
     * @deprecated Use {@link #open(DCPlayer, CaseDefinition, CaseLocation)} instead.
     */
    @Deprecated
    void open(@NotNull DCPlayer player, @NotNull CaseData caseData, @NotNull CaseLocation location);

    /**
     * Opens the default GUI of a case.
     *
     * @param player         The player who is opening the GUI.
     * @param caseDefinition The case definition containing GUI settings and rewards.
     * @param location       The location where the case is being opened.
     * @since 2.1.0.5
     */
    default void open(@NotNull DCPlayer player, @NotNull CaseDefinition caseDefinition, @NotNull CaseLocation location) {
        open(player, caseDefinition, caseDefinition.settings().defaultMenu(), location);
    }

    /**
     * Opens a specific menu of the case GUI.
     *
     * @param player         The player who is opening the GUI.
     * @param caseDefinition The case definition containing GUI settings and rewards.
     * @param menuId         The ID of the menu to open.
     * @param location       The location where the case is being opened.
     * @since 2.1.0.5
     */
    void open(@NotNull DCPlayer player, @NotNull CaseDefinition caseDefinition, @NotNull String menuId, @NotNull CaseLocation location);

    /**
     * Opens a specific menu of the case GUI by case type.
     *
     * @param player   The player who is opening the GUI.
     * @param caseType The type identifier of the case.
     * @param menuId   The ID of the menu to open.
     * @param location The location where the case is being opened.
     * @since 2.1.0.5
     */
    void open(@NotNull DCPlayer player, @NotNull String caseType, @NotNull String menuId, @NotNull CaseLocation location);

    /**
     * Retrieves the current map of open case GUIs, mapped by player UUID.
     *
     * @return A map containing player UUIDs and their corresponding {@link CaseGuiWrapper} instances.
     */
    Map<UUID, CaseGuiWrapper> getMap();
}
