package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.BukkitBackend;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.tools.BukkitUtils;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RainlyAnimation extends BukkitJavaAnimation {

    private EquipmentSlot itemSlot;
    private ArmorStandEulerAngle armorStandEulerAngle;

    @SneakyThrows
    @Override
    public void start() {
        Particle particle = Particle.valueOf(getSettings().getString("FallingParticle"));

        ArmorStandCreator as = DCAPI.getInstance().getPlatform().getTools().createArmorStand(getLocation().clone().add(0.5, 1, 0.5));
        as.setVisible(false);
        as.setGravity(false);

        armorStandEulerAngle = getSettings().node("Pose").get(ArmorStandEulerAngle.class);

        itemSlot = EquipmentSlot.valueOf(
                getSettings().node("ItemSlot").getString("HEAD").toUpperCase()
        );

        boolean small = getSettings().node("SmallArmorStand").getBoolean(true);
        as.setSmall(small);
        as.spawn();

        Bukkit.getScheduler().runTaskTimer(((BukkitBackend) DCAPI.getInstance().getPlatform()).getPlugin(), new Task(as, particle), 0L, 2L);
    }

    private class Task implements Consumer<BukkitTask> {

        private int i = 0;  // tick counter
        private double t = 0; // time variable for firework effect
        private final List<Location> rains = new ArrayList<>();
        private final Location bukkitLocation;
        private final CaseLocation location;
        private final Particle particle;
        private final ArmorStandCreator as;
        private final World world;

        private final Sound endSound = Sound.valueOf(getSettings().node("End.Sound").getString("ENTITY_GENERIC_EXPLODE"));
        private final float endVolume = getSettings().node("End.Volume").getFloat();
        private final float endPitch = getSettings().node("End.Pitch").getFloat();

        private final Sound scrollSound = Sound.valueOf(getSettings().node("Scroll", "Sound").getString("ENTITY_EXPERIENCE_ORB_PICKUP"));
        private final float scrollVolume = getSettings().node("Scroll", "Volume").getFloat();
        private final float scrollPitch = getSettings().node("Scroll", "Pitch").getFloat();


        public Task(ArmorStandCreator as, Particle particle) {
            this.as = as;
            this.location = as.getLocation();
            this.bukkitLocation = BukkitUtils.toBukkit(location);
            this.particle = particle;

            rains.add(bukkitLocation.clone().add(-2, 3,2 ));
            rains.add(bukkitLocation.clone().add(-2, 3, -2));
            rains.add(bukkitLocation.clone().add(2, 3, 2));
            rains.add(bukkitLocation.clone().add(2, 3, -2));
            world = bukkitLocation.getWorld() != null ? bukkitLocation.getWorld() : getPlayer().getWorld();
        }

        @Override
        public void accept(BukkitTask task) {
            // Spawn rain and cloud particles
            for (Location rain : rains) {
                world.spawnParticle(particle, rain, 1);
                world.spawnParticle(Particle.CLOUD, rain.clone().add(0, 0.5, 0), 0);
            }

            location.setYaw(location.getYaw() + 20.0F); // Rotate the armor stand
            as.teleport(location);

            if (i == 32) {
                handleWinningItem();
            }

            // Change random item every 2 ticks before tick 30
            if (i <= 30 && (i % 2 == 0)) {
                updateRandomItem();
                playFireworkParticles();
            }

            // End the animation after 70 ticks
            if (i >= 70) {
                as.remove();
                task.cancel();
                end();
            }

            i++; // Increment tick counter
        }

        private void handleWinningItem() {
            as.setEquipment(itemSlot, getWinItem().getMaterial().getItemStack());

            String winGroupDisplayName = DCAPI.getInstance().getPlatform().getPAPI().setPlaceholders(
                    getPlayer(), getWinItem().getMaterial().getDisplayName()
            );
            getWinItem().getMaterial().setDisplayName(winGroupDisplayName);

            as.setAngle(armorStandEulerAngle);
            as.setCustomNameVisible(true);
            as.setCustomName(winGroupDisplayName);
            as.updateMeta();

            preEnd();

            world.spawnParticle(getParticle("explosion"), bukkitLocation, 0);
            world.playSound(bukkitLocation, endSound, endVolume, endPitch);
        }

        private void updateRandomItem() {
            CaseDataItem item = getCaseData().getRandomItem();
            String itemDisplayName = DCAPI.getInstance().getPlatform().getPAPI().setPlaceholders(
                    getPlayer(), item.getMaterial().getDisplayName()
            );
            item.getMaterial().setDisplayName(itemDisplayName);

            as.setEquipment(itemSlot, item.getMaterial().getItemStack());

            as.setAngle(armorStandEulerAngle);

            if (item.getMaterial().getDisplayName() != null && !item.getMaterial().getDisplayName().isEmpty()) {
                as.setCustomNameVisible(true);
                as.setCustomName(item.getMaterial().getDisplayName());
            }

            as.updateMeta();
            world.playSound(bukkitLocation, scrollSound, scrollVolume, scrollPitch);
        }

        private void playFireworkParticles() {
            // Firework particle effect logic
            t += 0.25;
            Location particleLocation = bukkitLocation.clone().add(0.0, 0.6, 0.0);

            for (double phi = 0.0; phi <= 9; ++phi) {
                double x = 0.09 * (9 - t * 2.5) * Math.cos(t + phi);
                double z = 0.09 * (9 - t * 2.5) * Math.sin(t + phi);
                particleLocation.add(x, 0.0, z);
                world.spawnParticle(getParticle("fireworks"), bukkitLocation.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0);
                particleLocation.subtract(x, 0.0, z);

                if (t >= 22) {
                    particleLocation.add(x, 0.0, z);
                    t = 0.0;
                }
            }
        }

        private Particle getParticle(String name) {
            if(name.equalsIgnoreCase("explosion")) {
                try {
                    return Particle.valueOf("EXPLOSION_HUGE");
                } catch (IllegalArgumentException e) {
                    return Particle.valueOf("EXPLOSION");
                }
            } else {
                try {
                    return Particle.valueOf("FIREWORKS_SPARK");
                } catch (IllegalArgumentException e) {
                    return Particle.valueOf("FIREWORK");
                }
            }
        }
    }
}
