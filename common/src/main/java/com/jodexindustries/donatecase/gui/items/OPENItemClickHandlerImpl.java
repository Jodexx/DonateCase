package com.jodexindustries.donatecase.gui.items;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.event.GUIClickEvent;
import com.jodexindustries.donatecase.api.data.casedata.gui.TypedItemClickHandler;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.jetbrains.annotations.NotNull;

public class OPENItemClickHandlerImpl implements TypedItemClickHandler {


    @Override
    public void onClick(@NotNull GUIClickEvent e) {
        CaseGuiWrapper gui = e.getCaseGUI();
        CaseLocation location = gui.getLocation();
        String itemType = e.getItemType();
        CaseData caseData = gui.getCaseData();
        String caseType = caseData.getCaseType();

        if (itemType.contains("_")) {
            String[] parts = itemType.split("_");
            if (parts.length >= 2) {
                caseType = parts[1];
                caseData = DCAPI.getInstance().getCaseManager().get(caseType);
            }
        }


        if (caseData != null) {
            executeOpen(caseData, e.getPlayer(), location);
            e.getPlayer().closeInventory();
        } else {
            DCAPI.getInstance().getPlatform().getLogger().warning("CaseData " + caseType + " not found. ");
        }

    }

    public static void executeOpen(@NotNull CaseData caseData, @NotNull DCPlayer player, @NotNull CaseLocation location) {
        // TODO Events
//        PreOpenCaseEvent event = new PreOpenCaseEvent(player, caseData, location.getBlock());
//        Bukkit.getServer().getPluginManager().callEvent(event);
//        if (!event.isCancelled()) {
//            if (event.isIgnoreKeys() || checkKeys(caseData.getCaseType(), player.getName())) {
//                OpenCaseEvent openEvent = new OpenCaseEvent(player, caseData, location.getBlock());
//                Bukkit.getServer().getPluginManager().callEvent(openEvent);
//
//                if (!openEvent.isCancelled())
//                    executeOpenWithoutEvent(player, location, caseData, event.isIgnoreKeys());
//            } else {
//                DCAPI.getInstance().getActionManager().executeActions(player, caseData.getNoKeyActions());
//            }
//        }

            if (checkKeys(caseData.getCaseType(), player.getName())) {
                executeOpenWithoutEvent(player, location, caseData, false);
            } else {
                DCAPI.getInstance().getActionManager().execute(player, caseData.getNoKeyActions());
            }
    }

    public static void executeOpenWithoutEvent(DCPlayer player, CaseLocation location, CaseData caseData, boolean ignoreKeys) {
        DCAPI.getInstance().getAnimationManager().start(player, location, caseData).thenAcceptAsync(uuid -> {
            if(uuid != null) {
                ActiveCase activeCase = DCAPI.getInstance().getAnimationManager().getActiveCases().get(uuid);
                if(!ignoreKeys) {
                    DCAPI.getInstance().getCaseKeyManager().remove(caseData.getCaseType(), player.getName(), 1).thenAcceptAsync(status -> {
                        if(status == DatabaseStatus.COMPLETE) activeCase.setKeyRemoved(true);
                    });
                } else {
                    activeCase.setKeyRemoved(true);
                }
            }
        });
    }

    private static boolean checkKeys(String caseType, String player) {
        return DCAPI.getInstance().getCaseKeyManager().get(caseType, player) >= 1;
    }
}