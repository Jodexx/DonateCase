package com.jodexindustries.tools.animations;

import com.jodexindustries.dc.Main;
import com.jodexindustries.tools.CustomConfig;
import com.jodexindustries.tools.StartAnimation;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Shape {
    public static List<Player> caseOpen = new ArrayList<>();
    public Shape(final Player player, Location location, final String c) {
        final Location lAC = location.clone();
        final String casetitle = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Title");
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
        final String winGroupGroup = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Group");
        location.add(0.5, -0.1, 0.5);
        location.setYaw(-70.0F);
        final ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        Main.listAR.add(as);
        as.setGravity(false);
        as.setSmall(true);
        as.setVisible(false);
        as.setCustomNameVisible(true);
        final double[] y = {0};
        (new BukkitRunnable() {
            int i; //ticks count
            double t;
            Location l;
            Location trail;

            public void run() {
                Material material;
                ItemStack winItem = null;
                String sound;
                if(!winGroupId.startsWith("HEAD")) {
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
                if (this.i == 0) {
                    this.l = as.getLocation();
                }

                if (this.i >= 7) {
                    if (this.i == 16) {
                        as.setHelmet(winItem);
                        as.setCustomName(winItem.getItemMeta().getDisplayName());
                        Main.t.launchFirework(this.l.clone().add(0.0, 0.8, 0.0));
                        String titleWin = Main.lang.getString(ChatColor.translateAlternateColorCodes('&', "TitleWin"));
                        String subTitleWin = Main.lang.getString(ChatColor.translateAlternateColorCodes('&', "SubTitleWin"));
                        String reptitleWin = Main.t.rt(titleWin, "%groupdisplayname:" + winGroupDisplayName, "%group:" + winGroup);
                        String repsubTitleWin = Main.t.rt(subTitleWin, "%groupdisplayname:" + winGroupDisplayName, "%group:" + winGroup);
                        player.sendTitle(Main.t.rc(reptitleWin), Main.t.rc(repsubTitleWin), 5, 60, 5);
                        // givecommand
                        String playergroup = Main.getPermissions().getPrimaryGroup(player).toLowerCase();
                        String givecommand = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".GiveCommand");
                        if (CustomConfig.getConfig().getBoolean("DonatCase.LevelGroup")) {
                            if (CustomConfig.getConfig().getConfigurationSection("DonatCase.LevelGroups").contains(playergroup) &&
                                    CustomConfig.getConfig().getInt("DonatCase.LevelGroups." + playergroup) >=
                                            CustomConfig.getConfig().getInt("DonatCase.LevelGroups." + winGroupGroup)) {
                            } else {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.t.rt(givecommand, "%player:" + player.getName(), "%group:" + winGroupGroup));
                            }
                        } else {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.t.rt(givecommand, "%player:" + player.getName(), "%group:" + winGroupGroup));
                        }
                        // customcommands
                        for (String cmd : CustomConfig.getConfig().getStringList("DonatCase.Cases." + c + ".Items." + winGroup + ".Commands")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.t.rt(cmd, "%player:" + player.getName(), "%group:" + winGroupGroup));
                        }
                        // Sound
                        if(CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".AnimationSound") != null) {
                            sound = Objects.requireNonNull(CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".AnimationSound"));
                            Sound sound1;
                            sound1 = Sound.valueOf(sound.toUpperCase());
                            if (sound1 == null) {
                                sound1 = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
                            }
                            player.playSound(player.getLocation(), sound1, 1.0F, 5.0F);
                        } // Broadcast
                        for (Player pl : Bukkit.getOnlinePlayers()) {
                            for (String msg : CustomConfig.getConfig().getStringList("DonatCase.Cases." + c + ".Items." + winGroup + ".Broadcast")) {
                                Main.t.msg_(pl, Main.t.rt(msg, "%player:" + player.getName(), "%group:" + winGroupDisplayName, "%case:" + casetitle));
                            }
                        }
                    }
                }

                if (this.i <= 15) {
                    final String winGroup2 = Main.t.getRandomGroup(c);
                    ItemStack winItem2 = null;
                    Material material2;
                    final String winGroupDisplayName2 = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup2 + ".Item.DisplayName");
                    final String winGroup2Id = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup2 + ".Item.ID").toUpperCase();
                    if(!winGroup2Id.startsWith("HEAD")) {
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
                    as.setHelmet(winItem2);
                    as.setCustomName(winItem2.getItemMeta().getDisplayName());
                    if (this.i <= 8) {
                        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.ORANGE, 1.0F);
                        this.l.getWorld().spawnParticle(Particle.REDSTONE, this.l.clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0, dustOptions);
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
                        this.l.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, this.l.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0);
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
                    Main.ActiveCase.remove(lAC);
                    Main.listAR.remove(as);
                    StartAnimation.caseOpen.remove(player);
                }

                ++this.i;
            }
        }).runTaskTimer(Main.instance, 0L, 2L);
    }
}
