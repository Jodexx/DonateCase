package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.data.JavaAnimation;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class ShapeAnimation extends JavaAnimation {
    private EquipmentSlot itemSlot;
    private ArmorStandEulerAngle armorStandEulerAngle;

    @Override
    public void start() {
        getLocation().add(0.5, -0.1, 0.5);
        getLocation().setYaw(-70.0F);
        final ArmorStandCreator as = Tools.createArmorStand(getLocation());
        armorStandEulerAngle = Tools.getArmorStandEulerAngle("Shape.Pose");
        itemSlot = EquipmentSlot.valueOf(Case.getConfig().getAnimations().getString("Shape.ItemSlot", "HEAD").toUpperCase());
        boolean small = Case.getConfig().getAnimations().getBoolean("Shape.SmallArmorStand", true);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        as.spawn();

        float whiteSize = (float) Case.getConfig().getAnimations().getDouble("Shape.Particle.White.Size");
        float orangeSize = (float) Case.getConfig().getAnimations().getDouble("Shape.Particle.Orange.Size");
        final String orangeRgbString = Case.getConfig().getAnimations().getString("Shape.Particle.Orange.Rgb");
        final String whiteRgbString = Case.getConfig().getAnimations().getString("Shape.Particle.White.Rgb");

        final Color orangeColor = Tools.fromRGBString(orangeRgbString, Color.ORANGE);
        final Color whiteColor = Tools.fromRGBString(whiteRgbString, Color.WHITE);

        (new BukkitRunnable() {
            int i; //ticks count
            double t;
            Location l;


            @Override
            public void run() {
                if (i == 0) {
                    l = as.getLocation();
                }
                if (i >= 7) {
                    if (i == 16) {
                        if (getWinItem().getMaterial().getItemStack().getType() != Material.AIR) {
                            as.setEquipment(itemSlot, getWinItem().getMaterial().getItemStack());
                        }
                        as.setAngle(armorStandEulerAngle);
                        as.setCustomName(getWinItem().getMaterial().getDisplayName());
                        as.updateMeta();
                        Tools.launchFirework(this.l.clone().add(0.0, 0.8, 0.0));
                        Case.animationPreEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());

                    }
                }

                if (i <= 15) {
                    CaseData.Item winItem = getCaseData().getRandomItem();
                    if (winItem.getMaterial().getItemStack().getType() != Material.AIR) {
                        as.setAngle(armorStandEulerAngle);
                        as.setEquipment(itemSlot, winItem.getMaterial().getItemStack());
                    }
                    String winGroupDisplayName = Case.getInstance().papi.setPlaceholders(getPlayer(), winItem.getMaterial().getDisplayName());
                    winItem.getMaterial().setDisplayName(winGroupDisplayName);
                    as.setCustomName(Tools.rc(winGroupDisplayName));
                    as.setCustomNameVisible(true);
                    as.updateMeta();
                    if (this.i <= 8) {
                        Particle.DustOptions dustOptions = new Particle.DustOptions(orangeColor, orangeSize);
                        Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.REDSTONE, this.l.clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0, dustOptions);
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
                        if (!Bukkit.getVersion().contains("1.12")) {
                            Particle.DustOptions dustOptions = new Particle.DustOptions(whiteColor, whiteSize);
                            Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.REDSTONE, this.l.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0, dustOptions);
                        } else {
                            Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.FIREWORKS_SPARK, this.l.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0);
                        }
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
                    Case.animationEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());
                }

                ++this.i;
            }
        }).runTaskTimer(Case.getInstance(), 0L, 2L);
    }

}