package net.jodexindustries.listener;

import net.jodexindustries.dc.Case;
import net.jodexindustries.dc.DonateCase;
import net.jodexindustries.gui.GuiDonatCase;
import net.jodexindustries.tools.CustomConfig;
import net.jodexindustries.tools.StartAnimation;
import net.jodexindustries.tools.UpdateChecker;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

public class EventsListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework && event.getEntity() instanceof Player && event.getDamager().hasMetadata("case")) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onAdminJoined(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (CustomConfig.getConfig().getBoolean("DonatCase.UpdateChecker")) {
            if (p.hasPermission("donatecase.admin")) {
                new UpdateChecker(DonateCase.instance, 106701).getVersion((version) -> {
                    if (DonateCase.instance.getDescription().getVersion().equals(version)) {
                        DonateCase.t.msg_(p, DonateCase.t.rt(DonateCase.lang.getString("UpdateCheck"), "%version:" + version));
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
                if (e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getInventory().getType() == InventoryType.CHEST && e.getRawSlot() == DonateCase.t.c(5, 3)) {
                    String c = Case.getCaseByTitle(title);
                    if (Case.getKeys(casename, pl) >= 1) {
                        if (DonateCase.openCase.containsKey(p)) {
                            Location block = DonateCase.openCase.get(p);
                            Case.removeKeys(casename, pl, 1);
                            new StartAnimation(p, block, c);
                        }

                        p.closeInventory();
                    } else {
                        p.closeInventory();
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.4F);
                        DonateCase.t.msg(p, DonateCase.lang.getString("NoKey"));
                    }
                }
            }
        }

    }

    @EventHandler
    public void PlayerInteractEntity(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();
        if (entity.getType() == EntityType.ARMOR_STAND && DonateCase.listAR.contains(entity)) {
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            Location loc = e.getClickedBlock().getLocation();
            String loca = loc.toString();
            if (Case.hasCaseByLocation(loca)) {
                e.setCancelled(true);
                if (!StartAnimation.caseOpen.contains(p)) {
                    if (!DonateCase.ActiveCase.containsKey(loc)) {
                        DonateCase.openCase.put(p, loc.clone());
                        new GuiDonatCase(p, Case.getCaseByLocation(loca));
                    } else {
                        DonateCase.t.msg(p, DonateCase.lang.getString("HaveOpenCase"));
                    }
                }
            }
        }

    }

    @EventHandler
    public void InventoryClose(InventoryCloseEvent e) {
        Player p = (Player)e.getPlayer();
        if (Case.hasCaseByTitle(e.getView().getTitle()) && DonateCase.openCase.containsKey(p)) {
            DonateCase.openCase.remove(p);
        }

    }

    @EventHandler
    public void BlockBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        String loca = loc.toString();
        if (Case.hasCaseByLocation(loca)) {
            e.setCancelled(true);
            DonateCase.t.msg(e.getPlayer(), DonateCase.lang.getString("DestoryDonatCase"));
        }

    }
}
