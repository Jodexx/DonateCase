package com.jodexindustries.donatecase.common.event;

import com.jodexindustries.donatecase.api.event.Subscriber;
import com.jodexindustries.donatecase.common.DonateCase;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemClickHandler;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.event.player.CaseInteractEvent;
import com.jodexindustries.donatecase.api.event.player.GuiClickEvent;
import com.jodexindustries.donatecase.api.event.player.JoinEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.gui.items.OPENItemClickHandlerImpl;
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
                                    DCTools.rt(api.getConfigManager().getMessages().getString("new-update"), "%version:" + version)
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

        CaseData caseData = DCAPI.getInstance().getCaseManager().get(caseType);
        if (caseData == null) {
            player.sendMessage(DCTools.prefix("&cSomething wrong! Contact with server administrator!"));
            DCAPI.getInstance().getPlatform().getLogger().log(Level.WARNING, "Case with type: " + caseType + " not found! Check your Cases.yml for broken cases locations.");
            return;
        }

        if (event.action() == CaseInteractEvent.Action.RIGHT) {
            if (!event.cancelled()) {
                if (DCAPI.getInstance().getAnimationManager().isLocked(caseInfo.location())) {
                    player.sendMessage(DCTools.prefix(DCAPI.getInstance().getConfigManager().getMessages().getString("case-opens")));
                    return;
                }

                switch (caseData.openType()) {
                    case GUI:
                        DCAPI.getInstance().getGUIManager().open(player, caseData, caseInfo.location());
                        break;
                    case BLOCK:
                        OPENItemClickHandlerImpl.executeOpen(caseData, player, caseInfo.location());
                        break;
                }
            }
        }
    }
}