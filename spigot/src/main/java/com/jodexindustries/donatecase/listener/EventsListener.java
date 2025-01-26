package com.jodexindustries.donatecase.listener;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemClickHandler;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.gui.items.OPENItemClickHandlerImpl;
import com.jodexindustries.donatecase.tools.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;
import java.util.logging.Level;



public class EventsListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework && event.getEntity() instanceof Player && event.getDamager().hasMetadata("case")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAdminJoined(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (p.hasPermission("donatecase.admin")) {
            // TODO update checker (move to event manager)
//            if (instance.api.getConfig().getConfig().getBoolean("DonateCase.UpdateChecker")) {
//                instance.updateChecker.getVersion().thenAcceptAsync(version -> {
//                    if(DCTools.getPluginVersion(instance.getDescription().getVersion()) < DCTools.getPluginVersion(version.getVersionNumber())) {
//                        instance.api.getPlatform().getTools().msg(p, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("new-update"), "%version:" + version));
//                    }
//                });
//            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void InventoryClick(InventoryClickEvent e) {
        UUID uuid = e.getWhoClicked().getUniqueId();
        if (DCAPI.getInstance().getGUIManager().getMap().containsKey(uuid)) {
            e.setCancelled(true);

            CaseGuiWrapper gui = DCAPI.getInstance().getGUIManager().getMap().get(uuid);
            CaseData caseData = gui.getCaseData();
            String itemType = caseData.getCaseGui().getItemTypeBySlot(e.getRawSlot());
            CaseGuiClickEvent caseGuiClickEvent = new CaseGuiClickEvent(e.getView(), e.getSlotType(),
                    e.getSlot(), e.getClick(), e.getAction(), gui, itemType);
            Bukkit.getServer().getPluginManager().callEvent(caseGuiClickEvent);

            if (itemType == null) return;

            if (!caseGuiClickEvent.isCancelled()) {

                TypedItem typedItem = DCAPI.getInstance().getGuiTypedItemManager().getFromString(itemType);
                if (typedItem == null) return;

                TypedItemClickHandler handler = typedItem.getClick();
                if (handler == null) return;

                handler.onClick(caseGuiClickEvent);
            }
        }
    }

    @EventHandler
    public void PlayerInteractEntity(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();
        if (entity instanceof ArmorStand) {
            if (entity.hasMetadata("case")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        Player p = e.getPlayer();
        Block block = e.getClickedBlock();
        if (block != null) {
            Location blockLocation = block.getLocation();
            CaseInfo caseInfo = DCAPI.getInstance().getConfig().getCaseStorage().get(BukkitUtils.fromBukkit(blockLocation));
            if (caseInfo == null) return;

            String caseType = caseInfo.getType();

            e.setCancelled(true);

            CaseData caseData = DCAPI.getInstance().getCaseManager().get(caseType);
            if (caseData == null) {
                p.sendMessage(DCTools.prefix("&cSomething wrong! Contact with server administrator!"));
                DCAPI.getInstance().getPlatform().getLogger().log(Level.WARNING, "Case with type: " + caseType + " not found! Check your Cases.yml for broken cases locations.");
                return;
            }

            // TODO CaseInteractEvent
//            CaseInteractEvent event = new CaseInteractEvent(p, block, caseData, e.getAction(), DCAPI.getInstance().getAnimationManager().getActiveCasesByBlock(block));
//            Bukkit.getServer().getPluginManager().callEvent(event);
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
//                if (!event.isCancelled()) {
                    if (DCAPI.getInstance().getAnimationManager().isLocked(caseInfo.getLocation())) {
                        p.sendMessage(DCTools.prefix(DCAPI.getInstance().getConfig().getMessages().getString("case-opens")));
                        return;
                    }

                    DCPlayer player = BukkitUtils.fromBukkit(p);

                    switch (caseData.getOpenType()) {
                        case GUI:
                            DCAPI.getInstance().getGUIManager().open(player, caseData, caseInfo.getLocation());
                            break;
                        case BLOCK:
                            OPENItemClickHandlerImpl.executeOpen(caseData, player, caseInfo.getLocation());
                            break;
                    }
//                }
            }
        }
    }

    @EventHandler
    public void InventoryClose(InventoryCloseEvent e) {
        DCAPI.getInstance().getGUIManager().getMap().remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void BlockBreak(BlockBreakEvent e) {
        CaseLocation location = BukkitUtils.fromBukkit(e.getBlock().getLocation());
        if (DCAPI.getInstance().getConfig().getCaseStorage().has(location)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(DCTools.prefix(DCAPI.getInstance().getConfig().getMessages().getString("case-destroy-disallow")));
        }

    }

}
