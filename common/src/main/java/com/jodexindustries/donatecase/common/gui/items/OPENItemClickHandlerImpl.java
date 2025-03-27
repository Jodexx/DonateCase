package com.jodexindustries.donatecase.common.gui.items;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.event.player.GuiClickEvent;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemClickHandler;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.event.player.OpenCaseEvent;
import com.jodexindustries.donatecase.api.event.player.PreOpenCaseEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class OPENItemClickHandlerImpl implements TypedItemClickHandler {


    @Override
    public void onClick(@NotNull GuiClickEvent e) {
        CaseGuiWrapper gui = e.guiWrapper();
        CaseLocation location = gui.getLocation();
        String itemType = e.itemType();
        CaseData caseData = gui.getCaseData();
        String caseType = caseData.caseType();

        if (itemType.contains("_")) {
            String[] parts = itemType.split("_");
            if (parts.length >= 2) {
                caseType = parts[1];
                caseData = DCAPI.getInstance().getCaseManager().get(caseType);
            }
        }


        if (caseData != null) {
            executeOpen(caseData, e.player(), location);
            e.player().closeInventory();
        } else {
            DCAPI.getInstance().getPlatform().getLogger().warning("CaseData " + caseType + " not found. ");
        }

    }

    public static void executeOpen(@NotNull CaseData caseData, @NotNull DCPlayer player, @NotNull CaseLocation location) {
        PreOpenCaseEvent event = new PreOpenCaseEvent(player, caseData, location);
        DCAPI.getInstance().getEventBus().post(event);

        if (event.cancelled()) return;

        checkKeys(event).thenAccept(hasKeys -> {
            if (hasKeys) {
                OpenCaseEvent openEvent = new OpenCaseEvent(player, caseData, location);
                DCAPI.getInstance().getEventBus().post(openEvent);

                if (!openEvent.cancelled())
                    executeOpenWithoutEvent(player, location, caseData, event.ignoreKeys());
            } else {
                DCAPI.getInstance().getActionManager().execute(player, caseData.noKeyActions());
            }
        });
    }

    public static void executeOpenWithoutEvent(DCPlayer player, CaseLocation location, CaseData caseData, boolean ignoreKeys) {
        DCAPI.getInstance().getAnimationManager().start(player, location, caseData).thenAcceptAsync(uuid -> {
            if (uuid != null) {
                ActiveCase activeCase = DCAPI.getInstance().getAnimationManager().getActiveCases().get(uuid);
                if (!ignoreKeys) {
                    DCAPI.getInstance().getCaseKeyManager().remove(caseData.caseType(), player.getName(), 1).thenAcceptAsync(status -> {
                        if (status == DatabaseStatus.COMPLETE) activeCase.keyRemoved(true);
                    });
                } else {
                    activeCase.keyRemoved(true);
                }
            }
        });
    }

    private static CompletableFuture<Boolean> checkKeys(PreOpenCaseEvent event) {
        if (event.ignoreKeys()) return CompletableFuture.completedFuture(true);

        return DCAPI.getInstance()
                .getCaseKeyManager()
                .getAsync(event.caseData().caseType(), event.player().getName())
                .thenApply(keys -> keys >= 1);
    }

}