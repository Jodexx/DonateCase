package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.StartAnimation;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static com.jodexindustries.donatecase.dc.Main.customConfig;

public class Rainly {
    public Rainly(final Player player, Location location, final String c) {
        final Location lAC = location.clone();
        Main.ActiveCase.put(lAC, c);

        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (Main.openCase.containsKey(pl) && Main.t.isHere(location, Main.openCase.get(pl))) {
                pl.closeInventory();
            }
        }
        final String FallingParticle = customConfig.getAnimations().getString("Rainly.FallingParticle");
        final String winGroup = Main.t.getRandomGroup(c);
        final String winGroupId = customConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Item.ID").toUpperCase();
        final String winGroupDisplayName = customConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Item.DisplayName");
        location.add(0.5, 1, 0.5);
        Location rain1 = lAC.clone().add(-1.5, 3, -1.5);
        Location rain2 = lAC.clone().add(2.5, 3, -1.5);
        Location rain3 = lAC.clone().add(2.5, 3, 2.5);
        Location rain4 = lAC.clone().add(-1.5, 3, 2.5);
        Location cloud1 = rain1.clone().add(0, 0.5, 0);
        Location cloud2 = rain2.clone().add(0, 0.5, 0);
        Location cloud3 = rain3.clone().add(0, 0.5, 0);
        Location cloud4 = rain4.clone().add(0, 0.5, 0);
        location.setYaw(-70.0F);
        final ArmorStand as = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        as.setVisible(false);
        Main.listAR.add(as);
        as.setGravity(false);
        as.setSmall(true);
        as.setCustomNameVisible(true);
        (new BukkitRunnable() {
            int i; // count of ticks
            double t;
            Location l;

            public void run() {
                Material material;
                ItemStack winItem = null;
                lAC.getWorld().spawnParticle(Particle.valueOf(FallingParticle), rain1, 1);
                lAC.getWorld().spawnParticle(Particle.valueOf(FallingParticle), rain2, 1);
                lAC.getWorld().spawnParticle(Particle.valueOf(FallingParticle), rain3, 1);
                lAC.getWorld().spawnParticle(Particle.valueOf(FallingParticle), rain4, 1);
                lAC.getWorld().spawnParticle(Particle.CLOUD, cloud1, 0);
                lAC.getWorld().spawnParticle(Particle.CLOUD, cloud2, 0);
                lAC.getWorld().spawnParticle(Particle.CLOUD, cloud3, 0);
                lAC.getWorld().spawnParticle(Particle.CLOUD, cloud4, 0);
                Location las = as.getLocation().clone();
                las.setYaw(las.getYaw() + 20.0F);
                as.teleport(las);
                if(!winGroupId.contains(":")) {
                    material = Material.getMaterial(winGroupId);
                    if (material == null) {
                        material = Material.STONE;
                    }
                    winItem = Main.t.createItem(material, 1, 0, winGroupDisplayName);
                } else
                if(winGroupId.startsWith("HEAD")) {
                    String[] parts = winGroupId.split(":");
                    winItem = Main.t.getPlayerHead(parts[1], winGroupDisplayName);
                } else {
                    if (winGroupId.startsWith("HDB")) {
                        String[] parts = winGroupId.split(":");
                        String id = parts[1];
                        if (Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                            winItem = Main.t.getHDBSkull(id, winGroupDisplayName);
                        } else {
                            winItem = new ItemStack(Material.STONE);
                        }
                    } else if (winGroupId.startsWith("CH")) {
                        String[] parts = winGroupId.split(":");
                        String category = parts[1];
                        String id = parts[2];
                        if (Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                            winItem = Main.t.getCHSkull(category, id, winGroupDisplayName);
                        } else {
                            winItem = new ItemStack(Material.STONE);
                        }
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
                    final String winGroupDisplayName2 = customConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup2 + ".Item.DisplayName");
                    final String winGroup2Id = customConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup2 + ".Item.ID").toUpperCase();
                    if(!winGroup2Id.contains(":")) {
                        material2 = Material.getMaterial(winGroup2Id);
                        if (material2 == null) {
                            material2 = Material.STONE;
                        }
                        winItem2 = Main.t.createItem(material2, 1, 0, winGroupDisplayName2);
                    } else {
                        if (winGroup2Id.startsWith("HEAD")) {
                            String[] parts = winGroup2Id.split(":");
                            winItem2 = Main.t.getPlayerHead(parts[1], winGroupDisplayName2);
                        } else if (winGroup2Id.startsWith("HDB")) {
                            String[] parts = winGroup2Id.split(":");
                            String id = parts[1];
                            if (Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                                winItem2 = Main.t.getHDBSkull(id, winGroupDisplayName2);
                            } else {
                                winItem2 = new ItemStack(Material.STONE);
                            }
                        } else if (winGroupId.startsWith("CH")) {
                            String[] parts = winGroupId.split(":");
                            String category = parts[1];
                            String id = parts[2];
                            if (Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                                winItem2 = Main.t.getCHSkull(category, id, winGroupDisplayName);
                            } else {
                                winItem2 = new ItemStack(Material.STONE);
                            }
                        }
                    }
                    as.setHelmet(winItem2);
                    as.setCustomName(winItem2.getItemMeta().getDisplayName());
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 5.0F);
                    // firework particles
                    this.t += 0.25;
                    Location loc = this.l.clone();
                    loc = loc.add(0.0, 0.6, 0.0);

                    for(double phi = 0.0; phi <= 9; ++phi) {
                        double x = 0.09 * (9 - this.t * 2.5) * Math.cos(this.t + phi);
                        double z = 0.09 * (9 - this.t * 2.5) * Math.sin(this.t + phi);
                        loc.add(x, 0.0, z);
                        this.l.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, this.l.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0);
                        loc.subtract(x, 0.0, z);
                        if (this.t >= 22) {
                            loc.add(x, 0.0, z);
                            this.t = 0.0;
                        }
                    }
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
