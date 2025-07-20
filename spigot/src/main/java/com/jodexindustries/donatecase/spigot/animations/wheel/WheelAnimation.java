package com.jodexindustries.donatecase.spigot.animations.wheel;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMaterial;
import com.jodexindustries.donatecase.api.data.storage.CaseVector;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;
import java.util.function.Consumer;

public class WheelAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    private final List<ArmorStandCreator> armorStands = new ArrayList<>();

    private WheelSettings settings;

    @Override
    public void start() {
        try {
            this.settings = getSettings().get(WheelSettings.class);
        } catch (SerializationException e) {
            throw new RuntimeException("Error with parsing animation settings", e);
        }

        api.getPlatform().getScheduler().run(api.getPlatform(), new Task(), 0L, 0L);
    }

    private class Task implements Consumer<SchedulerTask> {

        private final CaseLocation location = getLocation().clone();

        private final Location bukkitLocation;

        private final World world;

        private final double baseAngle;
        private double lastCompletedRotation = 0.0;
        private int ticks;
        private double targetAngle;
        private final double rotationThreshold;
        private final double offset;

        public Task() {
            float pitch = settings.facing == null ? Math.round(location.pitch() / 45.0f) * 45.0f : settings.facing.pitch;
            float yaw = settings.facing == null ? Math.round(location.yaw() / 45.0f) * 45.0f : settings.facing.yaw;

            if(settings.startPosition != null) location.add(settings.startPosition);
            location.pitch(pitch);
            location.yaw(yaw);

            this.baseAngle = location.clone().getDirection().angle(new CaseVector(0, 0, 1));

            initializeItems();

            rotationThreshold = Math.PI / armorStands.size();
            offset = 2 * rotationThreshold;

            world = getPlayer().getWorld();

            this.bukkitLocation = BukkitUtils.toBukkit(location);
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
            if (settings.wheelType == WheelSettings.WheelType.FULL) {
                // FULL logic - unique items
                List<CaseItem> uniqueItems = new ArrayList<>(getDefinition().items().items().values());

                if (settings.shuffle) {
                    Collections.shuffle(uniqueItems);
                }

                int additionalSteps = 0;
                for (CaseItem uniqueItem : uniqueItems) {
                    if (uniqueItem.name().equals(getItem().name())) {
                        additionalSteps = uniqueItems.size() - armorStands.size();
                        armorStands.add(spawnArmorStand(location, getItem(), settings.smallArmorStand));
                    } else armorStands.add(spawnArmorStand(location, uniqueItem, settings.smallArmorStand));
                }

                double additionalAngle = additionalSteps * (2 * Math.PI / armorStands.size());
                targetAngle = 2 * Math.PI * settings.scroll.count + additionalAngle;
            } else {
                // RANDOM logic - random items with duplicates
                armorStands.add(spawnArmorStand(location, getItem(), settings.smallArmorStand));
                for (int i = 1; i < settings.itemsCount; i++) {
                    CaseItem randomItem = getDefinition().items().getRandomItem();
                    armorStands.add(spawnArmorStand(location, randomItem, settings.smallArmorStand));
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
            world.spawnParticle(settings.flame.particle, bukkitLocation.clone().add(dx, deltaY, dz), 1, 0, 0, 0, 0, null);
        }

        private void moveArmorStands(double angle) {
            for (ArmorStandCreator entity : armorStands) {
                double x = settings.radius * Math.sin(angle);
                double y = settings.radius * Math.cos(angle);

                CaseVector rotationAxis = location.getDirection().crossProduct(new CaseVector(0, 1, 0)).normalize();
                CaseLocation newLoc = location.clone().add(rotationAxis.multiply(x).add(location.getDirection().multiply(y)));
                entity.teleport(newLoc);
                angle += offset;

                double currentAngle = angle - baseAngle;
                if (currentAngle - lastCompletedRotation >= rotationThreshold) {
                    Sound sound = settings.scroll.sound();
                    if (sound != null) {
                        world.playSound(bukkitLocation, sound, settings.scroll.volume, settings.scroll.pitch);
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

    private ArmorStandCreator spawnArmorStand(CaseLocation location, CaseItem item, boolean small) {
        CaseMaterial material = item.material();

        ArmorStandCreator as = api.getPlatform().getTools().createArmorStand(getUuid(), location);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        if (settings.armorStandEulerAngle != null) as.setAngle(settings.armorStandEulerAngle);
        as.setCustomName(api.getPlatform().getPAPI().setPlaceholders(getPlayer(), item.material().displayName()));
        as.setCustomNameVisible(item.material().displayName() != null && !item.material().displayName().isEmpty());
        as.spawn();

        as.setEquipment(settings.itemSlot, material.itemStack());
        return as;
    }
}
