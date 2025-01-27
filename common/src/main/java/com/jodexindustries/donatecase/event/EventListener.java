package com.jodexindustries.donatecase.event;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemClickHandler;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.event.player.CaseInteractEvent;
import com.jodexindustries.donatecase.api.event.player.GuiClickEvent;
import com.jodexindustries.donatecase.api.event.player.PlayerJoinEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.gui.items.OPENItemClickHandlerImpl;
import net.kyori.event.PostOrders;
import net.kyori.event.method.annotation.PostOrder;
import net.kyori.event.method.annotation.Subscribe;

import java.util.logging.Level;

public class EventListener {

    private final DonateCase api;

    public EventListener(DonateCase api) {
        this.api = api;
    }

    @Subscribe
    public void onPlayerJoin(PlayerJoinEvent event) {
        DCPlayer player = event.getPlayer();
        if (player.hasPermission("donatecase.admin")) {
            api.getUpdateChecker().getVersion().thenAcceptAsync(version -> {
                if (version.isNew()) {
                    player.sendMessage(
                            DCTools.prefix(
                                    DCTools.rt(api.getConfig().getMessages().getString("new-update"), "%version:" + version)
                            )
                    );
                }
            });
        }
    }

    @Subscribe
    @PostOrder(PostOrders.LAST)
    public void onGUIClick(GuiClickEvent event) {
        TypedItem typedItem = DCAPI.getInstance().getGuiTypedItemManager().getFromString(event.getItemType());
        if (typedItem == null) return;

        TypedItemClickHandler handler = typedItem.getClick();
        if (handler == null) return;

        handler.onClick(event);
    }

    @Subscribe
    @PostOrder(PostOrders.LAST)
    public void onCaseInteract(CaseInteractEvent event) {
        DCPlayer player = event.getPlayer();
        CaseInfo caseInfo = event.getCaseInfo();
        String caseType = caseInfo.getType();

        CaseData caseData = DCAPI.getInstance().getCaseManager().get(caseType);
        if (caseData == null) {
            player.sendMessage(DCTools.prefix("&cSomething wrong! Contact with server administrator!"));
            DCAPI.getInstance().getPlatform().getLogger().log(Level.WARNING, "Case with type: " + caseType + " not found! Check your Cases.yml for broken cases locations.");
            return;
        }

        if (event.getAction() == CaseInteractEvent.Action.RIGHT) {
            if (!event.cancelled()) {
                if (DCAPI.getInstance().getAnimationManager().isLocked(caseInfo.getLocation())) {
                    player.sendMessage(DCTools.prefix(DCAPI.getInstance().getConfig().getMessages().getString("case-opens")));
                    return;
                }

                switch (caseData.getOpenType()) {
                    case GUI:
                        DCAPI.getInstance().getGUIManager().open(player, caseData, caseInfo.getLocation());
                        break;
                    case BLOCK:
                        OPENItemClickHandlerImpl.executeOpen(caseData, player, caseInfo.getLocation());
                        break;
                }
            }
        }
    }
}