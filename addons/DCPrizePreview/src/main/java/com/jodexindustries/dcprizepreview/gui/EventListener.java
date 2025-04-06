package com.jodexindustries.dcprizepreview.gui;

import com.jodexindustries.dcprizepreview.bootstrap.MainAddon;
import com.jodexindustries.dcprizepreview.config.CasePreview;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.event.Subscriber;
import com.jodexindustries.donatecase.api.event.player.CaseInteractEvent;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import net.kyori.event.method.annotation.Subscribe;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.UUID;

public class EventListener implements Subscriber, Listener {

    private final HashSet<UUID> players = new HashSet<>();
    private final MainAddon addon;

    public EventListener(MainAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onReload(DonateCaseReloadEvent e) {
        if (e.type() == DonateCaseReloadEvent.Type.CONFIG) addon.load(true);
    }

    @Subscribe
    public void onInteract(CaseInteractEvent e) {
        if (e.action() == CaseInteractEvent.Action.LEFT) {
            String caseType = e.caseInfo().type();
            CasePreview casePreview = addon.config.previewMap.get(caseType);
            if (casePreview == null) return;

            DCPlayer player = e.player();

            switch (casePreview.type()) {
                case AUTO: {
                    CaseData caseData = addon.api.getCaseManager().get(caseType);
                    if (caseData != null) {
                        Inventory inventory = PreviewGUI.loadGUI(caseData);
                        if (inventory == null) return;

                        player.openInventory(inventory);
                        players.add(player.getUniqueId());
                    }
                    break;
                }

                case COMMAND: {
                    if (player instanceof Player) ((Player) player).chat(casePreview.command());
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
