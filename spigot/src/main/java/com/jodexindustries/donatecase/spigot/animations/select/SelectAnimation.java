package com.jodexindustries.donatecase.spigot.animations.select;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.spigot.tools.Pair;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@Getter
public class SelectAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    private Task task;

    @Override
    public void start() {
        List<Pair<ArmorStandCreator, CaseLocation>> asList = new ArrayList<>();

        double origX = getLocation().x() + 0.5, origY = getLocation().y() - 0.5, origZ = getLocation().z() + 0.5;

        CaseLocation origCaseLocation = new CaseLocation(origX, origY, origZ);

        String facing = getSettings().node("Facing").getString();

        for (double y = -1; y < 2; y++) {
            for (double hor_offset = -1; hor_offset < 2; hor_offset++) {
                if (!(y == 0 && hor_offset == 0)) {

                    getLocation().y(origY + y);

                    if (facing != null) {
                        if (facing.equals("east") || facing.equals("west")) {
                            getLocation().z(origZ + hor_offset);
                        } else if (facing.equals("south") || facing.equals("north")) {
                            getLocation().x(origX + hor_offset);
                        } else {
                            throw new RuntimeException("Incorrect facing in config");
                        }
                    } else {
                        facing = "east";
                    }

                    final ArmorStandCreator as = DCAPI.getInstance().getPlatform().getTools().createArmorStand(getUuid(), getLocation());

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

        this.task = new Task(asList, origCaseLocation, facing);

        api.getPlatform().getScheduler().run(api.getPlatform(), task, 0L, period);
    }

    public class Task implements Consumer<SchedulerTask> {

        private int tick;

        private final CaseLocation location;
        private final List<Pair<ArmorStandCreator, CaseLocation>> asList;

        public final EquipmentSlot itemSlot;
        private final World world;
        private final String facing;

        public boolean canSelect = false;

        public volatile boolean selected = false;

        private Pair<ArmorStandCreator, CaseLocation> randomAS;
        private final List<ArmorStandCreator> toDelete = new ArrayList<>();

        public Task(final List<Pair<ArmorStandCreator, CaseLocation>> asList, CaseLocation location, String facing) {
            this.asList = asList;
            this.location = location;

            this.itemSlot = EquipmentSlot.valueOf(getSettings().node("ItemSlot").getString("HEAD").toUpperCase());

            this.world = getPlayer().getWorld();
            this.facing = facing;
        }

        @Override
        public void accept(SchedulerTask task) {
            if (tick == 0) {
                int facingYaw = getYaw(facing);
                for (Pair<ArmorStandCreator, CaseLocation> pair : asList) {
                    ArmorStandCreator as = pair.fst;
                    location.yaw(facingYaw);
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
                CaseLocation currentLocation = as.getLocation();

                as.teleport(as.getLocation().add(
                        currentLocation.x() > needLocation.x() ? -0.1 : (Math.abs(currentLocation.x() - needLocation.x()) < 1e-6 ? 0 : 0.1),
                        currentLocation.y() > needLocation.y() ? -0.1 : (Math.abs(currentLocation.y() - needLocation.y()) < 1e-6 ? 0 : 0.1),
                        currentLocation.z() > needLocation.z() ? -0.1 : (Math.abs(currentLocation.z() - needLocation.z()) < 1e-6 ? 0 : 0.1)
                ));
                as.teleport(as.getLocation());

                final Location bukkitLocation = new Location(world, as.getLocation().x(), as.getLocation().y() + 1, as.getLocation().z());

                world.spawnParticle(Particle.valueOf("CLOUD"), bukkitLocation, 0);

            }

            if (tick == 90) {
                this.canSelect = true;
            }

            if (tick > 90) {
                // event
                if (this.selected) {
                    task.cancel();
                    api.getPlatform().getScheduler().run(api.getPlatform(), this::end, 40L);
                }
            }

            // timeout
            if (tick >= 1000) {
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

        private int getYaw(String facing) {
            switch (facing) {
                case "west":
                    return 90;
                case "north":
                    return 180;
                case "east":
                    return 270;
                default:
                    return 0;
            }
        }
    }

}