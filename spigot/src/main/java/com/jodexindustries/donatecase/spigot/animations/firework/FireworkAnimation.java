package com.jodexindustries.donatecase.spigot.animations.firework;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import com.jodexindustries.donatecase.spigot.tools.DCToolsBukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.function.Consumer;

public class FireworkAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    private FireworkSettings settings;

    @Override
    public void start() {
        try {
            settings = getSettings().get(FireworkSettings.class);
        } catch (SerializationException e) {
            throw new RuntimeException("Error with parsing animation settings", e);
        }

        getLocation().add(settings.startPosition);

        final ArmorStandCreator as = DCAPI.getInstance().getPlatform().getTools().createArmorStand(getUuid(), getLocation());

        if (settings.pose != null) as.setAngle(settings.pose);
        as.setSmall(settings.small);
        as.setVisible(false);
        as.setGravity(false);
        as.spawn();

        api.getPlatform().getScheduler().run(api.getPlatform(), new Task(as), 0L, settings.scroll.period);
    }

    private class Task implements Consumer<SchedulerTask> {

        private int tick;

        private final CaseLocation location;
        private final ArmorStandCreator as;
        private final World world;

        public Task(final ArmorStandCreator as) {
            this.as = as;
            this.location = as.getLocation();

            world = getPlayer().getWorld();
        }

        @Override
        public void accept(SchedulerTask task) {
            if (tick == 0) {
                Firework firework = world.spawn(BukkitUtils.toBukkit(location), Firework.class);
                FireworkMeta data = firework.getFireworkMeta();
                data.addEffects(FireworkEffect.builder().withColor(Color.PURPLE).withColor(Color.RED).with(FireworkEffect.Type.BALL).withFlicker().build());
                for (String color : settings.fireworkColors) {
                    data.addEffect(FireworkEffect.builder().withColor(DCToolsBukkit.parseColor(color)).build());
                }
                data.setPower(settings.power);
                firework.setFireworkMeta(data);
            }

            if (tick == 10) {
                as.setEquipment(settings.itemSlot, getItem().material().itemStack());
                if (getItem().material().displayName() != null && !getItem().material().displayName().isEmpty())
                    as.setCustomNameVisible(true);
                as.setCustomName(DCAPI.getInstance().getPlatform().getPAPI().setPlaceholders(getPlayer(), getItem().material().displayName()));
                as.updateMeta();
                preEnd();
            }

            if (tick >= 10 && tick < 60) {
                location.yaw(location.yaw() + settings.scroll.yaw);
                as.teleport(location);
            }

            if (tick >= 60) {
                as.remove();
                task.cancel();
                end();
            }

            tick++;
        }
    }
}