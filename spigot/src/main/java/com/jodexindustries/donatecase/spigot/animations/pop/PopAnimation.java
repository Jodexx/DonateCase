package com.jodexindustries.donatecase.spigot.animations.pop;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.spigot.animations.Facing;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.spigot.tools.Pair;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class PopAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    private PopSettings settings;

    @Override
    public void start() {
        try {
            this.settings = getSettings().get(PopSettings.class);
        } catch (SerializationException e) {
            throw new RuntimeException("Error with parsing animation settings", e);
        }

        List<Pair<ArmorStandCreator, CaseLocation>> asList = new ArrayList<>();
        double origX = getLocation().x() + 0.5, origY = getLocation().y() - 0.5, origZ = getLocation().z() + 0.5;

        CaseLocation origCaseLocation = new CaseLocation(origX, origY, origZ);

        for (double y = -1; y < 2; y++) {
            for (double horOffset = -1; horOffset < 2; horOffset++) {
                if (!(y == 0 && horOffset == 0)) {

                    getLocation().y(origY + (settings.rounded ? (y / 1.4142) : y));

                    double horizonOffset = settings.rounded ? (horOffset * (y == 0 ? 1 : 0.707)) : horOffset;

                    if (settings.facing == Facing.EAST || settings.facing == Facing.WEST) {
                        getLocation().z(origZ + horizonOffset);
                    } else {
                        getLocation().x(origX + horizonOffset);
                    }

                    final ArmorStandCreator as = DCAPI.getInstance().getPlatform().getTools().createArmorStand(getUuid(), getLocation());

                    as.setVisible(false);
                    as.setGravity(false);
                    as.setSmall(true);
                    as.teleport(origCaseLocation);
                    as.spawn();

                    double yOffset = origY + (settings.rounded ? (y / (horOffset == 0 ? 1 : 1.4142)) : y) * settings.radius;

                    if (settings.facing == Facing.EAST || settings.facing == Facing.WEST) {
                        asList.add(Pair.of(as, new CaseLocation(origX, yOffset, origZ + horizonOffset * settings.radius)));
                    } else {
                        asList.add(Pair.of(as, new CaseLocation(origX + horizonOffset * settings.radius, yOffset, origZ)));
                    }

                }
            }
        }

        api.getPlatform().getScheduler().run(api.getPlatform(), new Task(asList, origCaseLocation), 0L, settings.period);
    }


    private class Task implements Consumer<SchedulerTask> {

        private int tick;

        private final CaseLocation location;
        private final List<Pair<ArmorStandCreator, CaseLocation>> asList;

        private final World world;

        private final List<Integer> indexes = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
        private int randomIndex;

        public Task(final List<Pair<ArmorStandCreator, CaseLocation>> asList, CaseLocation location) {
            this.asList = asList;
            this.location = location;

            this.world = getPlayer().getWorld();
        }

        @Override
        public void accept(SchedulerTask task) {
            if (tick == 0) {
                alignArmorStands();
            }

            if (tick == 10) {
                fillStandItem();
            }

            if (tick == 10) {
                for (Pair<ArmorStandCreator, CaseLocation> pair : asList) {
                    ArmorStandCreator as = pair.fst;
                    CaseLocation target = pair.snd;
                    as.teleport(target);
                }
            }

            if (tick >= 30 && tick % 15 == 0 && tick <= 129) {
                handleRemoveLogic();
            }

            if (tick == 129) {
                ArmorStandCreator as = asList.get(randomIndex).fst;
                location.x(as.getLocation().x());
                location.y(as.getLocation().y());
                location.z(as.getLocation().z());
                location.yaw(as.getLocation().yaw());
                preEnd();
            }

            if (tick >= 129) {
                ArmorStandCreator as = asList.get(randomIndex).fst;
                location.yaw(location.yaw() + 15);
                as.teleport(location);
            }


            if (tick >= 170) {
                cleanup(task);
            }
            tick++;
        }

        public void handleRemoveLogic() {
            Random random = new Random();
            int randomIndex = random.nextInt(indexes.size());
            int initialIndex = indexes.get(randomIndex);

            ArmorStandCreator as = asList.get(initialIndex).fst;
            final Location bukkitLocation = new Location(world, as.getLocation().x(), as.getLocation().y() + 1, as.getLocation().z());

            world.spawnParticle(Particle.CLOUD, bukkitLocation, 0);
            as.remove();

            indexes.remove(randomIndex);

            Sound sound = settings.scroll.sound();
            if (sound != null) world.playSound(bukkitLocation, sound, settings.scroll.volume, settings.scroll.pitch);

        }

        private void alignArmorStands() {
            for (Pair<ArmorStandCreator, CaseLocation> pair : asList) {
                pair.fst.teleport(location.yaw(settings.facing.yaw));
            }
        }

        private void fillStandItem() {
            Random random = new Random();
            randomIndex = random.nextInt(8);

            Pair<ArmorStandCreator, CaseLocation> win = asList.get(randomIndex);
            indexes.remove(randomIndex);

            win.fst.setEquipment(settings.itemSlot, getWinItem().material().itemStack());
            if (getWinItem().material().displayName() != null && !getWinItem().material().displayName().isEmpty())
                win.fst.setCustomNameVisible(true);
            win.fst.setCustomName(api.getPlatform().getPAPI().setPlaceholders(getPlayer(), getWinItem().material().displayName()));
            win.fst.updateMeta();

            for (int i = 0; i < 8; i++) {
                if (i != randomIndex) {
                    Pair<ArmorStandCreator, CaseLocation> pair = asList.get(i);
                    ArmorStandCreator as = pair.fst;
                    CaseDataItem item = getCaseData().getRandomItem();
                    as.setEquipment(settings.itemSlot, item.material().itemStack());

                    String winGroupDisplayName = api.getPlatform().getPAPI().setPlaceholders(getPlayer(),
                            item.material().displayName());

                    if (item.material().displayName() != null && !item.material().displayName().isEmpty()) {
                        as.setCustomNameVisible(true);
                    }
                    as.setCustomName(winGroupDisplayName);
                    as.updateMeta();
                }
            }
        }


        private void cleanup(SchedulerTask task) {
            asList.forEach(pair -> pair.fst.remove());
            task.cancel();
            end();
        }

    }
}