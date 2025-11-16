package com.jodexindustries.donatecase.spigot.animations.rainly;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RainlyAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    private RainlySettings settings;

    @Override
    public void start() {
        try {
            this.settings = getSettings().get(RainlySettings.class);
        } catch (SerializationException e) {
            throw new RuntimeException("Error with parsing animation settings", e);
        }

        ArmorStandCreator as = DCAPI.getInstance().getPlatform().getTools().createArmorStand(getUuid(), getLocation().clone().add(0.5, 1, 0.5));
        if (settings.armorStandEulerAngle != null) as.setAngle(settings.armorStandEulerAngle);
        as.setCustomNameVisible(true);
        as.setVisible(false);
        as.setGravity(false);

        as.setSmall(settings.isSmall);
        as.spawn();

        api.getPlatform().getScheduler().run(api.getPlatform(), new Task(as), 0L, 2L);
    }

    private class Task implements Consumer<SchedulerTask> {

        private int i = 0;
        private final List<Location> clouds = new ArrayList<>();
        private final Location bukkitLocation;
        private final CaseLocation location;
        private final ArmorStandCreator as;
        private final World world;

        private final Sound endSound = settings.end.sound();
        private final Sound scrollSound = settings.scroll.sound();

        public Task(ArmorStandCreator as) {
            this.as = as;
            this.location = as.getLocation();
            this.bukkitLocation = BukkitUtils.toBukkit(location);

            clouds.add(bukkitLocation.clone().add(-2, 3, 2));
            clouds.add(bukkitLocation.clone().add(-2, 3, -2));
            clouds.add(bukkitLocation.clone().add(2, 3, 2));
            clouds.add(bukkitLocation.clone().add(2, 3, -2));
            world = bukkitLocation.getWorld() != null ? bukkitLocation.getWorld() : getPlayer().getWorld();
        }

        @Override
        public void accept(SchedulerTask task) {
            for (Location cloud : clouds) {
                world.spawnParticle(settings.fallingParticle, cloud, 1);
                world.spawnParticle(settings.cloudParticle, cloud.clone().add(0, 0.5, 0), 0);
            }

            if (i == 32) {
                handleWinningItem();
            }

            if (i < 32) {
                location.yaw(location.yaw() + 20.0F);
                as.teleport(location);

                if (i % 2 == 0) {
                    updateRandomItem();
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
            as.setEquipment(settings.itemSlot, getItem().material().itemStack());

            String winGroupDisplayName = DCAPI.getInstance().getPlatform().getPAPI().setPlaceholders(
                    getPlayer(), getItem().material().displayName()
            );
            getItem().material().displayName(winGroupDisplayName);

            as.setCustomName(winGroupDisplayName);
            as.updateMeta();

            preEnd();

            if (endSound != null)
                world.playSound(bukkitLocation, endSound, settings.end.volume, settings.end.pitch);

            if (settings.end.particle != null)
                world.spawnParticle(settings.end.particle, bukkitLocation, 0);
        }

        private void updateRandomItem() {
            CaseItem item = getDefinition().items().getRandomItem();
            String itemDisplayName = DCAPI.getInstance().getPlatform().getPAPI().setPlaceholders(
                    getPlayer(), item.material().displayName()
            );
            item.material().displayName(itemDisplayName);

            as.setEquipment(settings.itemSlot, item.material().itemStack());

            if (item.material().displayName() != null && !item.material().displayName().isEmpty()) {
                as.setCustomName(item.material().displayName());
            }

            as.updateMeta();

            if (scrollSound != null)
                world.playSound(bukkitLocation, scrollSound, settings.scroll.volume, settings.scroll.pitch);

            if (settings.scroll.particle != null)
                world.spawnParticle(settings.scroll.particle, bukkitLocation.clone().add(0.0, 0.4, 0.0), 9, 0.1, 0.1, 0.1, 0.0);
        }
    }
}
