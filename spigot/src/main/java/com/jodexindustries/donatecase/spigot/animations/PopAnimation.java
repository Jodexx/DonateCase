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

        String facing = getSettings().node("Facing").getString("east");
        boolean rounded = getSettings().node("Rounded").getBoolean(true);

        double radius = getSettings().node("Radius").getDouble(1.1);

        for (double y = -1; y < 2; y++) {
            for (double hor_offset = -1; hor_offset < 2; hor_offset++) {
                if (!(y == 0 && hor_offset == 0)) {

                    getLocation().y(origY + (rounded ? (y / 1.4142) : y));

                    double horizont_offset = rounded ? (hor_offset * (y == 0 ? 1 : 0.707)) : hor_offset;

                    if (facing.equals("east") || facing.equals("west")) {
                        getLocation().z(origZ + horizont_offset);
                    } else if (facing.equals("south") || facing.equals("north")) {
                        getLocation().x(origX + horizont_offset);
                    } else {
                        throw new Exception("Incorrect facing in config");
                    }

                    final ArmorStandCreator as = DCAPI.getInstance().getPlatform().getTools().createArmorStand(getLocation());

                    as.setVisible(false);
                    as.setGravity(false);
                    as.setSmall(true);
                    as.teleport(origCaseLocation);
                    as.spawn();

                    double y_offset = origY + (rounded ? (y / (hor_offset == 0 ? 1 : 1.4142)) : y) * radius;

                    if (facing.equals("east") || facing.equals("west")) {
                        asList.add(Pair.of(as, new CaseLocation(origX, y_offset, origZ + horizont_offset * radius)));
                    } else {
                        asList.add(Pair.of(as, new CaseLocation(origX + horizont_offset * radius, y_offset, origZ)));
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

        private final Sound popSound = Sound.valueOf(getSettings().node("Scroll", "Sound").getString("ENTITY_ITEM_PICKUP"));
        private final float popVolume = getSettings().node("Scroll", "Volume").getFloat(10);
        private final float popPitch = getSettings().node("Scroll", "Pitch").getFloat(1);

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

        private int getYaw(String facing) {
            switch (facing) {
                case "west":
                    return 90;
                case "north":
                    return 180;
                case "east":
                    return 270;
                case "south":
                default:
                    return 0;
            }
        }

        public void handleRemoveLogic() {
            Random random = new Random();
            int ii = random.nextInt(indexes.size());
            int index = indexes.get(ii);

            ArmorStandCreator as = asList.get(index).fst;
            final Location bukkitLocation = new Location(world, as.getLocation().x(), as.getLocation().y() + 1, as.getLocation().z());

            world.spawnParticle(Particle.valueOf("CLOUD"), bukkitLocation, 0);
            as.remove();

            indexes.remove(ii);
            world.playSound(bukkitLocation, popSound, popVolume, popPitch);

        }

        public void changeArmorStandPosition() {
            for (Pair<ArmorStandCreator, CaseLocation> pair : asList) {
                ArmorStandCreator as = pair.fst;
                CaseLocation target = pair.snd;
                CaseLocation current = as.getLocation();
                as.teleport(current.add(
                        stepTowards(current.x(), target.x()),
                        stepTowards(current.y(), target.y()),
                        stepTowards(current.z(), target.z())
                ));
            }
        }

        private void alignArmorStands() {
            int yaw = getYaw(facing);
            for (Pair<ArmorStandCreator, CaseLocation> pair : asList) {
                pair.fst.teleport(location.yaw(yaw));
            }
        }

        private void fillStandItem(){
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


        private void cleanup(SchedulerTask task) {
            asList.forEach(pair -> pair.fst.remove());
            task.cancel();
            end();
        }

        private double stepTowards(double current, double target) {
            return Double.compare(current, target) == 0 ? 0 : (current > target ? -0.05 : 0.05);
        }
    }
}