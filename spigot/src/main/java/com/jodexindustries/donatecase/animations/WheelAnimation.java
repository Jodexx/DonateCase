package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.tools.BukkitUtils;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.*;
import java.util.function.Consumer;

public class WheelAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    private final List<ArmorStandCreator> armorStands = new ArrayList<>();

    private Settings settings;

    private enum WheelType {
        FULL,  // No duplicates, all unique items
        RANDOM; // Can have duplicates, random items
    }

    @SneakyThrows
    @Override
    public void start() {
        this.settings = getSettings().get(Settings.class);

        api.getPlatform().getScheduler().run(api.getPlatform(), new Task(), 0L, 0L);
    }

    @ConfigSerializable
    private static class Settings {

        @Setting("CircleRadius")
        private double radius;

        @Setting("Scroll")
        private Scroll scroll;

        @Setting("Flame")
        private Flame flame;

        @Setting("ItemsCount")
        private int itemsCount;

        @Setting("ItemSlot")
        private String itemSlot;

        @Setting("Pose")
        private ArmorStandEulerAngle armorStandEulerAngle;

        @Setting("Type")
        private String wheelType;

        public EquipmentSlot getItemSlot() {
            if (itemSlot == null) return EquipmentSlot.HEAD;
            try {
                return EquipmentSlot.valueOf(itemSlot);
            } catch (IllegalArgumentException e) {
                return EquipmentSlot.HEAD;
            }
        }

        /**
         * Safely parse the wheel type.
         */
        public WheelType getWheelType() {
            if (wheelType == null) return WheelType.RANDOM;
            try {
                return WheelType.valueOf(wheelType);
            } catch (IllegalArgumentException e) {
                return WheelType.RANDOM;
            }
        }

        @ConfigSerializable
        private static class Scroll {

            @Setting("Time")
            private int time = 100;

            @Setting("Count")
            private int count = 1;

            @Setting("EaseAmount")
            private double easeAmount = 2.5;

            @Setting("Sound")
            private String sound;

            @Setting("Volume")
            private float volume;

            @Setting("Pitch")
            private float pitch;

            private Sound getSound() {
                if (sound == null) return null;
                try {
                    return Sound.valueOf(sound);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }

        @ConfigSerializable
        private static class Flame {

            @Setting("Enabled")
            private boolean enabled;

            @Setting("Particle")
            private Particle particle = Particle.FLAME;
        }
    }

    private class Task implements Consumer<SchedulerTask> {

        private final Location location = BukkitUtils.toBukkit(getLocation().clone())
                .add(0.5, 0, 0.5)
                .add(0 + getSettings().node("LiftingAlongX").getDouble(),
                -1 + getSettings().node("LiftingAlongY").getDouble(),
                0 + getSettings().node("LiftingAlongZ").getDouble());

        private final World world;

        private final double baseAngle;
        private double lastCompletedRotation = 0.0;
        private int ticks;
        private double targetAngle;
        private final double rotationThreshold;
        private final double offset;

        public Task() {
            float pitch = Math.round(getLocation().getPitch() / 45.0f) * 45.0f;
            float yaw = Math.round(getLocation().getYaw() / 45.0f) * 45.0f;

            location.setPitch(pitch);
            location.setYaw(yaw);

            this.baseAngle = location.clone().getDirection().angle(new Vector(0, 0, 1));

            initializeItems();

            rotationThreshold = Math.PI / armorStands.size();
            offset = 2 * rotationThreshold;

            world = getPlayer().getWorld();
        }

        @Override
        public void accept(SchedulerTask task) {
            ticks++;

            double progress = Math.min(ticks / (double) settings.scroll.time, 1.0); // Progress from 0 to 1
            double easedProgress = 1 - Math.pow(1 - progress, settings.scroll.easeAmount); // ease-out
            double currentAngle = easedProgress * targetAngle;

            if (ticks <= settings.scroll.time) {
                handleFlameEffects();
                moveArmorStands(currentAngle);
            }

            if (ticks == settings.scroll.time) {
                preEnd();
            }

            if (ticks >= settings.scroll.time + 20) {
                endAnimation(task);
            }
        }

        private void initializeItems() {
            boolean small = getSettings().node("SmallArmorStand").getBoolean(true);

            if (settings.getWheelType() == WheelType.FULL) {
                // FULL logic - unique items
                List<CaseDataItem> uniqueItems = new ArrayList<>(getCaseData().getItems().values());

                if (getSettings().node("Shuffle").getBoolean(true)) {
                    Collections.shuffle(uniqueItems);
                }

                int additionalSteps = 0;
                for (CaseDataItem uniqueItem : uniqueItems) {
                    if (uniqueItem.getName().equals(getWinItem().getName())) {
                        additionalSteps = uniqueItems.size() - armorStands.size();
                        armorStands.add(spawnArmorStand(getLocation(), getWinItem(), small));
                    }
                    else armorStands.add(spawnArmorStand(getLocation(), uniqueItem, small));
                }

                double additionalAngle = additionalSteps * (2 * Math.PI / armorStands.size());
                targetAngle = 2 * Math.PI * settings.scroll.count + additionalAngle;
            }
            else {
                // RANDOM logic - random items with duplicates
                armorStands.add(spawnArmorStand(getLocation(), getWinItem(), small));
                for (int i = 1; i < settings.itemsCount; i++) {
                    CaseDataItem randomItem = getCaseData().getRandomItem();
                    armorStands.add(spawnArmorStand(getLocation(), randomItem, small));
                }
                int rand = new Random().nextInt(armorStands.size());
                int additionalSteps = armorStands.size() - rand;
                double additionalAngle = additionalSteps * (2 * Math.PI / armorStands.size());
                targetAngle = 2 * Math.PI * settings.scroll.count + additionalAngle;
                Collections.swap(armorStands, 0, rand);
            }
        }

        private void handleFlameEffects() {
            if (settings.flame.enabled) {
                double progress = Math.min(ticks / (double) settings.scroll.time * 0.9, 1); // progress from 0 to 1
                double easedProgress = 1 - Math.pow(1 - progress, settings.scroll.easeAmount); // ease-out

                double deltaX = Math.max((1 - easedProgress) * settings.radius, 0.4);
                double deltaY = easedProgress * settings.radius + 0.7;

                double theta = ticks / (20.0 / 3);
                spawnFlameEffect(deltaX, deltaY, theta);
                spawnFlameEffect(deltaX, deltaY, theta + Math.PI); // For the opposite side
            }
        }

        private void spawnFlameEffect(double deltaX, double deltaY, double theta) {
            double dx = deltaX * Math.sin(theta);
            double dz = deltaX * Math.cos(theta);
            Location particleLocation = location.clone().add(dx, deltaY, dz);
            world.spawnParticle(settings.flame.particle, particleLocation, 1, 0, 0, 0, 0, null);
        }

        private void moveArmorStands(double angle) {
            for (ArmorStandCreator entity : armorStands) {
                double x = settings.radius * Math.sin(angle);
                double y = settings.radius * Math.cos(angle);

                Vector rotationAxis = location.getDirection().crossProduct(new Vector(0, 1, 0)).normalize();
                Location newLoc = location.clone().add(rotationAxis.multiply(x).add(location.getDirection().multiply(y)));
                entity.teleport(BukkitUtils.fromBukkit(newLoc));
                angle += offset;

                double currentAngle = angle - baseAngle;
                if (currentAngle - lastCompletedRotation >= rotationThreshold) {
                    if(settings.scroll.getSound() != null) {
                        world.playSound(location, settings.scroll.getSound(), settings.scroll.volume, settings.scroll.pitch);
                        lastCompletedRotation = currentAngle;
                    }
                }
            }
        }

        private void endAnimation(SchedulerTask task) {
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
        as.setAngle(settings.armorStandEulerAngle);
        as.setCustomName(DCAPI.getInstance().getPlatform().getPAPI().setPlaceholders(getPlayer(), item.getMaterial().getDisplayName()));
        as.setCustomNameVisible(item.getMaterial().getDisplayName() != null && !item.getMaterial().getDisplayName().isEmpty());
        as.spawn();

        as.setEquipment(settings.getItemSlot(), material.getItemStack());
        return as;
    }
}
