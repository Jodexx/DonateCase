package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.JavaAnimation;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;
import java.util.function.Consumer;

public class ShapeAnimation extends JavaAnimation {

    @Override
    public void start() {
        getLocation().add(0.5, -0.1, 0.5);
        getLocation().setYaw(-70.0F);
        final ArmorStandCreator as = Tools.createArmorStand(getLocation());
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

        Bukkit.getScheduler().runTaskTimer(Case.getInstance(),
                new Task(as, whiteSize, orangeSize, orangeColor, whiteColor),
                0L, 2L);
    }

    private class Task implements Consumer<BukkitTask> {

        private int tick;
        private double tail;
        private Location l;
        
        private final EquipmentSlot itemSlot;
        private final ArmorStandEulerAngle armorStandEulerAngle;

        private final ArmorStandCreator as;
        private final float whiteSize;
        private final float orangeSize;
        private final Color orangeColor;
        private final Color whiteColor;
        
        public Task(final ArmorStandCreator as, final float whiteSize, final float orangeSize, final Color orangeColor, final Color whiteColor) {
            this.as = as;
            this.whiteSize = whiteSize;
            this.orangeSize = orangeSize;
            this.orangeColor = orangeColor;
            this.whiteColor = whiteColor;
            this.itemSlot = EquipmentSlot.valueOf(Case.getConfig().getAnimations().getString("Shape.ItemSlot", "HEAD")
                    .toUpperCase());
            this.armorStandEulerAngle = Tools.getArmorStandEulerAngle("Shape.Pose");

        }

        @Override
        public void accept(BukkitTask task) {
            if (tick == 0) {
                l = as.getLocation();
            }
            if (tick >= 7) {
                if (tick == 16) {
                    if (getWinItem().getMaterial().getItemStack().getType() != Material.AIR) {
                        as.setEquipment(itemSlot, getWinItem().getMaterial().getItemStack());
                    }
                    as.setAngle(armorStandEulerAngle);
                    as.setCustomName(getWinItem().getMaterial().getDisplayName());
                    as.updateMeta();
                    Tools.launchFirework(l.clone().add(0.0, 0.8, 0.0));
                    Case.animationPreEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());

                }
            }

            if (tick <= 15) {
                CaseData.Item item = getCaseData().getRandomItem();
                if (item.getMaterial().getItemStack().getType() != Material.AIR) {
                    as.setAngle(armorStandEulerAngle);
                    as.setEquipment(itemSlot, item.getMaterial().getItemStack());
                }
                String winGroupDisplayName = Tools.rc(Case.getInstance().papi.setPlaceholders(getPlayer(),
                        item.getMaterial().getDisplayName()));
                if(item.getMaterial().getDisplayName() != null && !item.getMaterial().getDisplayName().isEmpty())
                    as.setCustomNameVisible(true);
                as.setCustomName(winGroupDisplayName);
                as.updateMeta();
                if (tick <= 8) {
                    Particle.DustOptions dustOptions = new Particle.DustOptions(orangeColor, orangeSize);
                    Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.REDSTONE, l.clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0, dustOptions);
                }
            }

            Location las = as.getLocation().clone();
            las.setYaw(las.getYaw() + 20.0F);
            as.teleport(las);
            l = l.add(0.0, 0.13, 0.0);
            if (tick <= 7) {
                l.setYaw(las.getYaw());
                as.teleport(l);
            }
            //white trail
            if (tick <= 15) {
                tail += 0.25;
                Location loc = l.clone();
                loc = loc.add(0.0, 0.5, 0.0);
                for (double phi = 0.0; phi <= 9.4; phi += 1) {
                    final double x = 0.09 * (9.5 - tail * 2.5) * Math.cos(tail + phi);
                    final double z = 0.09 * (9.5 - tail * 2.5) * Math.sin(tail + phi);
                    loc.add(x, 0.0, z);
                    if (!Bukkit.getVersion().contains("1.12")) {
                        Particle.DustOptions dustOptions = new Particle.DustOptions(whiteColor, whiteSize);
                        Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.REDSTONE, l.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0, dustOptions);
                    } else {
                        Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.FIREWORKS_SPARK, l.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0);
                    }
                    loc.subtract(x, 0.0, z);
                    if (tail >= 22) {
                        loc.add(x, 0.0, z);
                        tail = 0.0;
                    }
                }
            }
            //end
            if (tick >= 40) {
                as.remove();
                task.cancel();
                Case.animationEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());
            }

            ++tick;
        }
    }
    }