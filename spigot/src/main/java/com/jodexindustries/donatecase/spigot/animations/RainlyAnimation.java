package com.jodexindustries.donatecase.spigot.animations;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RainlyAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    private EquipmentSlot itemSlot;

    @SneakyThrows
    @Override
    public void start() {
        Particle particle = Particle.valueOf(getSettings().node("FallingParticle").getString());

        ArmorStandCreator as = DCAPI.getInstance().getPlatform().getTools().createArmorStand(getUuid(), getLocation().clone().add(0.5, 1, 0.5));
        ArmorStandEulerAngle armorStandEulerAngle = getSettings().node("Pose").get(ArmorStandEulerAngle.class);
        if(armorStandEulerAngle != null) as.setAngle(armorStandEulerAngle);
        as.setCustomNameVisible(true);
        as.setVisible(false);
        as.setGravity(false);

        itemSlot = EquipmentSlot.valueOf(
                getSettings().node("ItemSlot").getString("HEAD").toUpperCase()
        );

        boolean small = getSettings().node("SmallArmorStand").getBoolean(true);
        as.setSmall(small);
        as.spawn();

        api.getPlatform().getScheduler().run(api.getPlatform(), new Task(as, particle), 0L, 2L);
    }

    private class Task implements Consumer<SchedulerTask> {

        private int i = 0;
        private final List<Location> clouds = new ArrayList<>();
        private final Location bukkitLocation;
        private final CaseLocation location;
        private final Particle particle;
        private final ArmorStandCreator as;
        private final World world;

        private final Sound endSound = Sound.valueOf(getSettings().node("End", "Sound").getString("ENTITY_GENERIC_EXPLODE"));
        private final float endVolume = getSettings().node("End", "Volume").getFloat();
        private final float endPitch = getSettings().node("End", "Pitch").getFloat();

        private final Sound scrollSound = Sound.valueOf(getSettings().node("Scroll", "Sound").getString("ENTITY_EXPERIENCE_ORB_PICKUP"));
        private final float scrollVolume = getSettings().node("Scroll", "Volume").getFloat();
        private final float scrollPitch = getSettings().node("Scroll", "Pitch").getFloat();


        public Task(ArmorStandCreator as, Particle particle) {
            this.as = as;
            this.location = as.getLocation();
            this.bukkitLocation = BukkitUtils.toBukkit(location);
            this.particle = particle;

            clouds.add(bukkitLocation.clone().add(-2, 3,2 ));
            clouds.add(bukkitLocation.clone().add(-2, 3, -2));
            clouds.add(bukkitLocation.clone().add(2, 3, 2));
            clouds.add(bukkitLocation.clone().add(2, 3, -2));
            world = bukkitLocation.getWorld() != null ? bukkitLocation.getWorld() : getPlayer().getWorld();
        }

        @Override
        public void accept(SchedulerTask task) {
            for (Location cloud : clouds) {
                world.spawnParticle(particle, cloud, 1);
                world.spawnParticle(Particle.CLOUD, cloud.clone().add(0, 0.5, 0), 0);
            }


            if (i == 32) {
                handleWinningItem();
            }

            if (i < 32) {
                location.yaw(location.yaw() + 20.0F);
                as.teleport(location);

                if(i % 2 == 0) {
                    updateRandomItem();
                    world.spawnParticle(getParticle("fireworks"), bukkitLocation.clone().add(0.0, 0.4, 0.0), 9, 0.1, 0.1, 0.1, 0.0);
                }
            }

            if (i >= 70) {
                as.remove();
                task.cancel();
                end();
            }

            i++;
        }

        private void handleWinningItem() {
            as.setEquipment(itemSlot, getItem().material().itemStack());

            String winGroupDisplayName = DCAPI.getInstance().getPlatform().getPAPI().setPlaceholders(
                    getPlayer(), getItem().material().displayName()
            );
            getItem().material().displayName(winGroupDisplayName);

            as.setCustomName(winGroupDisplayName);
            as.updateMeta();

            preEnd();

            world.spawnParticle(getParticle("explosion"), bukkitLocation, 0);
            world.playSound(bukkitLocation, endSound, endVolume, endPitch);
        }

        private void updateRandomItem() {
            CaseItem item = getDefinition().items().getRandomItem();
            String itemDisplayName = DCAPI.getInstance().getPlatform().getPAPI().setPlaceholders(
                    getPlayer(), item.material().displayName()
            );
            item.material().displayName(itemDisplayName);

            as.setEquipment(itemSlot, item.material().itemStack());

            if (item.material().displayName() != null && !item.material().displayName().isEmpty()) {
                as.setCustomName(item.material().displayName());
            }

            as.updateMeta();
            world.playSound(bukkitLocation, scrollSound, scrollVolume, scrollPitch);
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
