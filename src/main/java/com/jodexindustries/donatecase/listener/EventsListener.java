package com.jodexindustries.donatecase.listener;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.PlayerOpenCase;
import com.jodexindustries.donatecase.api.events.*;
import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.logging.Level;


public class EventsListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework && event.getEntity() instanceof Player && event.getDamager().hasMetadata("case")) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onAdminJoined(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (Case.getCustomConfig().getConfig().getBoolean("DonatCase.UpdateChecker")) {
            if (p.hasPermission("donatecase.admin")) {
                new UpdateChecker(Case.getInstance(), 106701).getVersion((version) -> {
                    if (Tools.getPluginVersion(Case.getInstance().getDescription().getVersion()) < Tools.getPluginVersion(version)) {
                        Tools.msg(p, Tools.rt(Case.getCustomConfig().getLang().getString("new-update"), "%version:" + version));
                    }
                });
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void InventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        String playerName = p.getName();
        if (Case.playersGui.containsKey(p.getUniqueId())) {
            PlayerOpenCase playerOpenCase = Case.playersGui.get(p.getUniqueId());
            String caseType = playerOpenCase.getCaseType();
            e.setCancelled(true);
            boolean isOpenItem = Tools.getOpenMaterialSlots(caseType).contains(e.getRawSlot());
            Location location = playerOpenCase.getLocation();
            CaseGuiClickEvent caseGuiClickEvent = new CaseGuiClickEvent(e.getView(), e.getSlotType(), e.getSlot(), e.getClick(), e.getAction(), location, caseType, isOpenItem);
            Bukkit.getServer().getPluginManager().callEvent(caseGuiClickEvent);
            if (e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getInventory().getType() == InventoryType.CHEST && isOpenItem) {
                String openMaterialType = Tools.getOpenMaterialTypeByMapBySlot(caseType, e.getRawSlot());
                if(openMaterialType != null) caseType = openMaterialType;

                if (Case.hasCaseByType(caseType)) {
                    PreOpenCaseEvent event = new PreOpenCaseEvent(p, caseType, location.getBlock());
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        if (Case.getKeys(caseType, playerName) >= 1) {
                            Case.removeKeys(caseType, playerName, 1);

                            OpenCaseEvent openEvent = new OpenCaseEvent(p, caseType, location.getBlock());
                            Bukkit.getServer().getPluginManager().callEvent(openEvent);

                            p.closeInventory();

                            Case.getInstance().api.getAnimationManager().startAnimation(p, location, caseType);
                        } else {
                            p.closeInventory();
                            Sound sound = Sound.ENTITY_ENDERMAN_TELEPORT;
                            try {
                                sound = Sound.valueOf(Case.getCustomConfig().getConfig().getString("DonatCase.NoKeyWarningSound"));
                            } catch (IllegalArgumentException ignore) {}
                            p.playSound(p.getLocation(), sound, 1.0F, 0.4F);
                            String noKey = Case.getCasesConfig().getCase(caseType).getString("Messages.NoKey");
                            if (noKey == null) noKey = Case.getCustomConfig().getLang().getString("no-keys");
                            Tools.msg(p, noKey);
                        }
                    }
                } else {
                    p.closeInventory();
                    Tools.msg(p, "&cSomething wrong! Contact with server administrator!");
                    Case.getInstance().getLogger().log(Level.WARNING, "Case with name " + caseType + " not exist!");
                }
            }
        }
    }

    @EventHandler
    public void PlayerInteractEntity(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();
        if(entity instanceof ArmorStand) {
            if (entity.hasMetadata("case")) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onAnimationRegistered(AnimationRegisteredEvent e) {
        if(!e.getAnimationName().startsWith("DEFAULT")) {
            Logger.log(ChatColor.GREEN + "Registered new animation with name: " + ChatColor.RED + e.getAnimationName() + ChatColor.GREEN + " from " + ChatColor.RED + e.getAnimationPluginName());
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void PlayerInteract(PlayerInteractEvent e) {
        if(e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            assert e.getClickedBlock() != null;
            Location blockLocation = e.getClickedBlock().getLocation();
            if (Case.hasCaseByLocation(blockLocation)) {
                String caseType = Case.getCaseTypeByLocation(blockLocation);
                if(caseType == null) return;
                e.setCancelled(true);
                CaseInteractEvent event = new CaseInteractEvent(p, e.getClickedBlock(), caseType);
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    if (!Case.activeCasesByLocation.containsKey(blockLocation)) {
                        if (Case.hasCaseByType(caseType)) {
                            CaseData caseData = Case.getCase(caseType);
                            if(caseData == null) return;
                            Case.openGui(p, caseData, blockLocation);
                        } else {
                            Tools.msg(p, "&cSomething wrong! Contact with server administrator!");
                            Case.getInstance().getLogger().log(Level.WARNING, "Case with type: " + caseType + " not found! Check your Cases.yml for broken cases locations.");
                        }
                    } else {
                        Tools.msg(p, Case.getCustomConfig().getLang().getString("case-opens"));
                    }
                }
            }
        }
    }

    @EventHandler
    public void InventoryClose(InventoryCloseEvent e) {
        Player p = (Player)e.getPlayer();
        if (Case.hasCaseByTitle(e.getView().getTitle())) {
            Case.playersGui.remove(p.getUniqueId());
        }

    }

    @EventHandler
    public void BlockBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        if (Case.hasCaseByLocation(loc)) {
            e.setCancelled(true);
            Tools.msg(e.getPlayer(), Case.getCustomConfig().getLang().getString("case-destroy-disallow"));
        }

    }

}
