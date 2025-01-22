package com.jodexindustries.donatecase.listener;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.gui.GuiTypedItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.TypedItemClickHandler;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.events.CaseInteractEvent;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.gui.items.OPENItemClickHandlerImpl;
import com.jodexindustries.donatecase.tools.BukkitUtils;
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

import java.util.UUID;
import java.util.logging.Level;

import static com.jodexindustries.donatecase.BukkitDonateCase.instance;


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
            if (instance.api.getConfig().getConfig().getBoolean("DonateCase.UpdateChecker")) {
                instance.updateChecker.getVersion().thenAcceptAsync(version -> {
                    if(DCTools.getPluginVersion(instance.getDescription().getVersion()) < DCTools.getPluginVersion(version.getVersionNumber())) {
                        instance.api.getPlatform().getTools().msg(p, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("new-update"), "%version:" + version));
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

            CaseGuiWrapper gui = instance.api.getGUIManager().getPlayersGUI().get(uuid);
            CaseData caseData = gui.getCaseData();
            String itemType = caseData.getCaseGui().getItemTypeBySlot(e.getRawSlot());
            CaseGuiClickEvent caseGuiClickEvent = new CaseGuiClickEvent(e.getView(), e.getSlotType(),
                    e.getSlot(), e.getClick(), e.getAction(), gui, itemType);
            Bukkit.getServer().getPluginManager().callEvent(caseGuiClickEvent);

            if (itemType == null) return;

            if (!caseGuiClickEvent.isCancelled()) {

                GuiTypedItem typedItem = instance.api.getGuiTypedItemManager().getFromString(itemType);
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

            CaseData caseData = instance.api.getCaseManager().getCase(caseType);
            if (caseData == null) {
                instance.api.getPlatform().getTools().msg(p, "&cSomething wrong! Contact with server administrator!");
                Case.getInstance().getLogger().log(Level.WARNING, "Case with type: " + caseType + " not found! Check your Cases.yml for broken cases locations.");
                return;
            }

            CaseInteractEvent event = new CaseInteractEvent(p, block, caseData, e.getAction(), instance.api.getAnimationManager().getActiveCasesByBlock(block));
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (!event.isCancelled()) {
                    if (event.isLocked()) {
                        instance.api.getPlatform().getTools().msg(p, instance.api.getConfig().getLang().getString("case-opens"));
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
        CaseLocation location = BukkitUtils.fromBukkit(e.getBlock().getLocation());
        if (DCAPI.getInstance().getConfig().getCaseStorage().has(location)) {
            e.setCancelled(true);
            instance.api.getPlatform().getTools().msg(e.getPlayer(), instance.api.getConfig().getLang().getString("case-destroy-disallow"));
        }

    }

}
