package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class Shape implements Animation {

    @Override
    public String getName() {
        return "DEFAULT SHAPE";
    }

    @Override
    public void start(Player player, Location location, String c) {
        final Location lAC = location.clone();

        final String winGroup = Tools.getRandomGroup(c);
        final String winGroupId = Case.getWinGroupId(c, winGroup);
        final String winGroupDisplayName = Case.getWinGroupDisplayName(c, winGroup);
        boolean winGroupEnchant = Case.getWinGroupEnchant(c, winGroup);
        location.add(0.5, -0.1, 0.5);
        location.setYaw(-70.0F);
        final ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        Case.listAR.add(as);
        as.setGravity(false);
        as.setSmall(true);
        as.setVisible(false);
        as.setCustomNameVisible(true);
        (new BukkitRunnable() {
            int i; //ticks count
            double t;
            Location l;

            public void run() {
                Material material;
                ItemStack winItem = null;
                if(!winGroupId.contains(":")) {
                    material = Material.getMaterial(winGroupId);
                    if (material == null) {
                        material = Material.STONE;
                    }
                    winItem = Main.t.createItem(material, 1, -1, winGroupDisplayName, winGroupEnchant);
                } else {
                    if (winGroupId.startsWith("HEAD")) {
                        String[] parts = winGroupId.split(":");
                        winItem = Main.t.getPlayerHead(parts[1], winGroupDisplayName);
                    } else if (winGroupId.startsWith("HDB")) {
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
                    } else if (winGroupId.startsWith("BASE64")) {
                        String[] parts = winGroupId.split(":");
                        String base64 = parts[1];
                        winItem = Main.t.getBASE64Skull(base64, winGroupDisplayName);
                    } else {
                        String[] parts = winGroupId.split(":");
                        byte data = -1;
                        if(parts[1] != null) {
                            data = Byte.parseByte(parts[1]);
                        }
                        material = Material.getMaterial(parts[0]);
                        if (material == null) {
                            material = Material.STONE;
                        }
                        winItem = Main.t.createItem(material, data, 1, winGroupDisplayName, winGroupEnchant);
                    }
                }
                if (this.i == 0) {
                    this.l = as.getLocation();
                }

                if (this.i >= 7) {
                    if (this.i == 16) {
                        as.setHelmet(winItem);
                        as.setCustomName(winItem != null ? Objects.requireNonNull(winItem.getItemMeta()).getDisplayName() : null);
                        Main.t.launchFirework(this.l.clone().add(0.0, 0.8, 0.0));
                        Case.onCaseOpenFinish(c, player, true, winGroup);

                    }
                }

                if (this.i <= 15) {
                    final String winGroup2 = Tools.getRandomGroup(c);
                    ItemStack winItem2 = null;
                    Material material2;
                    final String winGroupDisplayName2 = Case.getWinGroupDisplayName(c, winGroup2);
                    final String winGroup2Id = Case.getWinGroupId(c, winGroup2);
                    final boolean winGroupEnchant2 = Case.getWinGroupEnchant(c, winGroup2);
                    if(!winGroup2Id.contains(":")) {
                        material2 = Material.getMaterial(winGroup2Id);
                        if (material2 == null) {
                            material2 = Material.STONE;
                        }
                        winItem2 = Main.t.createItem(material2, 1, -1, winGroupDisplayName2, winGroupEnchant2);
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
                        } else if (winGroup2Id.startsWith("CH")) {
                            String[] parts = winGroup2Id.split(":");
                            String category = parts[1];
                            String id = parts[2];
                            if (Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                                winItem2 = Main.t.getCHSkull(category, id, winGroupDisplayName2);
                            } else {
                                winItem2 = new ItemStack(Material.STONE);
                            }
                        } else if (winGroup2Id.startsWith("BASE64")) {
                            String[] parts = winGroup2Id.split(":");
                            String base64 = parts[1];
                            winItem2 = Main.t.getBASE64Skull(base64, winGroupDisplayName2);
                        } else {
                            String[] parts = winGroup2Id.split(":");
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
                    as.setHelmet(winItem2);
                    as.setCustomName(Objects.requireNonNull(winItem2 != null ? winItem2.getItemMeta() : null).getDisplayName());
                    if (this.i <= 8) {
                        if (!Bukkit.getVersion().contains("1.12")) {
                            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.ORANGE, 1.0F);
                            Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.REDSTONE, this.l.clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0, dustOptions);
                        } else {
                            l.getWorld().spawnParticle(Particle.REDSTONE, this.l.clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0);
                        }
                    }
                }

                Location las = as.getLocation().clone();
                las.setYaw(las.getYaw() + 20.0F);
                as.teleport(las);
                this.l = this.l.add(0.0, 0.13, 0.0);
                if (this.i <= 7) {
                    this.l.setYaw(las.getYaw());
                    as.teleport(this.l);
                }
                //white trail
                if (this.i <= 15) {
                    this.t += 0.25;
                    Location loc = this.l.clone();
                    loc = loc.add(0.0, 0.5, 0.0);
                    for (double phi = 0.0; phi <= 9.4; phi += 1) {
                        final double x = 0.09 * (9.5 - this.t * 2.5) * Math.cos(this.t + phi);
                        final double z = 0.09 * (9.5 - this.t * 2.5) * Math.sin(this.t + phi);
                        loc.add(x, 0.0, z);
                        Objects.requireNonNull(this.l.getWorld()).spawnParticle(Particle.FIREWORKS_SPARK, this.l.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0);
                        loc.subtract(x, 0.0, z);
                        if (this.t >= 22) {
                            loc.add(x, 0.0, z);
                            this.t = 0.0;
                        }
                    }
                }
                //end
                if (this.i >= 40) {
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
