package com.jodexindustries.donatecase.spigot.animations;

import com.jodexindustries.donatecase.spigot.BukkitBackend;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import com.jodexindustries.donatecase.spigot.tools.DCToolsBukkit;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Consumer;

public class ShapeAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    @SneakyThrows
    @Override
    public void start() {
        double x = getSettings().node("StartPosition", "X").getDouble(0.5);
        double y = getSettings().node("StartPosition", "Y").getDouble(-0.1);
        double z = getSettings().node("StartPosition", "Z").getDouble(0.5);

        getLocation().add(x, y, z);

        final ArmorStandEulerAngle armorStandEulerAngle = getSettings().node("Pose").get(ArmorStandEulerAngle.class);
        final ArmorStandCreator as = DCAPI.getInstance().getPlatform().getTools().createArmorStand(getLocation());

        boolean small = getSettings().node("Shape", "SmallArmorStand").getBoolean(true);
        if(armorStandEulerAngle != null) as.setAngle(armorStandEulerAngle);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        as.spawn();

        final String orangeRgbString = getSettings().node("Shape", "Particle", "Orange", "Rgb").getString();
        final String whiteRgbString = getSettings().node("Shape", "Particle", "White", "Rgb").getString();

        final Color orangeColor = DCToolsBukkit.fromRGBString(orangeRgbString, Color.ORANGE);
        final Color whiteColor = DCToolsBukkit.fromRGBString(whiteRgbString, Color.WHITE);

        long period = getSettings().node("Scroll", "Period").getLong();

        api.getPlatform().getScheduler().run(api.getPlatform(), new Task(as, orangeColor, whiteColor), 0L, period);
    }

    private class Task implements Consumer<SchedulerTask> {

        private int tick;


        @NotNull
        private final World world;
        private final EquipmentSlot itemSlot;

        private final ArmorStandCreator as;
        private final CaseLocation location;
        private final Location bukkitLocation;

        private final float whiteSize;
        private final float orangeSize;
        private final Color orangeColor;
        private final Color whiteColor;

        private final double tailRadius;
        private double currentTail;

        private final int scrollTime;
        private final int scrollInterval;
        private final int endTime;
        private final double blockPerTick;
        private final float yaw;

        private final Particle particle = getParticle();

        private final Sound scrollSound = Sound.valueOf(getSettings().node("Scroll", "Sound").getString("ENTITY_ITEM_PICKUP"));
        private final float scrollVolume = getSettings().node("Scroll", "Volume").getFloat(10);
        private final float scrollPitch = getSettings().node("Scroll", "Pitch").getFloat(1);

        public Task(final ArmorStandCreator as, final Color orangeColor, final Color whiteColor) {
            this.as = as;
            this.location = as.getLocation();
            this.bukkitLocation = BukkitUtils.toBukkit(location);
            this.whiteSize = getSettings().node("Particle", "White", "Size").getFloat(1);
            this.orangeSize = getSettings().node("Particle", "Orange", "Size").getFloat(1);
            this.orangeColor = orangeColor;
            this.whiteColor = whiteColor;

            double height = getSettings().node("Scroll", "Height").getDouble(1.5);

            this.tailRadius = getSettings().node("Tail", "Radius").getDouble(0.5);
            this.currentTail = tailRadius;

            this.scrollTime = getSettings().node("Scroll", "Time").getInt(40);
            this.scrollInterval = getSettings().node("Scroll", "Interval").getInt(1);
            this.endTime = getSettings().node("End", "Time").getInt(40);
            this.blockPerTick = height / scrollTime;
            this.yaw = getSettings().node("Scroll", "Yaw").getFloat(20.0F);

            this.itemSlot = EquipmentSlot.valueOf(getSettings().node("ItemSlot").getString("HEAD")
                    .toUpperCase());
            world = getPlayer().getWorld();
        }

        @Override
        public void accept(SchedulerTask task) {

            if (tick <= scrollTime) {
                location.yaw(location.yaw() + yaw);
                bukkitLocation.setYaw(bukkitLocation.getYaw() + yaw);
                location.add(0.0, blockPerTick, 0.0);
                bukkitLocation.add(0.0, blockPerTick, 0.0);

                Particle.DustOptions dustOptions = new Particle.DustOptions(orangeColor, orangeSize);
                world.spawnParticle(particle, bukkitLocation.clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0, dustOptions);
                as.teleport(location);
            }


            if (tick <= scrollTime) {
                handleTail();

                if (tick % scrollInterval == 0) {
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
                world.playSound(bukkitLocation, scrollSound, scrollVolume, scrollPitch);

            }

            if (tick == scrollTime + 1) {
                as.setEquipment(itemSlot, getWinItem().material().itemStack());
                as.setCustomName(getWinItem().material().displayName());
                as.updateMeta();
                launchFirework(bukkitLocation.add(0, 0.5, 0));
                preEnd();
            }

            if (tick >= scrollTime + endTime) {
                as.remove();
                task.cancel();
                end();
            }

            ++tick;
        }

        private void handleTail() {
            currentTail -= tailRadius / scrollTime;
            Location loc = bukkitLocation.clone().add(0.0, blockPerTick, 0.0);

            double c = 2 * Math.PI * 0.5;
            double angle = c / 10;

            for (double t = 0.0; t <= c; t += angle) {
                double x = currentTail * Math.cos(t);
                double z = currentTail * Math.sin(t);
                loc.add(x, 0.4, z);
                Particle.DustOptions dustOptions = new Particle.DustOptions(whiteColor, whiteSize);

                world.spawnParticle(
                        particle,
                        loc,
                        1, 0.1, 0.1, 0.1, 0.0, dustOptions
                );
                loc.subtract(x, 0.4, z);
            }

        }

        private Particle getParticle() {
            try {
                return Particle.valueOf("REDSTONE");
            } catch (IllegalArgumentException e) {
                return Particle.valueOf("DUST");
            }
        }

        public void launchFirework(Location location) {
            Random r = new Random();
            World world = location.getWorld();
            if (world == null) return;

            Firework firework = world.spawn(location.subtract(new Vector(0.0, 0.5, 0.0)), Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            Color[] color = new Color[]{Color.RED, Color.AQUA, Color.GREEN, Color.ORANGE, Color.LIME, Color.BLUE, Color.MAROON, Color.WHITE};
            meta.addEffect(FireworkEffect.builder().flicker(false).with(FireworkEffect.Type.BALL).trail(false).withColor(color[r.nextInt(color.length)], color[r.nextInt(color.length)], color[r.nextInt(color.length)]).build());
            firework.setFireworkMeta(meta);
            firework.setMetadata("case", new FixedMetadataValue(((BukkitBackend) DCAPI.getInstance().getPlatform()).getPlugin(), "case"));
            firework.detonate();
        }

    }
}