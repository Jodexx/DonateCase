package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

import static com.jodexindustries.donatecase.dc.Main.t;

public class ShapeAnimation implements Animation {

    @Override
    public String getName() {
        return "DEFAULT SHAPE";
    }

    @Override
    public void start(Player player, Location location, String c) {
        final Location loc = location.clone();
        final String winGroup = Tools.getRandomGroup(c);
        String winGroupDisplayName = t.rc(Case.getWinGroupDisplayName(c, winGroup));
        if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            winGroupDisplayName = PAPISupport.setPlaceholders(player, winGroupDisplayName);
        }
        location.add(0.5, -0.1, 0.5);
        location.setYaw(-70.0F);
        final ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        Case.listAR.add(as);
        as.setGravity(false);
        as.setSmall(true);
        as.setVisible(false);
        as.setCustomNameVisible(true);
        String finalWinGroupDisplayName = winGroupDisplayName;
        (new BukkitRunnable() {
            int i; //ticks count
            double t;
            Location l;

            public void run() {
                ItemStack winItem = Main.t.getWinItem(c, winGroup, player);
                if (i == 0) {
                    l = as.getLocation();
                }
                if (i >= 7) {
                    if (i == 16) {
                        if(winItem.getType() != Material.AIR) {
                            as.setHelmet(winItem);
                        }
                        as.setCustomName(finalWinGroupDisplayName);
                        Main.t.launchFirework(this.l.clone().add(0.0, 0.8, 0.0));
                        Case.onCaseOpenFinish(c, player, true, winGroup);

                    }
                }

                if (i <= 15) {
                    final String winGroup2 = Tools.getRandomGroup(c);
                    ItemStack winItem2 = Main.t.getWinItem(c, winGroup2, player);
                    if(winItem2.getType() != Material.AIR) {
                        as.setHelmet(winItem2);
                    }
                    String winGroupDisplayName = Case.getWinGroupDisplayName(c, winGroup2);
                    as.setCustomName(Main.t.rc(winGroupDisplayName));
                    if (this.i <= 8) {
                        if (!Bukkit.getVersion().contains("1.12")) {
                            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.ORANGE, 1.0F);
                            Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.REDSTONE, this.l.clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0, dustOptions);
                        } else {
                            Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.REDSTONE, this.l.clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0);
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
                    Case.animationEnd(c, getName(), player, loc, winGroup);
                    Case.listAR.remove(as);
                }

                ++this.i;
            }
        }).runTaskTimer(Main.instance, 0L, 2L);
    }
}
