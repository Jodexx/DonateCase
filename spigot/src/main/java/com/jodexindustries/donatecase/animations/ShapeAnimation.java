package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.manager.AnimationManager;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.jodexindustries.donatecase.DonateCase.instance;

public class ShapeAnimation extends JavaAnimationBukkit {

    public static void register(AnimationManager<JavaAnimationBukkit, CaseDataMaterialBukkit, ItemStack, Player, Location, Block, CaseDataBukkit> manager) {
        CaseAnimation<JavaAnimationBukkit, CaseDataMaterialBukkit, ItemStack> caseAnimation = manager.builder("SHAPE")
                .animation(ShapeAnimation.class)
                .description("Items flip through and a shape appears")
                .requireSettings(true)
                .build();

        manager.registerAnimation(caseAnimation);
    }

    @Override
    public void start() {
        double x = getSettings().getDouble("StartPosition.X", 0.5);
        double y = getSettings().getDouble("StartPosition.Y", -0.1);
        double z = getSettings().getDouble("StartPosition.Z", 0.5);

        getLocation().add(x, y, z);
        getLocation().setYaw(-70.0F);

        final ArmorStandCreator as = instance.api.getTools().createArmorStand(getLocation());
        boolean small = getSettings().getBoolean("Shape.SmallArmorStand", true);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        as.spawn();

        final String orangeRgbString = getSettings().getString("Shape.Particle.Orange.Rgb");
        final String whiteRgbString = getSettings().getString("Shape.Particle.White.Rgb");

        final Color orangeColor = DCToolsBukkit.fromRGBString(orangeRgbString, Color.ORANGE);
        final Color whiteColor = DCToolsBukkit.fromRGBString(whiteRgbString, Color.WHITE);

        Bukkit.getScheduler().runTaskTimer(Case.getInstance(),
                new Task(as, orangeColor, whiteColor),
                0L, 2L);
    }

    private class Task implements Consumer<BukkitTask> {

        private int tick;


        @NotNull
        private final World world;
        private final EquipmentSlot itemSlot;
        private final ArmorStandEulerAngle armorStandEulerAngle;

        private final ArmorStandCreator as;
        private final Location location;

        private final float whiteSize;
        private final float orangeSize;
        private final Color orangeColor;
        private final Color whiteColor;

        private final double tailRadius;
        private double currentTail;

        private final int scrollTime;
        private final int scrollInterval;
        private final int endTime;
        private final double blockPerTick;

        private final Particle particle = getParticle();

        public Task(final ArmorStandCreator as, final Color orangeColor, final Color whiteColor) {
            this.as = as;
            this.location = as.getLocation();
            this.whiteSize = (float) getSettings().getDouble("Particle.White.Size");
            this.orangeSize = (float) getSettings().getDouble("Particle.Orange.Size");
            this.orangeColor = orangeColor;
            this.whiteColor = whiteColor;

            double height = getSettings().getDouble("Scroll.Height", 1.5);

            this.tailRadius = getSettings().getDouble("Tail.Radius", 0.5);
            this.currentTail = tailRadius;

            this.scrollTime = getSettings().getInt("Scroll.Time", 40);
            this.scrollInterval = getSettings().getInt("Scroll.Interval", 1);
            this.endTime = getSettings().getInt("End.Time", 40);
            this.blockPerTick = height / scrollTime;

            this.itemSlot = EquipmentSlot.valueOf(getSettings().getString("ItemSlot", "HEAD")
                    .toUpperCase());
            this.armorStandEulerAngle = DCToolsBukkit.getArmorStandEulerAngle(getSettings().getConfigurationSection("Pose"));
            world = as.getLocation().getWorld() != null ? as.getLocation().getWorld() : getPlayer().getWorld();
        }

        @Override
        public void accept(BukkitTask task) {

            location.setYaw(location.getYaw() + 20.0F);

            if (tick <= scrollTime) {
                location.add(0.0, blockPerTick, 0.0);

                Particle.DustOptions dustOptions = new Particle.DustOptions(orangeColor, orangeSize);
                world.spawnParticle(particle, as.getLocation().clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0, dustOptions);
            }

            as.teleport(location);

            if (tick <= scrollTime) {
                handleTail();

                if (tick % scrollInterval == 0) {
                    CaseDataItem<CaseDataMaterialBukkit, ItemStack> item = getCaseData().getRandomItem();
                    if (item.getMaterial().getItemStack().getType() != Material.AIR) {
                        as.setAngle(armorStandEulerAngle);
                        as.setEquipment(itemSlot, item.getMaterial().getItemStack());
                    }

                    String winGroupDisplayName = DCToolsBukkit.rc(Case.getInstance().papi.setPlaceholders(getPlayer(),
                            item.getMaterial().getDisplayName()));
                    if (item.getMaterial().getDisplayName() != null && !item.getMaterial().getDisplayName().isEmpty()) {
                        as.setCustomNameVisible(true);
                    }
                    as.setCustomName(winGroupDisplayName);
                    as.updateMeta();
                }

            }

            if (tick == scrollTime + 1) {
                if (getWinItem().getMaterial().getItemStack().getType() != Material.AIR) {
                    as.setEquipment(itemSlot, getWinItem().getMaterial().getItemStack());
                }
                as.setAngle(armorStandEulerAngle);
                as.setCustomName(getWinItem().getMaterial().getDisplayName());
                as.updateMeta();
                instance.api.getTools().launchFirework(as.getLocation().add(0, 0.5, 0));
                instance.api.getAnimationManager().animationPreEnd(getCaseDataBukkit(), getPlayer(), getUuid(), getWinItem());
            }

            if (tick >= scrollTime + endTime) {
                as.remove();
                task.cancel();
                instance.api.getAnimationManager().animationEnd(getCaseDataBukkit(), getPlayer(), getUuid(), getWinItem());
            }

            ++tick;
        }

        private void handleTail() {
            currentTail -= tailRadius / scrollTime;
            Location loc = as.getLocation().clone().add(0.0, blockPerTick, 0.0);

            double c = 2 * Math.PI * 0.5;
            double angle = c / 10;

            for (double t = 0.0; t <= c; t += angle) {
                double x = currentTail * Math.cos(t);
                double z = currentTail * Math.sin(t);
                loc.add(x, 0.4, z);
                Particle.DustOptions dustOptions = new Particle.DustOptions(whiteColor, whiteSize);

                world.spawnParticle(
                        particle,
                        loc,
                        1, 0.1, 0.1, 0.1, 0.0, dustOptions
                );
                loc.subtract(x, 0.4, z);
            }

        }

        private Particle getParticle() {
            try {
                return Particle.valueOf("REDSTONE");
            } catch (IllegalArgumentException e) {
                return Particle.valueOf("DUST");
            }
        }

    }
}