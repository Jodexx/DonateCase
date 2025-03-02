package com.jodexindustries.donatecase.spigot.animations;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.spigot.tools.Pair;
import lombok.SneakyThrows;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class PopAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    @SneakyThrows
    @Override
    public void start() {
        List<Pair<ArmorStandCreator, CaseLocation>> asList = new ArrayList<>();
        double origX = getLocation().x() + 0.5, origY = getLocation().y() - 0.5, origZ = getLocation().z() + 0.5;

        CaseLocation origCaseLocation = new CaseLocation(origX, origY, origZ);
        String facing = getSettings().node("Facing").getString();
        if (facing == null)
            facing = "east";

        for (double y = -1; y < 2; y++) {
            for (double hor_offset = -1; hor_offset < 2; hor_offset++) {
                if (!(y == 0 && hor_offset == 0)) {

                    getLocation().y(origY + y);

                    if (facing.equals("east") || facing.equals("west")) {
                        getLocation().z(origZ + hor_offset);
                    } else if (facing.equals("south") || facing.equals("north")) {
                        getLocation().x(origX + hor_offset);
                    } else {
                        throw new Exception("Incorrect facing in config");
                    }

                    final ArmorStandCreator as = DCAPI.getInstance().getPlatform().getTools().createArmorStand(getLocation());

                    as.setVisible(false);
                    as.setGravity(false);
                    as.setSmall(true);
                    as.teleport(origCaseLocation);
                    as.spawn();

                    if (facing.equals("east") || facing.equals("west")) {
                        asList.add(Pair.of(as, new CaseLocation(origX, origY + y, origZ + hor_offset)));
                    } else {
                        asList.add(Pair.of(as, new CaseLocation(origX + hor_offset, origY + y, origZ)));
                    }

                }
            }
        }

        long period = getSettings().node("Scroll", "Period").getLong();

        api.getPlatform().getScheduler().run(api.getPlatform(), new Task(asList, origCaseLocation, facing), 0L, period);
    }

    private class Task implements Consumer<SchedulerTask> {

        private int tick;

        private final CaseLocation location;
        private List<Pair<ArmorStandCreator, CaseLocation>> asList;

        private final EquipmentSlot itemSlot;
        private final World world;
        private final String facing;

        private Pair<ArmorStandCreator, CaseLocation> randomAS;

        private final List<Integer> indexes = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
        private int randomIndex;

        public Task(final List<Pair<ArmorStandCreator, CaseLocation>> asList, CaseLocation location, String facing) {
            this.asList = asList;
            this.location = location;

            this.itemSlot = EquipmentSlot.valueOf(getSettings().node("ItemSlot").getString("HEAD").toUpperCase());

            this.world = getPlayer().getWorld();
            this.facing = facing;
        }

        @SneakyThrows
        @Override
        public void accept(SchedulerTask task) {
            if (tick == 0) {
                for (Pair<ArmorStandCreator, CaseLocation> pair : asList) {
                    ArmorStandCreator as = pair.fst;
                    switch (facing) {
                        case "south":
                            location.yaw(0);
                            break;
                        case "west":
                            location.yaw(90);
                            break;
                        case "north":
                            location.yaw(180);
                            break;
                        case "east":
                            location.yaw(270);
                            break;
                        default:
                            break;
                    }
                    as.teleport(location);
                }
            }

            if (tick == 10) {

                Random random = new Random();
                randomIndex = random.nextInt(8);

                Pair<ArmorStandCreator, CaseLocation> win = asList.get(randomIndex);
                indexes.remove(randomIndex);

                win.fst.setEquipment(itemSlot, getWinItem().material().itemStack());
                if (getWinItem().material().displayName() != null && !getWinItem().material().displayName().isEmpty())
                    win.fst.setCustomNameVisible(true);
                win.fst.setCustomName(DCAPI.getInstance().getPlatform().getPAPI().setPlaceholders(getPlayer(), getWinItem().material().displayName()));
                win.fst.updateMeta();


                for (int i = 0; i < 8; i++) {
                    if (i != randomIndex) {
                        Pair<ArmorStandCreator, CaseLocation> pair = asList.get(i);
                        ArmorStandCreator as = pair.fst;
                        CaseDataItem item = getCaseData().getRandomItem();
                        as.setEquipment(itemSlot, item.material().itemStack());

                        String winGroupDisplayName = DCAPI.getInstance().getPlatform().getPAPI().setPlaceholders(getPlayer(),
                                item.material().displayName());

                        if (item.material().displayName() != null && !item.material().displayName().isEmpty()) {
                            as.setCustomNameVisible(true);
                        }
                        as.setCustomName(winGroupDisplayName);
                        as.updateMeta();
                    }
                }
            }

            if (tick >= 10 && tick <= 30) {
                for (Pair<ArmorStandCreator, CaseLocation> pair : asList) {

                    ArmorStandCreator as = pair.fst;
                    CaseLocation needLocation = pair.snd;

                    CaseLocation _location = as.getLocation();

                    as.teleport(as.getLocation().add(
                            _location.x() > needLocation.x() ? -0.05 : (Math.abs(_location.x() - needLocation.x()) < 1e-6 ? 0 : 0.05),
                            _location.y() > needLocation.y() ? -0.05 : (Math.abs(_location.y() - needLocation.y()) < 1e-6 ? 0 : 0.05),
                            _location.z() > needLocation.z() ? -0.05 : (Math.abs(_location.z() - needLocation.z()) < 1e-6 ? 0 : 0.05)
                    ));

                    as.teleport(as.getLocation());
                }
            }

            if (tick >= 40 && tick % 15 == 0 && tick <= 140) {
                Random random = new Random();
                int ii = random.nextInt(indexes.size());
                int index = indexes.get(ii);

                ArmorStandCreator as = asList.get(index).fst;
                final Location bukkitLocation = new Location(world, as.getLocation().x(), as.getLocation().y() + 1, as.getLocation().z());

                world.spawnParticle(Particle.valueOf("CLOUD"), bukkitLocation, 0);
                as.remove();

                indexes.remove(ii);
            }

            if (tick == 141) {
                ArmorStandCreator as = asList.get(randomIndex).fst;
                location.x(as.getLocation().x());
                location.y(as.getLocation().y());
                location.z(as.getLocation().z());
                preEnd();
            }

            if (tick >= 141) {
                ArmorStandCreator as = asList.get(randomIndex).fst;
                location.yaw(location.yaw() + 15);
                as.teleport(location);
            }


            if (tick >= 170) {
                for (Pair<ArmorStandCreator, CaseLocation> pair : asList) {
                    ArmorStandCreator as = pair.fst;
                    as.remove();
                }
                task.cancel();
                end();

            }

            tick++;
        }
    }
}