package com.jodexindustries.dcprizepreview.gui;

import com.jodexindustries.dcprizepreview.config.CasePreview;
import com.jodexindustries.dcprizepreview.config.Config;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.events.CaseInteractEvent;
import com.jodexindustries.donatecase.api.events.DonateCaseReloadEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.UUID;

public class EventListener implements Listener {
    private final HashSet<UUID> players = new HashSet<>();
    private final Config config;
    private final DCAPIBukkit api;

    public EventListener(Config config, DCAPIBukkit api) {
        this.config = config;
        this.api = api;
    }

    @EventHandler
    public void onReload(DonateCaseReloadEvent e) {
        if (e.getType() == DonateCaseReloadEvent.Type.CONFIG) {
            config.reload(true);
        }
    }

    @EventHandler
    public void onInteract(CaseInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            String caseType = e.getCaseType();
            CasePreview casePreview = config.previewMap.get(caseType);
            if (casePreview == null) return;

            Player player = e.getPlayer();

            switch (casePreview.type()) {
                case AUTO: {
                    CaseData caseData = api.getCaseManager().get(caseType);
                    if (caseData != null) {
                        Inventory inventory = PreviewGUI.loadGUI(caseData);
                        if (inventory == null) return;

                        player.openInventory(inventory);
                        players.add(player.getUniqueId());
                    }
                    break;
                }

                case COMMAND: {
                    player.chat(casePreview.command());
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (players.contains(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        players.remove(e.getPlayer().getUniqueId());
    }
}
