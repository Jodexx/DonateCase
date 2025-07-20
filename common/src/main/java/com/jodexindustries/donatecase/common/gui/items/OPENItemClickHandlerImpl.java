package com.jodexindustries.donatecase.common.gui.items;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
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
        CaseDefinition definition = gui.getDefinition();
        String caseType = definition.settings().type();

        if (itemType.contains("_")) {
            String[] parts = itemType.split("_");
            if (parts.length >= 2) {
                caseType = parts[1];
                definition = DCAPI.getInstance().getCaseManager().getByType(caseType).orElse(null);
            }
        }

        if (definition != null) {
            executeOpen(definition, e.player(), location);
            e.player().closeInventory();
        } else {
            DCAPI.getInstance().getPlatform().getLogger().warning("Case with type '" + caseType + "' not found. ");
        }

    }

    public static void executeOpen(@NotNull CaseDefinition definition, @NotNull DCPlayer player, @NotNull CaseLocation location) {
        PreOpenCaseEvent event = new PreOpenCaseEvent(player, definition, location);
        DCAPI.getInstance().getEventBus().post(event);

        if (event.cancelled()) return;

        checkKeys(event).thenAccept(hasKeys -> {
            if (hasKeys) {
                OpenCaseEvent openEvent = new OpenCaseEvent(player, definition, location);
                DCAPI.getInstance().getEventBus().post(openEvent);

                if (!openEvent.cancelled())
                    executeOpenWithoutEvent(player, location, definition, event.ignoreKeys());
            } else {
                DCAPI.getInstance().getActionManager().execute(player, definition.settings().noKeyActions());
            }
        });
    }

    @Deprecated
    public static void executeOpen(@NotNull CaseData caseData, @NotNull DCPlayer player, @NotNull CaseLocation location) {
        executeOpen(CaseData.toDefinition(caseData), player, location);
    }

    public static void executeOpenWithoutEvent(DCPlayer player, CaseLocation location, CaseDefinition definition, boolean ignoreKeys) {
        DCAPI.getInstance().getAnimationManager().start(player, location, definition).thenAcceptAsync(uuid -> {
            if (uuid != null) {
                ActiveCase activeCase = DCAPI.getInstance().getAnimationManager().getActiveCases().get(uuid);
                if (!ignoreKeys) {
                    DCAPI.getInstance().getCaseKeyManager().remove(definition.settings().type(), player.getName(), 1).thenAcceptAsync(status -> {
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
                .getAsync(event.definition().settings().type(), event.player().getName())
                .thenApply(keys -> keys >= 1);
    }

}