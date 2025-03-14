package com.jodexindustries.donatecase.spigot.animations.wheel;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.util.Vector;
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
            float pitch = Math.round(getLocation().pitch() / 45.0f) * 45.0f;
            float yaw = Math.round(getLocation().yaw() / 45.0f) * 45.0f;

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

            if (settings.getWheelType() == WheelSettings.WheelType.FULL) {
                // FULL logic - unique items
                List<CaseDataItem> uniqueItems = new ArrayList<>(getCaseData().items().values());

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
                    Sound sound = settings.scroll.sound();
                    if (sound != null) {
                        world.playSound(location, sound, settings.scroll.volume, settings.scroll.pitch);
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
        CaseDataMaterial material = item.material();

        ArmorStandCreator as = DCAPI.getInstance().getPlatform().getTools().createArmorStand(location);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        as.setAngle(settings.armorStandEulerAngle);
        as.setCustomName(DCAPI.getInstance().getPlatform().getPAPI().setPlaceholders(getPlayer(), item.material().displayName()));
        as.setCustomNameVisible(item.material().displayName() != null && !item.material().displayName().isEmpty());
        as.spawn();

        as.setEquipment(settings.getItemSlot(), material.itemStack());
        return as;
    }
}
