package net.jodexindustries.tools.animations;

import net.jodexindustries.dc.DonateCase;
import net.jodexindustries.tools.CustomConfig;
import net.jodexindustries.tools.StartAnimation;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class Rainly {
    public static List<Player> caseOpen = new ArrayList<>();
    public Rainly(final Player player, Location location, final String c) {
        final Location lAC = location.clone();
        final String casetitle = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Title");
        DonateCase.ActiveCase.put(lAC, c);
        caseOpen.add(player);

        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (DonateCase.openCase.containsKey(pl) && DonateCase.t.isHere(location, DonateCase.openCase.get(pl))) {
                pl.closeInventory();
            }
        }
        final String winGroup = DonateCase.t.getRandomGroup(c);
        final String winGroupId = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Item.ID").toUpperCase();
        final String winGroupDisplayName = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Item.DisplayName");
        final String winGroupGroup = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Group");
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
        DonateCase.listAR.add(as);
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
                String sound;
                lAC.getWorld().spawnParticle(Particle.FALLING_WATER, rain1, 1);
                lAC.getWorld().spawnParticle(Particle.FALLING_WATER, rain2, 1);
                lAC.getWorld().spawnParticle(Particle.FALLING_WATER, rain3, 1);
                lAC.getWorld().spawnParticle(Particle.FALLING_WATER, rain4, 1);
                lAC.getWorld().spawnParticle(Particle.CLOUD, cloud1, 0);
                lAC.getWorld().spawnParticle(Particle.CLOUD, cloud2, 0);
                lAC.getWorld().spawnParticle(Particle.CLOUD, cloud3, 0);
                lAC.getWorld().spawnParticle(Particle.CLOUD, cloud4, 0);
                Location las = as.getLocation().clone();
                las.setYaw(las.getYaw() + 20.0F);
                as.teleport(las);
                if(!winGroupId.startsWith("HEAD")) {
                    material = Material.getMaterial(winGroupId);
                    if (material == null) {
                        material = Material.STONE;
                    }
                    winItem = DonateCase.t.createItem(material, 1, 0, winGroupDisplayName);
                }
                if(winGroupId.startsWith("HEAD")) {
                    String[] parts = winGroupId.split(":");
                    winItem = DonateCase.t.getPlayerHead(parts[1], winGroupDisplayName);
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
                        String titleWin = DonateCase.lang.getString(ChatColor.translateAlternateColorCodes('&', "TitleWin"));
                        String subTitleWin = DonateCase.lang.getString(ChatColor.translateAlternateColorCodes('&', "SubTitleWin"));
                        String reptitleWin = DonateCase.t.rt(titleWin, "%groupdisplayname:" + winGroupDisplayName, "%group:" + winGroup);
                        String repsubTitleWin = DonateCase.t.rt(subTitleWin, "%groupdisplayname:" + winGroupDisplayName, "%group:" + winGroup);
                        player.sendTitle(DonateCase.t.rc(reptitleWin), DonateCase.t.rc(repsubTitleWin), 5, 60, 5);
                        // givecommand
                        String playergroup = DonateCase.getPermissions().getPrimaryGroup(player).toLowerCase();
                        String givecommand = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".GiveCommand");
                        if (CustomConfig.getConfig().getBoolean("DonatCase.LevelGroup")) {
                            if (CustomConfig.getConfig().getConfigurationSection("DonatCase.LevelGroups").contains(playergroup) &&
                                    CustomConfig.getConfig().getInt("DonatCase.LevelGroups." + playergroup) >=
                                            CustomConfig.getConfig().getInt("DonatCase.LevelGroups." + winGroupGroup)) {
                            } else {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), DonateCase.t.rt(givecommand, "%player:" + player.getName(), "%group:" + winGroupGroup));
                            }
                        } else {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), DonateCase.t.rt(givecommand, "%player:" + player.getName(), "%group:" + winGroupGroup));
                        }
                        // customcommands
                        for (String cmd : CustomConfig.getConfig().getStringList("DonatCase.Cases." + c + ".Items." + winGroup + ".Commands")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), DonateCase.t.rt(cmd, "%player:" + player.getName(), "%group:" + winGroupGroup));
                        }
                        // sound
//                        if (CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".AnimationSound") != null) {
//                            sound = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".AnimationSound");
//                            Sound sound1;
//                            sound1 = Sound.valueOf(sound.toUpperCase());
//                            if (sound == null) {
//                                sound1 = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
//                            }
//                            player.playSound(player.getLocation(), sound1, 1.0F, 5.0F);
//                        }
                        lAC.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, lAC, 0);
                        lAC.getWorld().playSound(lAC, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                        for (Player pl : Bukkit.getOnlinePlayers()) {
                            for (String msg : CustomConfig.getConfig().getStringList("DonatCase.Cases." + c + ".Items." + winGroup + ".Broadcast")) {
                                DonateCase.t.msg_(pl, DonateCase.t.rt(msg, "%player:" + player.getName(), "%group:" + winGroupDisplayName, "%case:" + casetitle));
                            }
                        }
                    }
                }

                // change random item
                if (this.i <= 30 && (this.i % 2 == 0 )) {
                    final String winGroup2 = DonateCase.t.getRandomGroup(c);
                    ItemStack winItem2 = null;
                    Material material2;
                    final String winGroupDisplayName2 = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup2 + ".Item.DisplayName");
                    final String winGroup2Id = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup2 + ".Item.ID").toUpperCase();
                    if(!winGroup2Id.startsWith("HEAD")) {
                        material2 = Material.getMaterial(winGroup2Id);
                        if (material2 == null) {
                            material2 = Material.STONE;
                        }
                        winItem2 = DonateCase.t.createItem(material2, 1, 0, winGroupDisplayName2);
                    }
                    if(winGroup2Id.startsWith("HEAD")) {
                        String[] parts = winGroup2Id.split(":");
                        winItem2 = DonateCase.t.getPlayerHead(parts[1], winGroupDisplayName2);
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
                    DonateCase.ActiveCase.remove(lAC);
                    DonateCase.listAR.remove(as);
                    StartAnimation.caseOpen.remove(player);
                }

                ++this.i;
            }
        }).runTaskTimer(DonateCase.instance, 0L, 2L);
    }
}
