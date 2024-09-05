package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.JavaAnimation;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WheelAnimation extends JavaAnimation {

    private final List<ArmorStandCreator> armorStands = new ArrayList<>();
    private EquipmentSlot itemSlot;
    private ArmorStandEulerAngle armorStandEulerAngle;

    private final WheelType wheelType = WheelType.getType(Case.getConfig().getAnimations().getString("Wheel.Type",
            "RANDOM"));

    private enum WheelType {
        FULL,  // No duplicates, all unique items
        RANDOM;       // Can have duplicates, random items

        @NotNull
        public static WheelType getType(String string) {
            try {
                return valueOf(string);
            } catch (IllegalArgumentException e) {
                return RANDOM;
            }
        }
    }

    @Override
    public void start() {
        armorStandEulerAngle = Tools.getArmorStandEulerAngle("Wheel.Pose");
        itemSlot = EquipmentSlot.valueOf(Case.getConfig().getAnimations().getString("Wheel.ItemSlot", "HEAD").toUpperCase());
        Bukkit.getScheduler().runTaskTimer(Case.getInstance(), new Task(), 0L, 0L);
    }

    private class Task implements Consumer<BukkitTask> {

        private final Location loc = getLocation().clone().add(0.5, 0, 0.5);
        private final World world;

        private final int itemsCount = Case.getConfig().getAnimations().getInt("Wheel.ItemsCount");
        private final int animationTime = Case.getConfig().getAnimations().getInt("Wheel.Scroll.Time", 100);
        private final Location flocation = loc.clone().add(0 + Case.getConfig().getAnimations().getDouble("Wheel.LiftingAlongX"),
                -1 + Case.getConfig().getAnimations().getDouble("Wheel.LiftingAlongY"),
                0 + Case.getConfig().getAnimations().getDouble("Wheel.LiftingAlongZ"));
        private final boolean needSound = Case.getConfig().getAnimations().getString("Wheel.Scroll.Sound") != null;
        private final Sound sound = Sound.valueOf(Case.getConfig().getAnimations().getString("Wheel.Scroll.Sound"));
        private final float volume = (float) Case.getConfig().getAnimations().getDouble("Wheel.Scroll.Volume");
        private final float vpitch = (float) Case.getConfig().getAnimations().getDouble("Wheel.Scroll.Pitch");
        private final double speed = Case.getConfig().getAnimations().getDouble("Wheel.CircleSpeed");
        private final double radius = Case.getConfig().getAnimations().getDouble("Wheel.CircleRadius");
        private final boolean useFlame = Case.getConfig().getAnimations().getBoolean("Wheel.Flame.Enabled");
        private final Particle flameParticle = Particle.valueOf(Case.getConfig().getAnimations().getString("Wheel.Flame.Particle", "FLAME"));

        private final double baseAngle = loc.clone().getDirection().angle(new Vector(0, 0, 1));
        private double lastCompletedRotation = 0.0;
        private int ticks;
        private double yAx = 0;
        private double radiusAx = radius;
        private double speedAx = speed;
        private final double rotationThreshold;
        private final double offset;

        public Task() {
            float pitch = Math.round(getLocation().getPitch() / 90.0f) * 90.0f;
            float yaw = Math.round(getLocation().getYaw() / 90.0f) * 90.0f;
            loc.setPitch(pitch);
            loc.setYaw(yaw);

            initializeItems();

            rotationThreshold = Math.PI / (armorStands.size() * speed);
            offset = 2 * Math.PI / armorStands.size();

            world = loc.getWorld() != null ? loc.getWorld() : getPlayer().getWorld();
        }

        @Override
        public void accept(BukkitTask task) {
            ticks++;
            double angle = ticks / (20.0 * (animationTime / 100D)) * speedAx * 2 * Math.PI;

            if (ticks < animationTime + 1) {
                handleFlameEffects();
                moveArmorStands(angle);
            }

            if (ticks == animationTime + 1) {
                Case.animationPreEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());
            }

            if (ticks >= animationTime + 20) {
                endAnimation(task);
            }

            if (ticks < animationTime + 1) {
                speedAx *= 1 - (speed / (animationTime - 2));
            }
        }

        private void initializeItems() {
            boolean small = Case.getConfig().getAnimations().getBoolean("Wheel.SmallArmorStand", true);
            armorStands.add(spawnArmorStand(getLocation(), getWinItem(), small));

            if (wheelType == WheelType.FULL) {
                // FULL logic - unique items

                for (CaseData.Item uniqueItem : getCaseData().getItems().values()) {
                    if(uniqueItem.getItemName().equals(getWinItem().getItemName())) continue;

                    armorStands.add(spawnArmorStand(getLocation(), uniqueItem, small));
                }

            } else {
                // RANDOM logic - random items with duplicates
                for (int i = 1; i < itemsCount; i++) {
                    CaseData.Item randomItem = getCaseData().getRandomItem();
                    armorStands.add(spawnArmorStand(getLocation(), randomItem, small));
                }
            }
        }

        private void handleFlameEffects() {
            if (useFlame) {
                yAx += (radius + 4.0) / animationTime * speedAx;
                radiusAx -= 0.015 / (animationTime / 100.0);
                double theta = ticks / (20.0 / (speedAx * 6));
                spawnFlameEffect(theta);
                spawnFlameEffect(theta + Math.PI); // For the opposite side
            }
        }

        private void spawnFlameEffect(double theta) {
            double dx = (radiusAx / 1.1) * Math.sin(theta);
            double dy = (radiusAx / 1.1) * Math.cos(theta);
            Location particleLocation = flocation.clone().add(dx, yAx, dy);
            world.spawnParticle(flameParticle, particleLocation, 1, 0, 0, 0, 0, null);
        }

        private void moveArmorStands(double angle) {
            for (ArmorStandCreator entity : armorStands) {
                double x = radius * Math.sin(angle);
                double y = radius * Math.cos(angle);

                Vector rotationAxis = loc.getDirection().crossProduct(new Vector(0, 1, 0)).normalize();
                Location newLoc = flocation.clone().add(rotationAxis.multiply(x).add(loc.getDirection().multiply(y)));
                entity.teleport(newLoc);
                angle += offset;

                double currentAngle = angle - baseAngle;
                if (currentAngle - lastCompletedRotation >= rotationThreshold && needSound) {
                    world.playSound(flocation, sound, volume, vpitch);
                    lastCompletedRotation = currentAngle;
                }
            }
        }

        private void endAnimation(BukkitTask task) {
            task.cancel();
            for (ArmorStandCreator stand : armorStands) {
                stand.remove();
            }
            Case.animationEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());
            armorStands.clear();
        }
    }

    private ArmorStandCreator spawnArmorStand(Location location, CaseData.Item item, boolean small) {
        ArmorStandCreator as = Tools.createArmorStand(location);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        as.setAngle(armorStandEulerAngle);
        as.setCustomName(item.getMaterial().getDisplayName());
        as.setCustomNameVisible(item.getMaterial().getDisplayName() != null && !item.getMaterial().getDisplayName().isEmpty());
        as.spawn();
        if (item.getMaterial().getItemStack().getType() != Material.AIR) {
            as.setEquipment(itemSlot, item.getMaterial().getItemStack());
        }
        return as;
    }
}
