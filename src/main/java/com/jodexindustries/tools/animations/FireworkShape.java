package com.jodexindustries.tools.animations;

import com.jodexindustries.dc.Main;
import com.jodexindustries.tools.CustomConfig;
import com.jodexindustries.tools.StartAnimation;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FireworkShape {
    public static List<Player> caseOpen = new ArrayList<>();
    public FireworkShape(final Player player, Location location, final String c) {
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
        as.setCustomNameVisible(false);
        (new BukkitRunnable() {
            int i; //ticks count
            Location l;

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
                if (this.i == 1) {
                    Location loc = this.l.clone().add(0.0, 1.0, 0);
                    Firework firework = loc.getWorld().spawn(loc, Firework.class);
                    FireworkMeta data = firework.getFireworkMeta();
                    data.addEffects(FireworkEffect.builder().withColor(Color.PURPLE).withColor(Color.RED).with(FireworkEffect.Type.BALL).withFlicker().build());
                    for (String color : CustomConfig.getAnimations().getStringList("Firework.FireworkColors")) {
                        data.addEffect(FireworkEffect.builder().withColor(Main.t.parseColor(color)).build());
                    }
                    data.setPower(CustomConfig.getAnimations().getInt("FireWork.Power"));
                    firework.setFireworkMeta(data);
                }
                Location las = as.getLocation().clone();
                las.setYaw(las.getYaw() + 20.0F);
                as.teleport(las);
                this.l = this.l.add(0.0, 0.14, 0.0);
                if (this.i <= 7) {
                    this.l.setYaw(las.getYaw());
                    as.teleport(this.l);
                }

                if (this.i >= 7) {
                    if (this.i == 10) {
                        as.setCustomNameVisible(true);
                        as.setHelmet(winItem);
                        as.setCustomName(winItem.getItemMeta().getDisplayName());
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
                        if(CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".AnimationSound") != null) {
                            sound = Objects.requireNonNull(CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".AnimationSound"));
                            Sound sound1;
                            sound1 = Sound.valueOf(sound.toUpperCase());
                            if (sound1 == null) {
                                sound1 = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
                            }
                            player.playSound(player.getLocation(), sound1, 1.0F, 5.0F);
                        }
                        // Broadcast
                        for (String cmd2 : CustomConfig.getConfig().getStringList("DonatCase.Cases." + c + ".Items." + winGroup + ".Broadcast")) {
                            Bukkit.broadcastMessage(Main.t.rc(Main.t.rt(cmd2, "%player:" + player.getName(), "%group:" + winGroupDisplayName, "%case:" + casetitle)));
                        }
                    }
                    // end
                    if (this.i >= 30) {
                        as.remove();
                        this.cancel();
                        Main.ActiveCase.remove(lAC);
                        Main.listAR.remove(as);
                        StartAnimation.caseOpen.remove(player);
                    }
                }



                ++this.i;
            }
        }).runTaskTimer(Main.instance, 0L, 2L);
    }
}
