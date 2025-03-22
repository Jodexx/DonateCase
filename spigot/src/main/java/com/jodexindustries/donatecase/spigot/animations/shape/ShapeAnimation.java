package com.jodexindustries.donatecase.spigot.animations.shape;

import com.jodexindustries.donatecase.spigot.BukkitBackend;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import com.jodexindustries.donatecase.spigot.tools.DCToolsBukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Random;
import java.util.function.Consumer;

public class ShapeAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    private ShapeSettings settings;

    @Override
    public void start() {
        try {
            settings = getSettings().get(ShapeSettings.class);
        } catch (SerializationException e) {
            throw new RuntimeException("Error with parsing animation settings", e);
        }

        getLocation().add(settings.startPosition);

        final ArmorStandCreator as = api.getPlatform().getTools().createArmorStand(getUuid(), getLocation());

        if (settings.pose != null) as.setAngle(settings.pose);
        as.setSmall(settings.small);
        as.setVisible(false);
        as.setGravity(false);
        as.spawn();

        final Color orangeColor = DCToolsBukkit.fromRGBString(settings.particle.orange.rgb, Color.ORANGE);
        final Color whiteColor = DCToolsBukkit.fromRGBString(settings.particle.white.rgb, Color.WHITE);

        api.getPlatform().getScheduler().run(api.getPlatform(), new Task(as, orangeColor, whiteColor), 0L, settings.scroll.period);
    }

    private class Task implements Consumer<SchedulerTask> {

        private int tick;


        @NotNull
        private final World world;

        private final ArmorStandCreator as;
        private final CaseLocation location;
        private final Location bukkitLocation;

        private final Color orangeColor;
        private final Color whiteColor;

        private double currentTail;

        private final int endTime;
        private final double blockPerTick;
        private final Particle particle = getParticle();

        public Task(final ArmorStandCreator as, final Color orangeColor, final Color whiteColor) {
            this.as = as;
            this.location = as.getLocation();
            this.bukkitLocation = BukkitUtils.toBukkit(location);
            this.orangeColor = orangeColor;
            this.whiteColor = whiteColor;

            this.currentTail = settings.scroll.tail.radius;

            this.endTime = getSettings().node("End", "Time").getInt(40);
            this.blockPerTick = settings.scroll.height / settings.scroll.time;

            world = getPlayer().getWorld();
        }

        @Override
        public void accept(SchedulerTask task) {

            if (tick <= settings.scroll.time) {
                location.yaw(location.yaw() + settings.scroll.yaw);
                location.add(0.0, blockPerTick, 0.0);
                bukkitLocation.add(0.0, blockPerTick, 0.0);

                Particle.DustOptions dustOptions = new Particle.DustOptions(orangeColor, settings.particle.orange.size);
                world.spawnParticle(particle, bukkitLocation.clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0, dustOptions);
                as.teleport(location);
            }


            if (tick <= settings.scroll.time) {
                handleTail();

                if (tick % settings.scroll.interval == 0) {
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
                world.playSound(bukkitLocation, settings.scroll.sound(), settings.scroll.volume, settings.scroll.pitch);

            }

            if (tick == settings.scroll.time + 1) {
                as.setEquipment(settings.itemSlot, getWinItem().material().itemStack());
                as.setCustomName(getWinItem().material().displayName());
                as.updateMeta();
                if (settings.firework) launchFirework(bukkitLocation.add(0, 0.5, 0));
                preEnd();
            }

            if (tick >= settings.scroll.time + endTime) {
                as.remove();
                task.cancel();
                end();
            }

            ++tick;
        }

        private void handleTail() {
            currentTail -= settings.scroll.tail.radius / settings.scroll.time;
            Location loc = bukkitLocation.clone().add(0.0, blockPerTick, 0.0);

            double c = 2 * Math.PI * 0.5;
            double angle = c / 10;

            for (double t = 0.0; t <= c; t += angle) {
                double x = currentTail * Math.cos(t);
                double z = currentTail * Math.sin(t);
                loc.add(x, 0.4, z);
                Particle.DustOptions dustOptions = new Particle.DustOptions(whiteColor, settings.particle.white.size);

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
            firework.setMetadata("case", new FixedMetadataValue(((BukkitBackend) api.getPlatform()).getPlugin(), "case"));
            firework.detonate();
        }

    }
}