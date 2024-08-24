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
import org.bukkit.World;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

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

        final String orangeRgbString = Case.getConfig().getAnimations().getString("Shape.Particle.Orange.Rgb");
        final String whiteRgbString = Case.getConfig().getAnimations().getString("Shape.Particle.White.Rgb");

        final Color orangeColor = Tools.fromRGBString(orangeRgbString, Color.ORANGE);
        final Color whiteColor = Tools.fromRGBString(whiteRgbString, Color.WHITE);

        Bukkit.getScheduler().runTaskTimer(Case.getInstance(),
                new Task(as, orangeColor, whiteColor),
                0L, 2L);
    }

    private class Task implements Consumer<BukkitTask> {

        private int tick;
        private double tail;
        private final Location l;

        @NotNull
        private final World world;
        private final EquipmentSlot itemSlot;
        private final ArmorStandEulerAngle armorStandEulerAngle;

        private final ArmorStandCreator as;
        private final float whiteSize;
        private final float orangeSize;
        private final Color orangeColor;
        private final Color whiteColor;

        public Task(final ArmorStandCreator as, final Color orangeColor, final Color whiteColor) {
            this.as = as;
            this.l = as.getLocation();
            this.whiteSize = (float) Case.getConfig().getAnimations().getDouble("Shape.Particle.White.Size");
            this.orangeSize = (float) Case.getConfig().getAnimations().getDouble("Shape.Particle.Orange.Size");
            this.orangeColor = orangeColor;
            this.whiteColor = whiteColor;
            this.itemSlot = EquipmentSlot.valueOf(Case.getConfig().getAnimations().getString("Shape.ItemSlot", "HEAD")
                    .toUpperCase());
            this.armorStandEulerAngle = Tools.getArmorStandEulerAngle("Shape.Pose");
            world = l.getWorld() != null ? l.getWorld() : getPlayer().getWorld();
        }

        @Override
        public void accept(BukkitTask task) {
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

            if (tick <= 15) {
                CaseData.Item item = getCaseData().getRandomItem();
                if (item.getMaterial().getItemStack().getType() != Material.AIR) {
                    as.setAngle(armorStandEulerAngle);
                    as.setEquipment(itemSlot, item.getMaterial().getItemStack());
                }

                String winGroupDisplayName = Tools.rc(Case.getInstance().papi.setPlaceholders(getPlayer(),
                        item.getMaterial().getDisplayName()));
                if (item.getMaterial().getDisplayName() != null && !item.getMaterial().getDisplayName().isEmpty()) {
                    as.setCustomNameVisible(true);
                }
                as.setCustomName(winGroupDisplayName);
                as.updateMeta();

                if (tick <= 8) {
                    Particle.DustOptions dustOptions = new Particle.DustOptions(orangeColor, orangeSize);
                    world.spawnParticle(Particle.REDSTONE, l.clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0, dustOptions);
                }
            }

            Location las = as.getLocation();
            las.setYaw(las.getYaw() + 20.0F);
            as.teleport(las);

            l.add(0.0, 0.13, 0.0);
            if (tick <= 7) {
                l.setYaw(las.getYaw());
                as.teleport(l);
            }

            if (tick <= 15) {
                tail += 0.25;
                Location loc = l.clone().add(0.0, 0.5, 0.0);

                for (double phi = 0.0; phi <= 9.4; phi += 1) {
                    double x = 0.09 * (9.5 - tail * 2.5) * Math.cos(tail + phi);
                    double z = 0.09 * (9.5 - tail * 2.5) * Math.sin(tail + phi);
                    loc.add(x, 0.0, z);
                    Particle.DustOptions dustOptions = new Particle.DustOptions(whiteColor, whiteSize);

                    world.spawnParticle(
                            Bukkit.getVersion().contains("1.12") ? Particle.FIREWORKS_SPARK : Particle.REDSTONE,
                            loc.clone().add(0.0, 0.4, 0.0),
                            1, 0.1, 0.1, 0.1, 0.0, dustOptions
                    );
                    loc.subtract(x, 0.0, z);
                }

                if (tail >= 22) {
                    tail = 0.0;
                }
            }

            if (tick >= 40) {
                as.remove();
                task.cancel();
                Case.animationEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());
            }

            ++tick;
        }

    }
}