package com.jodexindustries.donatecase.spigot.animations;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.spigot.api.animation.BukkitJavaAnimation;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import com.jodexindustries.donatecase.spigot.tools.DCToolsBukkit;
import lombok.SneakyThrows;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.function.Consumer;

public class FireworkAnimation extends BukkitJavaAnimation {

    private final static DCAPI api = DCAPI.getInstance();

    @SneakyThrows
    @Override
    public void start() {
        double x = getSettings().node("StartPosition", "X").getDouble(0.5);
        double y = getSettings().node("StartPosition", "Y").getDouble(1);
        double z = getSettings().node("StartPosition", "Z").getDouble(0.5);

        getLocation().add(x, y, z);

        final ArmorStandCreator as = DCAPI.getInstance().getPlatform().getTools().createArmorStand(getLocation());

        boolean small = getSettings().node("SmallArmorStand").getBoolean(true);

        final ArmorStandEulerAngle angle = getSettings().node("Pose").get(ArmorStandEulerAngle.class);
        if (angle != null) as.setAngle(angle);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        as.spawn();

        long period = getSettings().node("Scroll", "Period").getLong();

        api.getPlatform().getScheduler().run(api.getPlatform(), new Task(as), 0L, period);
    }

    private class Task implements Consumer<SchedulerTask> {

        private int tick;

        private final CaseLocation location;
        private final ArmorStandCreator as;
        private final World world;

        private final EquipmentSlot itemSlot;
        private final float yaw;

        public Task(final ArmorStandCreator as) {
            this.as = as;
            this.location = as.getLocation();

            this.itemSlot = EquipmentSlot.valueOf(getSettings().node("ItemSlot").getString("HEAD").toUpperCase());
            this.yaw = getSettings().node("Scroll", "Yaw").getFloat(20.0F);

            world = getPlayer().getWorld();
        }

        @SneakyThrows
        @Override
        public void accept(SchedulerTask task) {
            if (tick == 0) {
                Firework firework = world.spawn(BukkitUtils.toBukkit(location), Firework.class);
                FireworkMeta data = firework.getFireworkMeta();
                data.addEffects(FireworkEffect.builder().withColor(Color.PURPLE).withColor(Color.RED).with(FireworkEffect.Type.BALL).withFlicker().build());
                for (String color : getSettings().node("FireworkColors").getList(String.class, new ArrayList<>())) {
                    data.addEffect(FireworkEffect.builder().withColor(DCToolsBukkit.parseColor(color)).build());
                }
                data.setPower(getSettings().node("Power").getInt());
                firework.setFireworkMeta(data);
            }

            if (tick == 10) {
                as.setEquipment(itemSlot, getWinItem().material().itemStack());
                if (getWinItem().material().displayName() != null && !getWinItem().material().displayName().isEmpty())
                    as.setCustomNameVisible(true);
                as.setCustomName(DCAPI.getInstance().getPlatform().getPAPI().setPlaceholders(getPlayer(), getWinItem().material().displayName()));
                as.updateMeta();
                preEnd();
            }

            if (tick >= 10 && tick < 60) {
                location.yaw(location.yaw() + yaw);
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