package com.jodexindustries.donatecase.gui.items;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.GUITypedItemManager;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.gui.GUITypedItem;
import com.jodexindustries.donatecase.api.data.gui.TypedItemClickHandler;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.events.OpenCaseEvent;
import com.jodexindustries.donatecase.api.events.PreOpenCaseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OPENItemClickHandlerImpl implements TypedItemClickHandler {

    public static void register(GUITypedItemManager manager) {
        OPENItemClickHandlerImpl handler = new OPENItemClickHandlerImpl();

        GUITypedItem item = manager.builder("OPEN")
                .click(handler)
                .setUpdateMeta(true)
                .setLoadOnCase(true)
                .build();

        manager.registerItem(item);
    }

    @Override
    public void onClick(@NotNull CaseGuiClickEvent e) {
        Location location = e.getLocation();
        String itemType = e.getItemType();
        Player p = (Player) e.getWhoClicked();
        CaseData caseData = e.getCaseData();
        String caseType = caseData.getCaseType();

        if (itemType.contains("_")) {
            String[] parts = itemType.split("_");
            if (parts.length >= 2) {
                caseType = parts[1];
                caseData = Case.getCase(caseType);
            }
        }

        PreOpenCaseEvent event = new PreOpenCaseEvent(p, caseType, location.getBlock());
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            if (caseData != null) {
                executeOpen(caseData, p, location);
            } else {
                Case.getInstance().getLogger().warning("CaseData " + caseType + " not found. ");
            }
        }

        p.closeInventory();
    }

    public static void executeOpen(@NotNull CaseData caseData, Player player, Location location) {
        if (Case.getKeys(caseData.getCaseType(), player.getName()) >= 1) {
            Case.removeKeys(caseData.getCaseType(), player.getName(), 1);

            OpenCaseEvent openEvent = new OpenCaseEvent(player, caseData.getCaseType(), location.getBlock());
            Bukkit.getServer().getPluginManager().callEvent(openEvent);

            if (!openEvent.isCancelled())
                Case.getInstance().api.getAnimationManager().startAnimation(player, location, caseData);
        } else {
            Case.executeActions(player, caseData.getNoKeyActions());
        }
    }

}
