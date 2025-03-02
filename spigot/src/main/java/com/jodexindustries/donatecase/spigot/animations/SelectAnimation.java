package com.jodexindustries.donatecase.spigot.animations;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.spigot.tools.Pair;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class SelectAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    @SneakyThrows
    @Override
    public void start() {
        List<Pair<ArmorStandCreator, CaseLocation>> asList = new ArrayList<>();

        double origX = getLocation().x() + 0.5, origY = getLocation().y() - 0.5, origZ = getLocation().z() + 0.5;

        CaseLocation origCaseLocation = new CaseLocation(origX, origY, origZ);

        String facing = getSettings().node("Facing").getString();
        System.out.println(facing);

        for (double y = -1; y < 2; y++) {
            for (double x_or_z = -1; x_or_z < 2; x_or_z++) {
                if (!(y == 0 && x_or_z == 0)) {

                    getLocation().y(origY + y);

                    if (facing != null) {
                        if (facing.equals("east") || facing.equals("west")) {
                            getLocation().z(origZ + x_or_z);
                        } else if (facing.equals("south") || facing.equals("north")) {
                            getLocation().x(origX + x_or_z);
                        } else {
                            throw new Exception("Incorrect facing in config");
                        }
                    } else {
                        facing = "east";
                    }

                    final ArmorStandCreator as = DCAPI.getInstance().getPlatform().getTools().createArmorStand(getLocation());

                    as.setVisible(false);

                    as.setGravity(false);

                    as.setSmall(true);

                    as.teleport(origCaseLocation);

                    as.spawn();

                    if (facing.equals("east") || facing.equals("west")) {
                        asList.add(Pair.of(as, new CaseLocation(origX, origY + y, origZ + x_or_z)));
                    } else {
                        asList.add(Pair.of(as, new CaseLocation(origX + x_or_z, origY + y, origZ)));
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
        private List<ArmorStandCreator> toDelete = new ArrayList<>();

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

            if (tick >= 10 && tick <= 89) {
                if (tick % 10 == 0) {
                    Random random = new Random();
                    int index = random.nextInt(asList.size());
                    randomAS = asList.get(index);
                    asList.remove(index);
                    toDelete.add(randomAS.fst);
                }
                ArmorStandCreator as = randomAS.fst;

                CaseLocation needLocation = randomAS.snd;

                as.setEquipment(itemSlot, new ItemStack(Material.CHEST));
                as.updateMeta();
                CaseLocation _location = as.getLocation();

                as.teleport(as.getLocation().add(
                        _location.x() > needLocation.x() ? -0.1 : (Math.abs(_location.x() - needLocation.x()) < 1e-6 ? 0 : 0.1),
                        _location.y() > needLocation.y() ? -0.1 : (Math.abs(_location.y() - needLocation.y()) < 1e-6 ? 0 : 0.1),
                        _location.z() > needLocation.z() ? -0.1 : (Math.abs(_location.z() - needLocation.z()) < 1e-6 ? 0 : 0.1)
                ));
                as.teleport(as.getLocation());

                final Location bukkitLocation = new Location(world, as.getLocation().x(), as.getLocation().y() + 1, as.getLocation().z());

                world.spawnParticle(Particle.valueOf("CLOUD"), bukkitLocation, 0);

            }


            if (tick >= 100) {
                preEnd();
                for (ArmorStandCreator as : toDelete) {
                    as.remove();
                }
                task.cancel();
                end();

            }

            tick++;
        }
    }
}