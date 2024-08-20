package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.JavaAnimation;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class RainlyAnimation extends JavaAnimation {
    private EquipmentSlot itemSlot;
    private ArmorStandEulerAngle armorStandEulerAngle;

    @Override
    public void start() {
        final Location loc = getLocation().clone();
        final String FallingParticle = Case.getConfig().getAnimations().getString("Rainly.FallingParticle");
        String winGroupDisplayName = Case.getInstance().papi.setPlaceholders(getPlayer(), getWinItem().getMaterial().getDisplayName());
        getWinItem().getMaterial().setDisplayName(winGroupDisplayName);
        getLocation().add(0.5, 1, 0.5);
        Location rain1 = loc.clone().add(-1.5, 3, -1.5);
        Location rain2 = loc.clone().add(2.5, 3, -1.5);
        Location rain3 = loc.clone().add(2.5, 3, 2.5);
        Location rain4 = loc.clone().add(-1.5, 3, 2.5);
        Location cloud1 = rain1.clone().add(0, 0.5, 0);
        Location cloud2 = rain2.clone().add(0, 0.5, 0);
        Location cloud3 = rain3.clone().add(0, 0.5, 0);
        Location cloud4 = rain4.clone().add(0, 0.5, 0);
        getLocation().setYaw(-70.0F);
        ArmorStandCreator as = Tools.createArmorStand(getLocation());
        as.setVisible(false);
        as.setGravity(false);
        armorStandEulerAngle = Tools.getArmorStandEulerAngle("Rainly.Pose");

        itemSlot = EquipmentSlot.valueOf(Case.getConfig().getAnimations().getString("Rainly.ItemSlot", "HEAD").toUpperCase());

        boolean small = Case.getConfig().getAnimations().getBoolean("Rainly.SmallArmorStand", true);

        as.setSmall(small);
        as.spawn();
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
                        if (getWinItem().getMaterial().getItemStack().getType() != Material.AIR) {
                            as.setEquipment(itemSlot, getWinItem().getMaterial().getItemStack());
                        }
                        as.setAngle(armorStandEulerAngle);
                        as.setCustomNameVisible(true);
                        as.setCustomName(winGroupDisplayName);
                        as.updateMeta();
                        Case.animationPreEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());
                        loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 0);
                        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    }
                }

                // change random item
                if (this.i <= 30 && (this.i % 2 == 0)) {
                    CaseData.Item item = getCaseData().getRandomItem();
                    String winGroupDisplayName = Case.getInstance().papi.setPlaceholders(getPlayer(), item.getMaterial().getDisplayName());
                    item.getMaterial().setDisplayName(winGroupDisplayName);
                    if (item.getMaterial().getItemStack().getType() != Material.AIR) {
                        as.setEquipment(itemSlot, item.getMaterial().getItemStack());
                    }
                    as.setAngle(armorStandEulerAngle);
                    if(item.getMaterial().getDisplayName() != null && !item.getMaterial().getDisplayName().isEmpty())
                        as.setCustomNameVisible(true);
                    as.setCustomName(item.getMaterial().getDisplayName());
                    as.updateMeta();
                    loc.getWorld().playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 5.0F);
                    // firework particles
                    this.t += 0.25;
                    Location loc = this.l.clone();
                    loc = loc.add(0.0, 0.6, 0.0);

                    for (double phi = 0.0; phi <= 9; ++phi) {
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
                    Case.animationEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());
                }

                ++this.i;
            }
        }).runTaskTimer(Case.getInstance(), 0L, 2L);
    }

}