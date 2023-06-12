package com.jodexindustries.donatecase.listener;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.events.CaseInteractEvent;
import com.jodexindustries.donatecase.api.events.PreOpenCaseEvent;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.gui.GuiDonatCase;
import com.jodexindustries.donatecase.tools.StartAnimation;
import com.jodexindustries.donatecase.tools.UpdateChecker;
import org.bukkit.Bukkit;
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

import static com.jodexindustries.donatecase.dc.Main.customConfig;


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
                    if (!Main.instance.getDescription().getVersion().equals(version)) {
                        Main.t.msg(p, Main.t.rt(Main.lang.getString("UpdateCheck"), "%version:" + version));
                    }

                });
            }
        }
    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            Player p = (Player)e.getWhoClicked();
            String pl = p.getName();
            String title = e.getView().getTitle();
            String casename = Case.getCaseByTitle(title);
            if (Case.hasCaseByTitle(title)) {
                e.setCancelled(true);
                if (e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getInventory().getType() == InventoryType.CHEST && e.getRawSlot() == Main.t.c(5, 3)) {
                    String c = Case.getCaseByTitle(title);
                    PreOpenCaseEvent event = new PreOpenCaseEvent(p, c);
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        if (Case.getKeys(casename, pl) >= 1) {
                            if (Case.openCase.containsKey(p)) {
                                Location block = Case.openCase.get(p);
                                Case.removeKeys(casename, pl, 1);
                                new StartAnimation(p, block, c);
                            }

                            p.closeInventory();
                        } else {
                            p.closeInventory();
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.4F);
                            Main.t.msg(p, Main.lang.getString("NoKey"));
                        }
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
    public void PlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            Location blockLocation = e.getClickedBlock().getLocation();
            String loca = blockLocation.toString();
            if (Case.hasCaseByLocation(loca)) {
                String caseType = Case.getCaseTypeByLocation(loca);
                e.setCancelled(true);
                CaseInteractEvent event = new CaseInteractEvent(p, e.getClickedBlock(), caseType);
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    if (!StartAnimation.caseOpen.contains(p)) {
                        if (!Case.ActiveCase.containsKey(blockLocation)) {
                            Case.openCase.put(p, blockLocation.clone());
                            new GuiDonatCase(p, caseType);
                        } else {
                            Main.t.msg(p, Main.lang.getString("HaveOpenCase"));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void InventoryClose(InventoryCloseEvent e) {
        Player p = (Player)e.getPlayer();
        if (Case.hasCaseByTitle(e.getView().getTitle()) && Case.openCase.containsKey(p)) {
            Case.openCase.remove(p);
        }

    }

    @EventHandler
    public void BlockBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        String loca = loc.toString();
        if (Case.hasCaseByLocation(loca)) {
            e.setCancelled(true);
            Main.t.msg(e.getPlayer(), Main.lang.getString("DestoryDonatCase"));
        }

    }
}
