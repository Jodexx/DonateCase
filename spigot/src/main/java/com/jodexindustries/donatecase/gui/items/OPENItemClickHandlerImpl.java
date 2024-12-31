package com.jodexindustries.donatecase.gui.items;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.data.casedata.gui.GUITypedItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.TypedItemClickHandler;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.events.OpenCaseEvent;
import com.jodexindustries.donatecase.api.events.PreOpenCaseEvent;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import com.jodexindustries.donatecase.api.manager.GUITypedItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class OPENItemClickHandlerImpl implements TypedItemClickHandler<CaseGuiClickEvent> {

    public static void register(GUITypedItemManager<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent> manager) {
        OPENItemClickHandlerImpl handler = new OPENItemClickHandlerImpl();

        GUITypedItem<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent> item = manager.builder("OPEN")
                .description("Type to open the case")
                .click(handler)
                .setUpdateMeta(true)
                .setLoadOnCase(true)
                .build();

        manager.registerItem(item);
    }

    @Override
    public void onClick(@NotNull CaseGuiClickEvent e) {
        CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit> gui = e.getGui();
        Location location = gui.getLocation();
        String itemType = e.getItemType();
        Player p = (Player) e.getWhoClicked();
        CaseDataBukkit caseData = gui.getCaseData();
        String caseType = caseData.getCaseType();

        if (itemType.contains("_")) {
            String[] parts = itemType.split("_");
            if (parts.length >= 2) {
                caseType = parts[1];
                caseData = Case.getInstance().api.getCaseManager().getCase(caseType);
            }
        }


        if (caseData != null) {
            executeOpen(caseData, p, location);
        } else {
            Case.getInstance().getLogger().warning("CaseData " + caseType + " not found. ");
        }

        p.closeInventory();
    }

    public static void executeOpen(@NotNull CaseDataBukkit caseData, @NotNull Player player, @NotNull Location location) {
        PreOpenCaseEvent event = new PreOpenCaseEvent(player, caseData, location.getBlock());
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            if (checkKeys(event)) {
                OpenCaseEvent openEvent = new OpenCaseEvent(player, caseData, location.getBlock());
                Bukkit.getServer().getPluginManager().callEvent(openEvent);

                if (!openEvent.isCancelled()) executeOpenWithoutEvent(player, location, caseData, event.isIgnoreKeys());
            } else {
                Case.getInstance().api.getActionManager().executeActions(player, caseData.getNoKeyActions());
            }
        }
    }

    public static void executeOpenWithoutEvent(Player player, Location location, CaseDataBukkit caseData, boolean ignoreKeys) {
        Case.getInstance().api.getAnimationManager().startAnimation(player, location, caseData).thenAcceptAsync(uuid -> {
            if(uuid != null) {
                ActiveCase<Block, Player, CaseDataItem<CaseDataMaterialBukkit>> activeCase = Case.getInstance().api.getAnimationManager().getActiveCases().get(uuid);
                if(!ignoreKeys) {
                    Case.getInstance().api.getCaseKeyManager().removeKeys(caseData.getCaseType(), player.getName(), 1).thenAcceptAsync(status -> {
                        activeCase.setKeyRemoved(true);
                    });
                } else {
                    activeCase.setKeyRemoved(true);
                }
            }
        });
    }

    private static boolean checkKeys(PreOpenCaseEvent event) {
        if (event.isIgnoreKeys()) return true;
        return Case.getInstance().api.getCaseKeyManager().getKeys(event.getCaseType(), event.getPlayer().getName()) >= 1;
    }
}