package com.jodexindustries.donatecase.spigot.animations.select;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.api.data.animation.Facing;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.spigot.tools.Pair;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class SelectAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    @Getter
    private Task task;

    @NotNull
    public SelectSettings settings = new SelectSettings();

    @Override
    public void start() {
        try {
            this.settings = getSettings().get(SelectSettings.class, new SelectSettings());
        } catch (SerializationException e) {
            throw new RuntimeException("Error with parsing animation settings", e);
        }

        List<Pair<ArmorStandCreator, CaseLocation>> asList = new ArrayList<>();

        double origX = getLocation().x() + 0.5, origY = getLocation().y() - 0.5, origZ = getLocation().z() + 0.5;

        CaseLocation origCaseLocation = new CaseLocation(origX, origY, origZ);

        for (double y = -1; y < 2; y++) {
            for (double horizonOffset = -1; horizonOffset < 2; horizonOffset++) {
                if (!(y == 0 && horizonOffset == 0)) {

                    getLocation().y(origY + y);

                    if (settings.facing == Facing.EAST || settings.facing == Facing.WEST) {
                        getLocation().z(origZ + horizonOffset);
                    } else {
                        getLocation().x(origX + horizonOffset);
                    }

                    final ArmorStandCreator as = api.getPlatform().getTools().createArmorStand(getUuid(), getLocation());

                    as.setVisible(false);

                    as.setGravity(false);

                    as.setSmall(true);

                    as.teleport(origCaseLocation);

                    as.spawn();

                    if (settings.facing == Facing.EAST || settings.facing == Facing.WEST) {
                        asList.add(Pair.of(as, new CaseLocation(origX, origY + y * settings.radius, origZ + horizonOffset * settings.radius)));
                    } else {
                        asList.add(Pair.of(as, new CaseLocation(origX + horizonOffset * settings.radius, origY + y * settings.radius, origZ)));
                    }

                }
            }
        }

        this.task = new Task(asList, origCaseLocation);

        api.getPlatform().getScheduler().run(api.getPlatform(), task, 0L, settings.period);
    }

    public class Task implements Consumer<SchedulerTask> {

        private int tick;

        private final CaseLocation location;
        private final List<Pair<ArmorStandCreator, CaseLocation>> asList;

        private final World world;

        public boolean canSelect = false;

        public volatile boolean selected = false;

        private Pair<ArmorStandCreator, CaseLocation> randomAS;
        private final List<ArmorStandCreator> toDelete = new ArrayList<>();

        public Task(final List<Pair<ArmorStandCreator, CaseLocation>> asList, CaseLocation location) {
            this.asList = asList;
            this.location = location;

            this.world = getPlayer().getWorld();
        }

        @Override
        public void accept(SchedulerTask task) {
            if (tick == 0) {
                for (Pair<ArmorStandCreator, CaseLocation> pair : asList) {
                    ArmorStandCreator as = pair.fst;
                    location.yaw(settings.facing.yaw);
                    as.teleport(location);
                }
            }

            if (tick >= 10 && tick < 90) {
                if (tick % 10 == 0 && !asList.isEmpty()) {
                    Random random = new Random();
                    int index = random.nextInt(asList.size());
                    randomAS = asList.get(index);
                    asList.remove(index);
                    toDelete.add(randomAS.fst);
                }

                if (randomAS != null) {
                    ArmorStandCreator as = randomAS.fst;
                    CaseLocation needLocation = randomAS.snd;

                    as.setEquipment(settings.itemSlot, api.getPlatform().getTools().loadCaseItem(settings.item));
                    as.updateMeta();

                    CaseLocation currentLocation = as.getLocation().clone();

                    double deltaX = needLocation.x() - currentLocation.x();
                    double deltaY = needLocation.y() - currentLocation.y();
                    double deltaZ = needLocation.z() - currentLocation.z();

                    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                    double step = distance / 10;

                    double moveX = deltaX * (step / distance);
                    double moveY = deltaY * (step / distance);
                    double moveZ = deltaZ * (step / distance);

                    as.teleport(currentLocation.add(moveX, moveY, moveZ));

                    final Location bukkitLocation = new Location(world, as.getLocation().x(), as.getLocation().y() + 1, as.getLocation().z());

                    world.spawnParticle(Particle.CLOUD, bukkitLocation, 0);
                }
            }


            if (tick == 91) {
                this.canSelect = true;
            }

            if (tick > 91) {
                // event
                if (this.selected) {
                    task.cancel();
                    api.getPlatform().getScheduler().run(api.getPlatform(), this::end, 40L);
                }
            }

            // timeout
            if (tick >= settings.timeout) {
                task.cancel();
                end();
            }
            tick++;
        }

        private void end() {
            SelectAnimation.super.preEnd();
            for (ArmorStandCreator as : toDelete) {
                as.remove();
            }

            SelectAnimation.super.end();
        }

    }

}