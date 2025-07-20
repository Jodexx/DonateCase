package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMenu;
import com.jodexindustries.donatecase.common.DonateCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.manager.GUIManager;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.common.platform.BackendPlatform;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GUIManagerImpl implements GUIManager {

    public final Map<UUID, CaseGuiWrapper> playersGui = new ConcurrentHashMap<>();

    private final DonateCase api;
    private final BackendPlatform platform;

    public GUIManagerImpl(DonateCase api) {
        this.api = api;
        this.platform = api.getPlatform();
    }

    @Override
    @Deprecated
    public void open(@NotNull DCPlayer player, @NotNull CaseData caseData, @NotNull CaseLocation location) {
        CaseDefinition definition = CaseData.toDefinition(caseData);

        open(player, definition, location);
    }

    @Override
    public void open(@NotNull DCPlayer player, @NotNull CaseDefinition caseDefinition, @NotNull String menuId, @NotNull CaseLocation location) {
        Optional<CaseMenu> optionalMenu = caseDefinition.getMenuById(menuId);
        if (!optionalMenu.isPresent()) {
            platform.getLogger().warning("Player '" + player.getName() + "' attempted to open case type '" + caseDefinition.settings().type() + "' with missing or undefined GUI (menu ID: '" + menuId + "').");
            return;
        }

        if (playersGui.containsKey(player.getUniqueId())) {
            platform.getLogger().fine("Player '" + player.getName() + "' already has an open GUI. Skipping duplicate open.");
            return;
        }

        playersGui.put(player.getUniqueId(), platform.createGui(player, caseDefinition, optionalMenu.get(), location));
    }

    @Override
    public void open(@NotNull DCPlayer player, @NotNull String caseType, @NotNull String menuId, @NotNull CaseLocation location) {
        Optional<CaseDefinition> optional = api.getCaseManager().getByType(caseType);
        if (!optional.isPresent()) {
            platform.getLogger().warning("Player '" + player.getName() + "' attempted to open a non-existent case type: '" + caseType + "'.");
            return;
        }

        open(player, optional.get(), menuId, location);
    }

    @Override
    public Map<UUID, CaseGuiWrapper> getMap() {
        return playersGui;
    }
}
