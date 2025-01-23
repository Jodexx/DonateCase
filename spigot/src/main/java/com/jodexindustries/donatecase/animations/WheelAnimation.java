package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.BukkitBackend;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.tools.BukkitUtils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.util.function.Consumer;

public class WheelAnimation extends BukkitJavaAnimation {

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

    @SneakyThrows
    @Override
    public void start() {
        wheelType = WheelType.getType(getSettings().node("Type").getString("RANDOM"));
        armorStandEulerAngle = getSettings().node("Pose").get(ArmorStandEulerAngle.class);
        itemSlot = EquipmentSlot.valueOf(getSettings().node("ItemSlot").getString("HEAD").toUpperCase());
        Bukkit.getScheduler().runTaskTimer(((BukkitBackend) DCAPI.getInstance().getPlatform()).getPlugin(), new Task(), 0L, 0L);
    }

    private class Task implements Consumer<BukkitTask> {

        private final Location loc = BukkitUtils.toBukkit(getLocation().clone()).add(0.5, 0, 0.5);
        private final World world;

        private final int itemsCount = getSettings().node("ItemsCount").getInt();
        private final int animationTime = getSettings().node("Scroll.Time").getInt(100);
        private final int scrollCount = getSettings().node("Scroll.Count").getInt(1);
        private final double easeAmount = getSettings().node("Scroll.EaseAmount").getDouble(2.5);
        private final Location flocation = loc.clone().add(0 + getSettings().node("LiftingAlongX").getDouble(),
                -1 + getSettings().node("LiftingAlongY").getDouble(),
                0 + getSettings().node("LiftingAlongZ").getDouble());
        private final boolean needSound = getSettings().node("Scroll.Sound") != null;
        private final Sound sound = Sound.valueOf(getSettings().node("Scroll.Sound").getString());
        private final float volume = getSettings().node("Scroll.Volume").getFloat();
        private final float vpitch = getSettings().node("Scroll.Pitch").getFloat();
        private final double radius = getSettings().node("CircleRadius").getDouble();
        private final boolean useFlame = getSettings().node("Flame.Enabled").getBoolean();
        private final Particle flameParticle = Particle.valueOf(getSettings().node("Flame.Particle").getString("FLAME"));

        private final double baseAngle = loc.clone().getDirection().angle(new Vector(0, 0, 1));
        private double lastCompletedRotation = 0.0;
        private int ticks;
        private double targetAngle;
        private final double rotationThreshold;
        private final double offset;

        public Task() {
            float pitch = Math.round(getLocation().getPitch() / 90.0f) * 90.0f;
            float yaw = Math.round(getLocation().getYaw() / 90.0f) * 90.0f;
            loc.setPitch(pitch);
            loc.setYaw(yaw);

            initializeItems();

            rotationThreshold = Math.PI / armorStands.size();
            offset = 2 * rotationThreshold;

            world = getPlayer().getWorld();
        }

        @Override
        public void accept(BukkitTask task) {
            ticks++;

            double progress = Math.min(ticks / (double) animationTime, 1.0); // Progress from 0 to 1
            double easedProgress = 1 - Math.pow(1 - progress, easeAmount); // ease-out
            double currentAngle = easedProgress * targetAngle;

            if (ticks <= animationTime) {
                handleFlameEffects();
                moveArmorStands(currentAngle);
            }

            if (ticks == animationTime) {
                preEnd();
            }

            if (ticks >= animationTime + 20) {
                endAnimation(task);
            }
        }

        private void initializeItems() {
            boolean small = getSettings().node("SmallArmorStand").getBoolean(true);

            if (wheelType == WheelType.FULL) {
                // FULL logic - unique items
                List<CaseDataItem> uniqueItems = new ArrayList<>(getCaseData().getItems().values());

                if (getSettings().node("Shuffle").getBoolean(true)) {
                    Collections.shuffle(uniqueItems);
                }

                int additionalSteps = 0;
                for (CaseDataItem uniqueItem : uniqueItems) {
                    if (uniqueItem.getItemName().equals(getWinItem().getItemName())) {
                        additionalSteps = uniqueItems.size() - armorStands.size();
                        armorStands.add(spawnArmorStand(getLocation(), getWinItem(), small));
                    }
                    else armorStands.add(spawnArmorStand(getLocation(), uniqueItem, small));
                }

                double additionalAngle = additionalSteps * (2 * Math.PI / armorStands.size());
                targetAngle = 2 * Math.PI * scrollCount + additionalAngle;
            }
            else {
                // RANDOM logic - random items with duplicates
                armorStands.add(spawnArmorStand(getLocation(), getWinItem(), small));
                for (int i = 1; i < itemsCount; i++) {
                    CaseDataItem randomItem = getCaseData().getRandomItem();
                    armorStands.add(spawnArmorStand(getLocation(), randomItem, small));
                }
                int rand = new Random().nextInt(armorStands.size());
                int additionalSteps = armorStands.size() - rand;
                double additionalAngle = additionalSteps * (2 * Math.PI / armorStands.size());
                targetAngle = 2 * Math.PI * scrollCount + additionalAngle;
                Collections.swap(armorStands, 0, rand);
            }
        }

        private void handleFlameEffects() {
            if (useFlame) {
                double progress = Math.min(ticks / (double) animationTime * 0.9, 1); // progress from 0 to 1
                double easedProgress = 1 - Math.pow(1 - progress, easeAmount); // ease-out

                double deltaX = Math.max((1 - easedProgress) * radius, 0.4);
                double deltaY = easedProgress * radius + 0.7;

                double theta = ticks / (20.0 / 3);
                spawnFlameEffect(deltaX, deltaY, theta);
                spawnFlameEffect(deltaX, deltaY, theta + Math.PI); // For the opposite side
            }
        }

        private void spawnFlameEffect(double deltaX, double deltaY, double theta) {
            double dx = deltaX * Math.sin(theta);
            double dz = deltaX * Math.cos(theta);
            Location particleLocation = flocation.clone().add(dx, deltaY, dz);
            world.spawnParticle(flameParticle, particleLocation, 1, 0, 0, 0, 0, null);
        }

        private void moveArmorStands(double angle) {
            for (ArmorStandCreator entity : armorStands) {
                double x = radius * Math.sin(angle);
                double y = radius * Math.cos(angle);

                Vector rotationAxis = loc.getDirection().crossProduct(new Vector(0, 1, 0)).normalize();
                Location newLoc = flocation.clone().add(rotationAxis.multiply(x).add(loc.getDirection().multiply(y)));
                entity.teleport(BukkitUtils.fromBukkit(newLoc));
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
            end();
            armorStands.clear();
        }
    }

    private ArmorStandCreator spawnArmorStand(CaseLocation location, CaseDataItem item, boolean small) {
        CaseDataMaterial material = item.getMaterial();

        ArmorStandCreator as = DCAPI.getInstance().getPlatform().getTools().createArmorStand(location);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        as.setAngle(armorStandEulerAngle);
        as.setCustomName(DCAPI.getInstance().getPlatform().getTools().getPAPI().setPlaceholders(getPlayer(), item.getMaterial().getDisplayName()));
        as.setCustomNameVisible(item.getMaterial().getDisplayName() != null && !item.getMaterial().getDisplayName().isEmpty());
        as.spawn();

        as.setEquipment(itemSlot, material.getItemStack());
        return as;
    }
}
