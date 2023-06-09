package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.CustomConfig;
import com.jodexindustries.donatecase.tools.StartAnimation;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Wheel {
    public static List<Player> caseOpen = new ArrayList<>();
    public Wheel(final Player player, Location location, final String c) {
        final Location lAC = location.clone();
        Main.ActiveCase.put(lAC, c);
        caseOpen.add(player);

        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (Main.openCase.containsKey(pl) && Main.t.isHere(location, Main.openCase.get(pl))) {
                pl.closeInventory();
            }
        }
        final String winGroup = Main.t.getRandomGroup(c);
        final String winGroupId = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Item.ID").toUpperCase();
        final String winGroupDisplayName = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Item.DisplayName");
        location.add(0.5, 1, 0.5);
        location.setYaw(-70.0F);
        final ArmorStand as = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        as.setVisible(false);
        Main.listAR.add(as);
        as.setGravity(false);
        as.setSmall(true);
        as.setCustomNameVisible(true);
        (new BukkitRunnable() {
            int i; // count of ticks
            Location l;

            public void run() {
                Material material;
                ItemStack winItem = null;
                Location las = as.getLocation().clone();
                las.setYaw(las.getYaw() + 20.0F);
                as.teleport(las);
                if(!winGroupId.startsWith("HEAD") && !winGroupId.startsWith("BASE64")) {
                    material = Material.getMaterial(winGroupId);
                    if (material == null) {
                        material = Material.STONE;
                    }
                    winItem = Main.t.createItem(material, 1, 0, winGroupDisplayName);
                }
                if(winGroupId.startsWith("HEAD")) {
                    String[] parts = winGroupId.split(":");
                    winItem = Main.t.getPlayerHead(parts[1], winGroupDisplayName);
                }
                if(winGroupId.startsWith("HDB")) {
                    String[] parts = winGroupId.split(":");
                    String id = parts[1];
                    if(Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                        winItem  = Main.t.getHDBSkull(id, winGroupDisplayName);
                    } else {
                        winItem = new ItemStack(Material.STONE);
                    }
                }
                if (this.i == 0) {
                    this.l = as.getLocation();
                }
                if (this.i >= 14) {
                    this.l.setYaw(las.getYaw());
                    if (this.i == 32) {
                        // win item and title
                        as.setHelmet(winItem);
                        as.setCustomName(winItem.getItemMeta().getDisplayName());
                        Main.t.onCaseOpenFinish(c, player, false, winGroup);
                        lAC.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, lAC, 0);
                        lAC.getWorld().playSound(lAC, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    }
                }

                // change random item
                if (this.i <= 30 && (this.i % 2 == 0 )) {
                    final String winGroup2 = Main.t.getRandomGroup(c);
                    ItemStack winItem2 = null;
                    Material material2;
                    final String winGroupDisplayName2 = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup2 + ".Item.DisplayName");
                    final String winGroup2Id = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup2 + ".Item.ID").toUpperCase();
                    if(!winGroup2Id.startsWith("HEAD") && !winGroup2Id.startsWith("BASE64")) {
                        material2 = Material.getMaterial(winGroup2Id);
                        if (material2 == null) {
                            material2 = Material.STONE;
                        }
                        winItem2 = Main.t.createItem(material2, 1, 0, winGroupDisplayName2);
                    }
                    if(winGroup2Id.startsWith("HEAD")) {
                        String[] parts = winGroup2Id.split(":");
                        winItem2 = Main.t.getPlayerHead(parts[1], winGroupDisplayName2);
                    }
                    if(winGroup2Id.startsWith("HDB")) {
                        String[] parts = winGroup2Id.split(":");
                        String id = parts[1];
                        if(Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                            winItem2  = Main.t.getHDBSkull(id, winGroupDisplayName2);
                        } else {
                            winItem2 = new ItemStack(Material.STONE);
                        }
                    }
                    as.setHelmet(winItem2);
                    as.setCustomName(winItem2.getItemMeta().getDisplayName());
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 5.0F);
                }
                // End
                if (this.i >= 70) {
                    as.remove();
                    this.cancel();
                    Main.ActiveCase.remove(lAC);
                    Main.listAR.remove(as);
                    StartAnimation.caseOpen.remove(player);
                }

                ++this.i;
            }
        }).runTaskTimer(Main.instance, 0L, 2L);
    }
}
