package com.jodexindustries.donatecase.common.event;

import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.event.Subscriber;
import com.jodexindustries.donatecase.common.DonateCase;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemClickHandler;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.event.player.CaseInteractEvent;
import com.jodexindustries.donatecase.api.event.player.GuiClickEvent;
import com.jodexindustries.donatecase.api.event.player.JoinEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.gui.items.OPENItemClickHandlerImpl;
import com.jodexindustries.donatecase.common.tools.LocalPlaceholder;
import net.kyori.event.PostOrders;
import net.kyori.event.method.annotation.PostOrder;
import net.kyori.event.method.annotation.Subscribe;

import java.util.Optional;
import java.util.logging.Level;

public class EventListener implements Subscriber {

    private final DonateCase api;

    public EventListener(DonateCase api) {
        this.api = api;
    }

    @Subscribe
    public void onPlayerJoin(JoinEvent event) {
        DCPlayer player = event.player();
        if (player.hasPermission("donatecase.admin")) {
            api.getUpdateChecker().getVersion().thenAcceptAsync(version -> {
                if (version.isNew()) {
                    player.sendMessage(
                            DCTools.prefix(
                                    DCTools.rt(
                                            api.getConfigManager().getMessages().getString("new-update"),
                                            LocalPlaceholder.of("%version%", version.getVersionNumber())
                                    )
                            )
                    );
                }
            });
        }
    }

    @Subscribe
    @PostOrder(PostOrders.LAST)
    public void onGUIClick(GuiClickEvent event) {
        Optional<TypedItem> typedItem = DCAPI.getInstance().getGuiTypedItemManager().getFromString(event.itemType());
        if (!typedItem.isPresent()) return;

        TypedItemClickHandler handler = typedItem.get().click();
        if (handler == null) return;

        handler.onClick(event);
    }

    @Subscribe
    @PostOrder(PostOrders.LAST)
    public void onCaseInteract(CaseInteractEvent event) {
        DCPlayer player = event.player();
        CaseInfo caseInfo = event.caseInfo();
        String caseType = caseInfo.type();

        Optional<CaseDefinition> optional = DCAPI.getInstance().getCaseManager().getByType(caseType);
        if (!optional.isPresent()) {
            player.sendMessage(DCTools.prefix("&cSomething wrong! Contact with server administrator!"));
            DCAPI.getInstance().getPlatform().getLogger().log(Level.WARNING, "Case with type: " + caseType + " not found! Check your Cases.yml for broken cases locations.");
            return;
        }

        CaseDefinition caseDefinition = optional.get();

        if (event.action() == CaseInteractEvent.Action.RIGHT) {
            if (!event.cancelled()) {
                if (DCAPI.getInstance().getAnimationManager().isLocked(caseInfo.location())) {
                    player.sendMessage(DCTools.prefix(DCAPI.getInstance().getConfigManager().getMessages().getString("case-opens")));
                    return;
                }

                switch (caseDefinition.settings().openType()) {
                    case GUI:
                        DCAPI.getInstance().getGUIManager().open(player, caseDefinition, caseInfo.location());
                        break;
                    case BLOCK:
                        OPENItemClickHandlerImpl.executeOpen(caseDefinition, player, caseInfo.location());
                        break;
                }
            }
        }
    }
}