package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.data.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

import static com.jodexindustries.donatecase.DonateCase.customConfig;
import static com.jodexindustries.donatecase.DonateCase.t;

public class RainlyAnimation implements Animation {
    @Override
    public String getName() {
        return "DEFAULT RAINLY";
    }
    @Override
    public void start(Player player, Location location, CaseData c, CaseData.Item winItem) {
        final Location loc = location.clone();
        final String FallingParticle = customConfig.getAnimations().getString("Rainly.FallingParticle");
        String winGroupDisplayName = PAPISupport.setPlaceholders(player,winItem.getMaterial().getDisplayName());
        winItem.getMaterial().setDisplayName(winGroupDisplayName);
        location.add(0.5, 1, 0.5);
        Location rain1 = loc.clone().add(-1.5, 3, -1.5);
        Location rain2 = loc.clone().add(2.5, 3, -1.5);
        Location rain3 = loc.clone().add(2.5, 3, 2.5);
        Location rain4 = loc.clone().add(-1.5, 3, 2.5);
        Location cloud1 = rain1.clone().add(0, 0.5, 0);
        Location cloud2 = rain2.clone().add(0, 0.5, 0);
        Location cloud3 = rain3.clone().add(0, 0.5, 0);
        Location cloud4 = rain4.clone().add(0, 0.5, 0);
        location.setYaw(-70.0F);
        ArmorStandCreator as = t.createArmorStand();
        as.spawnArmorStand(location);
        as.setVisible(false);
        as.setGravity(false);
        boolean small = customConfig.getAnimations().getBoolean("Rainly.SmallArmorStand", true);

        as.setSmall(small);
        (new BukkitRunnable() {
            int i; // count of ticks
            double t;
            Location l;

            public void run() {
                Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.valueOf(FallingParticle), rain1, 1);
                loc.getWorld().spawnParticle(Particle.valueOf(FallingParticle), rain2, 1);
                loc.getWorld().spawnParticle(Particle.valueOf(FallingParticle), rain3, 1);
                loc.getWorld().spawnParticle(Particle.valueOf(FallingParticle), rain4, 1);
                loc.getWorld().spawnParticle(Particle.CLOUD, cloud1, 0);
                loc.getWorld().spawnParticle(Particle.CLOUD, cloud2, 0);
                loc.getWorld().spawnParticle(Particle.CLOUD, cloud3, 0);
                loc.getWorld().spawnParticle(Particle.CLOUD, cloud4, 0);
                Location las = as.getLocation().clone();
                las.setYaw(las.getYaw() + 20.0F);
                as.teleport(las);
                if (this.i == 0) {
                    this.l = as.getLocation();
                }
                if (this.i >= 14) {
                    this.l.setYaw(las.getYaw());
                    if (this.i == 32) {
                        // win item and title
                        if(winItem.getMaterial().getItemStack().getType() != Material.AIR) {
                            as.setHelmet(winItem.getMaterial().getItemStack());
                        }
                        as.setCustomName(winGroupDisplayName);
                        Case.onCaseOpenFinish(c, player, false, winItem);
                        loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 0);
                        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    }
                }

                // change random item
                if (this.i <= 30 && (this.i % 2 == 0 )) {
                    CaseData.Item winItem = Case.getRandomItem(c);
                    String winGroupDisplayName = PAPISupport.setPlaceholders(player,winItem.getMaterial().getDisplayName());
                    winItem.getMaterial().setDisplayName(winGroupDisplayName);
                    if(winItem.getMaterial().getItemStack() != null && winItem.getMaterial().getItemStack().getType() != Material.AIR) {
                        as.setHelmet(winItem.getMaterial().getItemStack());
                    }
                    as.setCustomName(winItem.getMaterial().getDisplayName());
                    loc.getWorld().playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 5.0F);
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
                    Case.animationEnd(c, getName(), player, loc, winItem);
                }

                ++this.i;
            }
        }).runTaskTimer(DonateCase.instance, 0L, 2L);
    }
}