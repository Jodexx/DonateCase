package net.jodexindustries.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.jodexindustries.dc.DonateCase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class StartAnimation {
    public static List<Player> caseOpen = new ArrayList();

    public StartAnimation(final Player player, Location location, final String c) {
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
        location.add(0.5, -0.1, 0.5);
        location.setYaw(-70.0F);
        final ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        DonateCase.listAR.add(as);
        as.setGravity(false);
        as.setSmall(true);
        as.setVisible(false);
        as.setCustomNameVisible(true);
        (new BukkitRunnable() {
            int i;
            double t;
            Location l;

            public void run() {
                Material material;
                material = Material.getMaterial(winGroupId);
                if (material == null) {
                    material = Material.STONE;
                }

                ItemStack winItem = DonateCase.t.createItem(material, 1, 0, winGroupDisplayName);
                if (this.i == 0) {
                    this.l = as.getLocation();
                }

                if (this.i >= 7) {
                    if (this.i == 16) {
                        as.setHelmet(winItem);
                        as.setCustomName(winItem.getItemMeta().getDisplayName());
                        DonateCase.t.launchFirework(this.l.clone().add(0.0, 0.8, 0.0));
                        String message = DonateCase.lang.getString(ChatColor.translateAlternateColorCodes('&', "TitleWin"));
                        String message2 = DonateCase.lang.getString(ChatColor.translateAlternateColorCodes('&', "SubTitleWin"));
                        player.sendTitle(DonateCase.t.rc(message), DonateCase.t.rc(message2 + winGroupDisplayName), 5, 60, 5);
                        Iterator var6 = CustomConfig.getConfig().getStringList("DonatCase.Cases." + c + ".Items." + winGroup + ".Commands").iterator();

                        while(var6.hasNext()) {
                            String cmd = (String)var6.next();
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), DonateCase.t.rt(cmd, "%player:" + player.getName(), "%group:" + winGroupGroup));
                        }

                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADE, 1.0F, 5.0F);
                        var6 = Bukkit.getOnlinePlayers().iterator();

                        while(var6.hasNext()) {
                            Player pl = (Player)var6.next();

                            for (String cmd2 : CustomConfig.getConfig().getStringList("DonatCase.Cases." + c + ".Items." + winGroup + ".Broadcast")) {
                                DonateCase.t.msg_(pl, DonateCase.t.rt(cmd2, "%player:" + player.getName(), "%group:" + winGroupDisplayName, "%case:" + casetitle));
                            }
                        }
                    }

                    if (this.i >= 40) {
                        as.remove();
                        this.cancel();
                        DonateCase.ActiveCase.remove(lAC);
                        DonateCase.listAR.remove(as);
                        StartAnimation.caseOpen.remove(player);
                    }
                }

                if (this.i <= 15) {
                    final String winGroup2 = DonateCase.t.getRandomGroup(c);
                    final String winGroupDisplayName2 = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup2 + ".Item.DisplayName");
                    ItemStack winItem2 = DonateCase.t.createItem(material, 1, 0, winGroupDisplayName2);
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
                this.l = this.l.add(0.0, 0.14, 0.0);
                if (this.i <= 7) {
                    this.l.setYaw(las.getYaw());
                    as.teleport(this.l);
                }

                if (this.i <= 15) {
                    this.t += 0.241660973353061;
                    Location loc = this.l.clone();
                    loc = loc.add(0.0, 0.6000000000000001, 0.0);

                    for(double phi = 0.0; phi <= 9.42477796076938; ++phi) {
                        double x = 0.09 * (9.42477796076938 - this.t * 2.5) * Math.cos(this.t + phi);
                        double z = 0.09 * (9.42477796076938 - this.t * 2.5) * Math.sin(this.t + phi);
                        loc.add(x, 0.0, z);
                        this.l.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, this.l.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0);
                        loc.subtract(x, 0.0, z);
                        if (this.t >= 21.991148575128552) {
                            loc.add(x, 0.0, z);
                            this.t = 0.0;
                        }
                    }
                }

                ++this.i;
            }
        }).runTaskTimer(DonateCase.instance, 0L, 2L);
    }
}
