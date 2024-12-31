package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.manager.AnimationManager;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class FireworkAnimation extends JavaAnimationBukkit {

    public static void register(AnimationManager<JavaAnimationBukkit, CaseDataMaterialBukkit, Player, Location, Block, CaseDataBukkit> manager) {
        CaseAnimation<JavaAnimationBukkit> caseAnimation = manager.builder("FIREWORK")
                .animation(FireworkAnimation.class)
                .description("Fireworks fly to the skies and a prize appears")
                .requireSettings(true)
                .build();

        manager.registerAnimation(caseAnimation);
    }

    @Override
    public void start() {
        double x = getSettings().getDouble("StartPosition.X", 0.5);
        double y = getSettings().getDouble("StartPosition.Y", 1);
        double z = getSettings().getDouble("StartPosition.Z", 0.5);

        getLocation().add(x, y, z);

        final ArmorStandEulerAngle armorStandEulerAngle = DCToolsBukkit.getArmorStandEulerAngle(getSettings().getConfigurationSection("Pose"));
        final ArmorStandCreator as = getApi().getTools().createArmorStand(getLocation());

        boolean small = getSettings().getBoolean("SmallArmorStand", true);
        as.setAngle(armorStandEulerAngle);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        as.spawn();

        long period = getSettings().getLong("Scroll.Period");

        Bukkit.getScheduler().runTaskTimer(getApi().getDonateCase(), new Task(as), 0L, period);
    }

    private class Task implements Consumer<BukkitTask> {

        private int tick;

        private final Location location;
        private final ArmorStandCreator as;
        private final World world;

        private final EquipmentSlot itemSlot;
        private final float yaw;

        public Task(final ArmorStandCreator as) {
            this.as = as;
            this.location = as.getLocation();

            this.itemSlot = EquipmentSlot.valueOf(getSettings().getString("ItemSlot", "HEAD").toUpperCase());
            this.yaw = (float) getSettings().getDouble("Scroll.Yaw", 20.0F);

            world = location.getWorld() != null ? location.getWorld() : getPlayer().getWorld();
        }

        @Override
        public void accept(BukkitTask task) {
            if (tick == 0) {
                Firework firework = world.spawn(location, Firework.class);
                FireworkMeta data = firework.getFireworkMeta();
                data.addEffects(FireworkEffect.builder().withColor(Color.PURPLE).withColor(Color.RED).with(FireworkEffect.Type.BALL).withFlicker().build());
                for (String color : getSettings().getStringList("FireworkColors")) {
                    data.addEffect(FireworkEffect.builder().withColor(DCToolsBukkit.parseColor(color)).build());
                }
                data.setPower(getSettings().getInt("Power"));
                firework.setFireworkMeta(data);
            }

            if (tick == 10) {
                if (getWinItem().getMaterial().getItemStack().getType() != Material.AIR) {
                    as.setEquipment(itemSlot, getWinItem().getMaterial().getItemStack());
                }
                if (getWinItem().getMaterial().getDisplayName() != null && !getWinItem().getMaterial().getDisplayName().isEmpty())
                    as.setCustomNameVisible(true);
                as.setCustomName(getWinItem().getMaterial().getDisplayName());
                as.updateMeta();
                preEnd();
            }

            if(tick >= 10 && tick <= 30) {
                location.setYaw(location.getYaw() + yaw);
                as.teleport(location);
            }

            if (tick >= 60) {
                task.cancel();
                as.remove();
                end();
            }

            tick++;
        }
    }
}