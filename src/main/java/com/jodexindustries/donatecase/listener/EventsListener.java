package com.jodexindustries.donatecase.listener;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.OpenCase;
import com.jodexindustries.donatecase.api.events.AnimationRegisteredEvent;
import com.jodexindustries.donatecase.api.events.CaseInteractEvent;
import com.jodexindustries.donatecase.api.events.OpenCaseEvent;
import com.jodexindustries.donatecase.api.events.PreOpenCaseEvent;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.gui.CaseGui;
import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.StartAnimation;
import com.jodexindustries.donatecase.tools.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;

import static com.jodexindustries.donatecase.dc.Main.customConfig;
import static com.jodexindustries.donatecase.dc.Main.t;


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
        if (customConfig.getConfig().getBoolean("DonatCase.UpdateChecker")) {
            if (p.hasPermission("donatecase.admin")) {
                new UpdateChecker(Main.instance, 106701).getVersion((version) -> {
                    if (t.getPluginVersion(Main.instance.getDescription().getVersion()) < t.getPluginVersion(version)) {
                        Main.t.msg(p, Main.t.rt(Main.lang.getString("UpdateCheck"), "%version:" + version));
                    }

                });
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void InventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            Player p = (Player)e.getWhoClicked();
            String pl = p.getName();
            if(Case.playerOpensCase.containsKey(p.getUniqueId())) {
                String caseType = Case.playerOpensCase.get(p.getUniqueId()).getName();
                    e.setCancelled(true);
                    if (e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getInventory().getType() == InventoryType.CHEST && t.getOpenMaterialSlots(caseType).contains(e.getRawSlot())) {
                        if (Case.hasCaseByName(caseType)) {
                            Location block = Case.playerOpensCase.get(p.getUniqueId()).getLocation();
                            PreOpenCaseEvent event = new PreOpenCaseEvent(p, caseType, block.getBlock());
                            Bukkit.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                if (Case.getKeys(caseType, pl) >= 1) {
                                    Case.removeKeys(caseType, pl, 1);
                                    new StartAnimation(p, block, caseType);
                                    OpenCaseEvent openEvent = new OpenCaseEvent(p, caseType, block.getBlock());
                                    Bukkit.getServer().getPluginManager().callEvent(openEvent);
                                    p.closeInventory();
                                } else {
                                    p.closeInventory();
                                    p.playSound(p.getLocation(), Sound.valueOf(customConfig.getConfig().getString("DonatCase.NoKeyWarningSound")), 1.0F, 0.4F);
                                    Main.t.msg(p, Main.lang.getString("NoKey"));
                                }
                            }
                        } else {
                            Main.t.msg(p, "&cSomething wrong! Contact with server administrator!");
                        }
                    }
            }
        }

    }

    @EventHandler
    public void PlayerInteractEntity(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();
        if (entity.getType() == EntityType.ARMOR_STAND && Case.listAR.contains(entity)) {
            e.setCancelled(true);
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
            Location blockLocation = e.getClickedBlock().getLocation();
            if (Case.hasCaseByLocation(blockLocation)) {
                String caseType = Case.getCaseTypeByLocation(blockLocation);
                e.setCancelled(true);
                CaseInteractEvent event = new CaseInteractEvent(p, e.getClickedBlock(), caseType);
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    if (!Case.playerOpensCase.containsKey(p.getUniqueId())) {
                        if (!Case.ActiveCase.containsKey(blockLocation)) {
                            Case.playerOpensCase.put(p.getUniqueId(), new OpenCase(blockLocation, caseType, p.getUniqueId()));
                            new CaseGui(p, caseType);
                        } else {
                            Main.t.msg(p, Main.lang.getString("HaveOpenCase"));
                        }
                    } // else player already opened case
                }
            }
        }
    }

    @EventHandler
    public void InventoryClose(InventoryCloseEvent e) {
        Player p = (Player)e.getPlayer();
        if (Case.hasCaseByTitle(e.getView().getTitle())) {
            Case.playerOpensCase.remove(p.getUniqueId());
        }

    }

    @EventHandler
    public void BlockBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        if (Case.hasCaseByLocation(loc)) {
            e.setCancelled(true);
            Main.t.msg(e.getPlayer(), Main.lang.getString("DestoryDonatCase"));
        }

    }

}
