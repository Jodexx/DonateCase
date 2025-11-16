package com.jodexindustries.donatecase.spigot.animations.futurewheel;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMaterial;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.data.storage.CaseVector;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FutureWheelAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    private FutureWheelSettings settings;

    @Override
    public void start() {
        try {
            this.settings = getSettings().get(FutureWheelSettings.class);
        } catch (SerializationException e) {
            throw new RuntimeException("Error with parsing animation settings", e);
        }

        api.getPlatform().getScheduler().run(api.getPlatform(), new Task(), 0L, 0L);
    }

    public class Task implements Consumer<SchedulerTask> {
        int i = 0;

        private final CaseLocation location = getLocation().clone();
        private final Location bukkitLocation;
        private final World world;

        private final List<ArmorStandCreator> armorStandList;
        private final List<ArmorStandCreator> hologramList;
        private final List<Object> itemStackList;

        private final Sound spawnSound = settings.spawn.sound();
        private final Sound scrollSound = settings.scroll.sound();

        private final double offset;

        private final double scrollingTime;

        private int winEntity;

        private double angle = 0;

        private int index = 0;

        private boolean finished = false;

        private final int spawnTime;

        public Task() {
            float pitch = settings.facing == null ? Math.round(location.pitch() / 45.0f) * 45.0f : settings.facing.pitch;
            float yaw = settings.facing == null ? Math.round(location.yaw() / 45.0f) * 45.0f : settings.facing.yaw;

            if (settings.startPosition != null) location.add(settings.startPosition);
            location.pitch(pitch);
            location.yaw(yaw);

            this.bukkitLocation = BukkitUtils.toBukkit(location);
            this.world = bukkitLocation.getWorld();

            Map<String, CaseItem> items = getDefinition().items().items();

            armorStandList = new ArrayList<>(items.size());
            hologramList = new ArrayList<>(items.size());
            itemStackList = new ArrayList<>(items.size());

            for (CaseItem item : items.values()) {
                CaseMaterial material = item.material();
                if (world == null) continue;
                armorStandList.add(spawnArmorStand(item, false));
                itemStackList.add(material.itemStack());

                // Spawn hologram
                hologramList.add(spawnArmorStand(item, true));
            }

            int itemCount = armorStandList.size();
            this.offset = 2 * Math.PI / itemCount;

            this.spawnTime = settings.spawn.interval * itemCount;

            scrollingTime = spawnTime + settings.scrollingTime;
        }

        @Override
        public void accept(SchedulerTask task) {
            i++;
            if (i == 1) {
                for (ArmorStandCreator entity : armorStandList) {
                    double x = settings.radius * Math.sin(angle);
                    double y = settings.radius * Math.cos(angle);
                    CaseVector rotationAxis = location.getDirection().crossProduct(new CaseVector(0, 1, 0)).normalize();
                    CaseLocation loc = location.clone().add(rotationAxis.multiply(x).add(location.getDirection().multiply(y)));
                    entity.teleport(loc);
                    angle += offset;
                }
                angle = 0;
                for (ArmorStandCreator entity : hologramList) {
                    double x = settings.radius * Math.sin(angle);
                    double y = settings.radius * Math.cos(angle);
                    CaseVector rotationAxis = location.getDirection().crossProduct(new CaseVector(0, 1, 0)).normalize();
                    CaseLocation loc = location.clone().add(rotationAxis.multiply(x).add(location.getDirection().multiply(y))).add(settings.hologramPosition);
                    entity.teleport(loc);
                    angle += offset;
                }
                angle = 0;
            }

            if (i >= settings.spawn.interval && i <= spawnTime) {
                if (i % settings.spawn.interval == 0) {
                    int index = (i / settings.spawn.interval) - 1;
                    ArmorStandCreator entity = armorStandList.get(index);
                    Object item = itemStackList.get(index);
                    entity.setEquipment(EquipmentSlot.HEAD, item);

                    ArmorStandCreator holo = hologramList.get(index);
                    holo.setCustomNameVisible(true);
                    holo.updateMeta();

                    if (spawnSound != null) {
                        world.playSound(bukkitLocation, spawnSound, 1, 1);
                    }
                    if (settings.spawn.particle != null) {
                        world.spawnParticle(settings.spawn.particle, BukkitUtils.toBukkit(entity.getLocation()), settings.spawn.particleCount, 0, 0, 0, null);
                    }
                }
            }

            // if ticks > then previous time action and <= full scrolling time with previous time action
            // also if win item not found (!finished)
            if (i > spawnTime && ((i <= scrollingTime) || !finished)) {
                if (i % settings.skipTicks == 0) {
                    // start rolling here

                    if (index - 1 >= 0) {
                        ArmorStandCreator previous = armorStandList.get(index - 1);
                        previous.setGlowing(false);
                        previous.updateMeta();
                    }

                    if (index >= armorStandList.size()) {
                        index = 0;
                    }
                    ArmorStandCreator entity = armorStandList.get(index);
                    entity.setGlowing(true);
                    entity.updateMeta();

                    if (scrollSound != null) {
                        world.playSound(bukkitLocation, scrollSound, 1, 1);
                    }
                    if (settings.scroll.particle != null) {
                        world.spawnParticle(settings.scroll.particle, BukkitUtils.toBukkit(entity.getLocation()), settings.scroll.particleCount, 0, 0, 0, null);
                    }

                    index++;

                    if (i > scrollingTime) {
                        if (entity.getEntityId() == winEntity) {
                            finished = true;
                            preEnd();
                        }
                    }
                }
            }

            if (i > scrollingTime + (20 * 3) && finished) {
                for (ArmorStandCreator creator : hologramList) {
                    creator.remove();
                }
                for (ArmorStandCreator armorStandCreator : armorStandList) {
                    armorStandCreator.remove();
                }
                end();
                task.cancel();
            }
        }

        private ArmorStandCreator spawnArmorStand(CaseItem item, boolean hologram) {
            ArmorStandCreator creator = api.getPlatform().getTools().createArmorStand(getUuid(), location);

            if (item.name().equals(getItem().name()) && !hologram) winEntity = creator.getEntityId();
            creator.setMarker(true);
            creator.setVisible(false);
            creator.setCollidable(false);
            creator.setGravity(false);
            creator.setCustomNameVisible(false);

            if (hologram)
                creator.setCustomName(api.getPlatform().getPAPI().setPlaceholders(getPlayer(), item.material().displayName()));
            creator.setSmall(true);
            creator.spawn();
            creator.updateMeta();
            return creator;
        }
    }
}
