package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.JavaAnimation;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
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
    private WheelType wheelType;

    private enum WheelType {
        FULL,  // No duplicates, all unique items
        RANDOM; // Can have duplicates, random items

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
        wheelType = WheelType.getType(getSettings().getString("Type", "RANDOM"));
        armorStandEulerAngle = Tools.getArmorStandEulerAngle(getSettings().getConfigurationSection("Pose"));
        itemSlot = EquipmentSlot.valueOf(getSettings().getString("ItemSlot", "HEAD").toUpperCase());
        Bukkit.getScheduler().runTaskTimer(Case.getInstance(), new Task(), 0L, 0L);
    }

    private class Task implements Consumer<BukkitTask> {

        private final Location loc = getLocation().clone().add(0.5, 0, 0.5);
        private final World world;

        private final int itemsCount = getSettings().getInt("ItemsCount");
        private final int animationTime = getSettings().getInt("Scroll.Time", 100);
        private final Location flocation = loc.clone().add(0 + getSettings().getDouble("LiftingAlongX"),
                -1 + getSettings().getDouble("LiftingAlongY"),
                0 + getSettings().getDouble("LiftingAlongZ"));
        private final boolean needSound = getSettings().getString("Scroll.Sound") != null;
        private final Sound sound = Sound.valueOf(getSettings().getString("Scroll.Sound"));
        private final float volume = (float) getSettings().getDouble("Scroll.Volume");
        private final float vpitch = (float) getSettings().getDouble("Scroll.Pitch");
        private final double speed = getSettings().getDouble("CircleSpeed");
        private final double radius = getSettings().getDouble("CircleRadius");
        private final boolean useFlame = getSettings().getBoolean("Flame.Enabled");
        private final Particle flameParticle = Particle.valueOf(getSettings().getString("Flame.Particle", "FLAME"));

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
            boolean small = getSettings().getBoolean("SmallArmorStand", true);
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
