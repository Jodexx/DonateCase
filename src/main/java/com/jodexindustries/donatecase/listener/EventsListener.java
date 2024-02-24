package com.jodexindustries.donatecase.listener;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.OpenCase;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.events.AnimationRegisteredEvent;
import com.jodexindustries.donatecase.api.events.CaseInteractEvent;
import com.jodexindustries.donatecase.api.events.OpenCaseEvent;
import com.jodexindustries.donatecase.api.events.PreOpenCaseEvent;
import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.gui.CaseGui;
import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.StartAnimation;
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

import static com.jodexindustries.donatecase.DonateCase.*;


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
                new UpdateChecker(DonateCase.instance, 106701).getVersion((version) -> {
                    if (t.getPluginVersion(DonateCase.instance.getDescription().getVersion()) < t.getPluginVersion(version)) {
                        DonateCase.t.msg(p, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("UpdateCheck"), "%version:" + version));
                    }

                });
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void InventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        String pl = p.getName();
        if (Case.playersCases.containsKey(p.getUniqueId())) {
            String caseType = Case.playersCases.get(p.getUniqueId()).getName();
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getInventory().getType() == InventoryType.CHEST && t.getOpenMaterialSlots(caseType).contains(e.getRawSlot())) {
                caseType = t.getOpenMaterialTypeByMapBySlot(caseType, e.getRawSlot());
                if (Case.hasCaseByType(caseType)) {
                    Location block = Case.playersCases.get(p.getUniqueId()).getLocation();
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
                            Sound sound = null;
                            try {
                                sound = Sound.valueOf(customConfig.getConfig().getString("DonatCase.NoKeyWarningSound"));
                            } catch (IllegalArgumentException ignore) {
                            }
                            if (sound == null) {
                                sound = Sound.valueOf("ENTITY_ENDERMEN_TELEPORT");
                            }
                            p.playSound(p.getLocation(), sound, 1.0F, 0.4F);
                            String noKey = casesConfig.getCase(caseType).getString("Messages.NoKey");
                            if (noKey == null) noKey = DonateCase.customConfig.getLang().getString("NoKey");
                            DonateCase.t.msg(p, noKey);
                        }
                    }
                } else {
                    p.closeInventory();
                    DonateCase.t.msg(p, "&cSomething wrong! Contact with server administrator!");
                    DonateCase.instance.getLogger().log(Level.WARNING, "Case with name " + caseType + " not exist!");
                }
            }
        }
    }

    @EventHandler
    public void PlayerInteractEntity(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();
        if(entity instanceof ArmorStand) {
            if (Case.armorStandList.contains(entity)) {
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
                e.setCancelled(true);
                CaseInteractEvent event = new CaseInteractEvent(p, e.getClickedBlock(), caseType);
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    if (!Case.playersCases.containsKey(p.getUniqueId())) {
                        if (!Case.activeCases.containsKey(blockLocation)) {
                            Case.playersCases.put(p.getUniqueId(), new OpenCase(blockLocation, caseType, p.getUniqueId()));
                            try {
                                if(Case.hasCaseByType(caseType)) {
                                    CaseData caseData = Case.getCase(caseType).clone();
                                    new CaseGui(p, caseData);
                                } else {
                                    DonateCase.t.msg(p, "&cSomething wrong! Contact with server administrator!");
                                    DonateCase.instance.getLogger().log(Level.WARNING, "Case with type: " + caseType + " not found! Check your Cases.yml for broken cases locations.");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            DonateCase.t.msg(p, DonateCase.customConfig.getLang().getString("HaveOpenCase"));
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
            Case.playersCases.remove(p.getUniqueId());
        }

    }

    @EventHandler
    public void BlockBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        if (Case.hasCaseByLocation(loc)) {
            e.setCancelled(true);
            DonateCase.t.msg(e.getPlayer(), DonateCase.customConfig.getLang().getString("DestoryDonatCase"));
        }

    }

}
