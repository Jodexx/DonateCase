package com.jodexindustries.donatecase.spigot.listener;

import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.event.player.ArmorStandCreatorInteractEvent;
import com.jodexindustries.donatecase.spigot.BukkitBackend;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.event.player.CaseInteractEvent;
import com.jodexindustries.donatecase.api.event.player.GuiClickEvent;
import com.jodexindustries.donatecase.api.event.player.JoinEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.spigot.api.platform.BukkitInventory;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import org.bukkit.block.Block;
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
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;

public class EventListener implements Listener {

    private final BukkitBackend backend;

    public EventListener(BukkitBackend backend) {
        this.backend = backend;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework && event.getEntity() instanceof Player && event.getDamager().hasMetadata("case")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAdminJoined(PlayerJoinEvent event) {
        backend.getAPI().getEventBus().post(new JoinEvent(BukkitUtils.fromBukkit(event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        if (!(holder instanceof BukkitInventory)) return;

        CaseGuiWrapper gui = ((BukkitInventory) holder).getWrapper();

        Player player = (Player) e.getWhoClicked();

        e.setCancelled(true);

        String itemType = gui.getDefinition().defaultMenu().getItemTypeBySlot(e.getRawSlot());
        if (itemType == null) return;

        backend.getAPI().getEventBus().post(
                new GuiClickEvent(
                        e.getRawSlot(),
                        BukkitUtils.fromBukkit(player),
                        gui,
                        itemType
                )
        );
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryDrag(InventoryDragEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        if (!(holder instanceof BukkitInventory)) return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerInteractEntity(PlayerInteractAtEntityEvent e) {
        ArmorStandCreator creator = ArmorStandCreator.armorStands.get(e.getRightClicked().getEntityId());
        if (creator == null) return;

        e.setCancelled(true);

        DCPlayer player = BukkitUtils.fromBukkit(e.getPlayer());

        com.jodexindustries.donatecase.api.armorstand.EquipmentSlot hand =
                e.getHand() == EquipmentSlot.HAND ?
                        com.jodexindustries.donatecase.api.armorstand.EquipmentSlot.HAND :
                        com.jodexindustries.donatecase.api.armorstand.EquipmentSlot.OFF_HAND;

        backend.getAPI().getEventBus().post(new ArmorStandCreatorInteractEvent(player, creator, hand));
    }

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;

        Block block = e.getClickedBlock();
        if (block == null) return;

        CaseInfo caseInfo = DCAPI.getInstance().getConfigManager().getCaseStorage().get(BukkitUtils.fromBukkit(block.getLocation()));
        if (caseInfo == null) return;

        CaseInteractEvent.Action action = e.getAction() == Action.RIGHT_CLICK_BLOCK ? CaseInteractEvent.Action.RIGHT : CaseInteractEvent.Action.LEFT;

        e.setCancelled(true);

        DCPlayer player = BukkitUtils.fromBukkit(e.getPlayer());

        backend.getAPI().getEventBus().post(new CaseInteractEvent(player, caseInfo, action));
    }

    @EventHandler
    public void InventoryClose(InventoryCloseEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        if (!(holder instanceof BukkitInventory)) return;

        DCAPI.getInstance().getGUIManager().getMap().remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void BlockBreak(BlockBreakEvent e) {
        CaseLocation location = BukkitUtils.fromBukkit(e.getBlock().getLocation());
        if (DCAPI.getInstance().getConfigManager().getCaseStorage().has(location)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(DCTools.prefix(DCAPI.getInstance().getConfigManager().getMessages().getString("case-destroy-disallow")));
        }

    }

}
