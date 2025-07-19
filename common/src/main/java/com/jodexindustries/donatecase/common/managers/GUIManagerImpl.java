package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.common.DonateCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.manager.GUIManager;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.common.platform.BackendPlatform;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GUIManagerImpl implements GUIManager {

    public final Map<UUID, CaseGuiWrapper> playersGui = new ConcurrentHashMap<>();

    private final BackendPlatform platform;

    public GUIManagerImpl(DonateCase api) {
        this.platform = api.getPlatform();
    }

    @Override
    public void open(@NotNull DCPlayer player, @NotNull CaseData caseData, @NotNull CaseLocation location) {
        if (caseData.caseGui() != null) {
            if (!playersGui.containsKey(player.getUniqueId())) {
                playersGui.put(player.getUniqueId(), platform.createGui(player, caseData.clone(), location));
            } else {
                platform.getLogger().warning("Player " + player.getName() + " already opened case: " + caseData.caseType());
            }
        } else {
            platform.getLogger().warning("Player " + player.getName() + " trying to open case: " + caseData.caseType() + " without GUI!");
        }
    }

    @Override
    public Map<UUID, CaseGuiWrapper> getMap() {
        return playersGui;
    }
}
