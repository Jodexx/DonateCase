package com.jodexindustries.donatecase.listener;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.data.casedata.gui.GUITypedItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.TypedItemClickHandler;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.events.CaseInteractEvent;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.gui.items.OPENItemClickHandlerImpl;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
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
import org.bukkit.inventory.Inventory;

import java.util.UUID;
import java.util.logging.Level;

import static com.jodexindustries.donatecase.DonateCase.instance;


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
            if (instance.api.getConfig().getConfig().getBoolean("DonateCase.UpdateChecker")) {
                instance.updateChecker.getVersion().thenAcceptAsync(version -> {
                    if(DCTools.getPluginVersion(instance.getDescription().getVersion()) < DCTools.getPluginVersion(version.getVersionNumber())) {
                        instance.api.getTools().msg(p, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("new-update"), "%version:" + version));
                    }
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void InventoryClick(InventoryClickEvent e) {
        UUID uuid = e.getWhoClicked().getUniqueId();
        if (instance.api.getGUIManager().getPlayersGUI().containsKey(uuid)) {
            e.setCancelled(true);

            CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit> gui = instance.api.getGUIManager().getPlayersGUI().get(uuid);
            CaseDataBukkit caseData = gui.getCaseData();
            String itemType = caseData.getGui().getItemTypeBySlot(e.getRawSlot());
            CaseGuiClickEvent caseGuiClickEvent = new CaseGuiClickEvent(e.getView(), e.getSlotType(),
                    e.getSlot(), e.getClick(), e.getAction(), gui, itemType);
            Bukkit.getServer().getPluginManager().callEvent(caseGuiClickEvent);

            if (itemType == null) return;

            if (!caseGuiClickEvent.isCancelled()) {

                GUITypedItem<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent> typedItem = instance.api.getGuiTypedItemManager().getFromString(itemType);
                if (typedItem == null) return;

                TypedItemClickHandler<CaseGuiClickEvent> handler = typedItem.getItemClickHandler();
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
            String caseType = Case.getCaseTypeByLocation(blockLocation);
            if (caseType == null) return;

            e.setCancelled(true);

            CaseDataBukkit caseData = instance.api.getCaseManager().getCase(caseType);
            if (caseData == null) {
                instance.api.getTools().msg(p, "&cSomething wrong! Contact with server administrator!");
                Case.getInstance().getLogger().log(Level.WARNING, "Case with type: " + caseType + " not found! Check your Cases.yml for broken cases locations.");
                return;
            }

            CaseInteractEvent event = new CaseInteractEvent(p, block, caseData, e.getAction(), instance.api.getAnimationManager().getActiveCasesByBlock(block));
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (!event.isCancelled()) {
                    if (event.isLocked()) {
                        instance.api.getTools().msg(p, instance.api.getConfig().getLang().getString("case-opens"));
                        return;
                    }

                    switch (caseData.getOpenType()) {
                        case GUI:
                            instance.api.getGUIManager().open(p, caseData, blockLocation);
                            break;
                        case BLOCK:
                            OPENItemClickHandlerImpl.executeOpen(caseData, p, blockLocation);
                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void InventoryClose(InventoryCloseEvent e) {
        instance.api.getGUIManager().getPlayersGUI().remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void BlockBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        if (Case.hasCaseByLocation(loc)) {
            e.setCancelled(true);
            instance.api.getTools().msg(e.getPlayer(), instance.api.getConfig().getLang().getString("case-destroy-disallow"));
        }

    }

}
