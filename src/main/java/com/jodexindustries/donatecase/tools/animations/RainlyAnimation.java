package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.PAPISupport;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

import static com.jodexindustries.donatecase.dc.Main.customConfig;
import static com.jodexindustries.donatecase.dc.Main.t;

public class RainlyAnimation implements Animation {
    @Override
    public String getName() {
        return "DEFAULT RAINLY";
    }
    @Override
    public void start(Player player, Location location, String c) {
        final Location lAC = location.clone();
        final String FallingParticle = customConfig.getAnimations().getString("Rainly.FallingParticle");
        final String winGroup = Tools.getRandomGroup(c);
        String winGroupId = Case.getWinGroupId(c, winGroup);
        String winGroupDisplayName = t.rc(Case.getWinGroupDisplayName(c, winGroup));
        if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            winGroupId = PAPISupport.setPlaceholders(player, winGroupId);
            winGroupDisplayName = PAPISupport.setPlaceholders(player, winGroupDisplayName);
        }
        final boolean winGroupEnchant = Case.getWinGroupEnchant(c, winGroup);
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
        Case.listAR.add(as);
        as.setGravity(false);
        as.setSmall(true);
        as.setCustomNameVisible(true);
        String finalWinGroupId = winGroupId;
        String finalWinGroupDisplayName = winGroupDisplayName;
        (new BukkitRunnable() {
            int i; // count of ticks
            double t;
            Location l;

            public void run() {
                Material material;
                ItemStack winItem;
                Objects.requireNonNull(lAC.getWorld()).spawnParticle(Particle.valueOf(FallingParticle), rain1, 1);
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
                if(!finalWinGroupId.contains(":")) {
                    material = Material.getMaterial(finalWinGroupId);
                    if (material == null) {
                        material = Material.STONE;
                    }
                    if(material != Material.AIR) {
                        winItem = Main.t.createItem(material, 1, -1, finalWinGroupDisplayName, winGroupEnchant);
                    } else {
                        winItem = new ItemStack(Material.AIR);
                    }
                } else
                if(finalWinGroupId.startsWith("HEAD")) {
                    String[] parts = finalWinGroupId.split(":");
                    winItem = Main.t.getPlayerHead(parts[1], finalWinGroupDisplayName);
                } else {
                    if (finalWinGroupId.startsWith("HDB")) {
                        String[] parts = finalWinGroupId.split(":");
                        String id = parts[1];
                        if (Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                            winItem = Main.t.getHDBSkull(id, finalWinGroupDisplayName);
                        } else {
                            winItem = new ItemStack(Material.STONE);
                        }
                    } else if (finalWinGroupId.startsWith("CH")) {
                        String[] parts = finalWinGroupId.split(":");
                        String category = parts[1];
                        String id = parts[2];
                        if (Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                            winItem = Main.t.getCHSkull(category, id, finalWinGroupDisplayName);
                        } else {
                            winItem = new ItemStack(Material.STONE);
                        }
                    } else if (finalWinGroupId.startsWith("BASE64")) {
                        String[] parts = finalWinGroupId.split(":");
                        String base64 = parts[1];
                        winItem = Main.t.getBASE64Skull(base64, finalWinGroupDisplayName);
                    } else {
                        String[] parts = finalWinGroupId.split(":");
                        byte data = -1;
                        if(parts[1] != null) {
                            data = Byte.parseByte(parts[1]);
                        }
                        material = Material.getMaterial(parts[0]);
                        if (material == null) {
                            material = Material.STONE;
                        }
                        winItem = Main.t.createItem(material, data, 1, finalWinGroupDisplayName, winGroupEnchant);
                    }
                }
                if (this.i == 0) {
                    this.l = as.getLocation();
                }
                if (this.i >= 14) {
                    this.l.setYaw(las.getYaw());
                    if (this.i == 32) {
                        // win item and title
                        if(winItem.getType() != Material.AIR) {
                            as.setHelmet(winItem);
                        }
                        as.setCustomName(finalWinGroupDisplayName);
                        Case.onCaseOpenFinish(c, player, false, winGroup);
                        lAC.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, lAC, 0);
                        lAC.getWorld().playSound(lAC, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    }
                }

                // change random item
                if (this.i <= 30 && (this.i % 2 == 0 )) {
                    final String winGroup2 = Case.getRandomGroup(c);
                    ItemStack winItem2;
                    Material material2;
                    String winGroupDisplayName2 = Main.t.rc(Case.getWinGroupDisplayName(c, winGroup2));
                    String winGroupId2 = Case.getWinGroupId(c, winGroup2);
                    if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                        winGroupId2 = PAPISupport.setPlaceholders(player, winGroupId2);
                        winGroupDisplayName2 = PAPISupport.setPlaceholders(player, winGroupDisplayName2);
                    }
                    boolean winGroupEnchant2 = Case.getWinGroupEnchant(c, winGroup2);
                    if(!winGroupId2.contains(":")) {
                        material2 = Material.getMaterial(winGroupId2);
                        if (material2 == null) {
                            material2 = Material.STONE;
                        }
                        if(material2 != Material.AIR) {
                            winItem2 = Main.t.createItem(material2, 1, -1, winGroupDisplayName2, winGroupEnchant2);
                        } else {
                            winItem2 = new ItemStack(Material.AIR);
                        }
                    } else {
                        if (winGroupId2.startsWith("HEAD")) {
                            String[] parts = winGroupId2.split(":");
                            winItem2 = Main.t.getPlayerHead(parts[1], winGroupDisplayName2);
                        } else if (winGroupId2.startsWith("HDB")) {
                            String[] parts = winGroupId2.split(":");
                            String id = parts[1];
                            if (Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                                winItem2 = Main.t.getHDBSkull(id, winGroupDisplayName2);
                            } else {
                                winItem2 = new ItemStack(Material.STONE);
                            }
                        } else if (winGroupId2.startsWith("CH")) {
                            String[] parts = winGroupId2.split(":");
                            String category = parts[1];
                            String id = parts[2];
                            if (Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                                winItem2 = Main.t.getCHSkull(category, id, winGroupDisplayName2);
                            } else {
                                winItem2 = new ItemStack(Material.STONE);
                            }
                        } else if (winGroupId2.startsWith("BASE64")) {
                            String[] parts = winGroupId2.split(":");
                            String base64 = parts[1];
                            winItem2 = Main.t.getBASE64Skull(base64, winGroupDisplayName2);
                        } else {
                            String[] parts = winGroupId2.split(":");
                            byte data = -1;
                            if(parts[1] != null) {
                                data = Byte.parseByte(parts[1]);
                            }
                            material = Material.getMaterial(parts[0]);
                            if (material == null) {
                                material = Material.STONE;
                            }
                            winItem2 = Main.t.createItem(material, data, 1, winGroupDisplayName2, winGroupEnchant2);
                        }
                    }
                    if(winItem2.getType() != Material.AIR) {
                        as.setHelmet(winItem2);
                    }
                    as.setCustomName(winGroupDisplayName2);
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 5.0F);
                    // firework particles
                    this.t += 0.25;
                    Location loc = this.l.clone();
                    loc = loc.add(0.0, 0.6, 0.0);

                    for(double phi = 0.0; phi <= 9; ++phi) {
                        double x = 0.09 * (9 - this.t * 2.5) * Math.cos(this.t + phi);
                        double z = 0.09 * (9 - this.t * 2.5) * Math.sin(this.t + phi);
                        loc.add(x, 0.0, z);
                        Objects.requireNonNull(this.l.getWorld()).spawnParticle(Particle.FIREWORKS_SPARK, this.l.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0);
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
                    Case.animationEnd(c, getName(), player, lAC);
                    Case.listAR.remove(as);
                }

                ++this.i;
            }
        }).runTaskTimer(Main.instance, 0L, 2L);
    }
}
