package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import com.jodexindustries.donatecase.impl.managers.AnimationManagerImpl;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RainlyAnimation extends JavaAnimationBukkit {

    private EquipmentSlot itemSlot;
    private ArmorStandEulerAngle armorStandEulerAngle;

    public static void register(AnimationManagerImpl manager) {
        CaseAnimation<JavaAnimationBukkit, CaseDataMaterialBukkit> caseAnimation = manager.builder("RAINLY")
                .animation(RainlyAnimation.class)
                .description("Rain drips from the clouds")
                .requireSettings(true)
                .build();

        manager.registerAnimation(caseAnimation);
    }

    @Override
    public void start() {
        Particle particle = Particle.valueOf(getSettings().getString("FallingParticle"));

        ArmorStandCreator as = Tools.createArmorStand(getLocation().clone().add(0.5, 1, 0.5));
        as.setVisible(false);
        as.setGravity(false);

        armorStandEulerAngle = Tools.getArmorStandEulerAngle(getSettings().getConfigurationSection("Pose"));

        itemSlot = EquipmentSlot.valueOf(
                getSettings().getString("ItemSlot", "HEAD").toUpperCase()
        );

        boolean small = getSettings().getBoolean("SmallArmorStand", true);
        as.setSmall(small);
        as.spawn();

        Bukkit.getScheduler().runTaskTimer(Case.getInstance(), new Task(as, particle), 0L, 2L);
    }

    private class Task implements Consumer<BukkitTask> {

        private int i = 0;  // tick counter
        private double t = 0; // time variable for firework effect
        private final List<Location> rains = new ArrayList<>();
        private final Location loc;
        private final Particle particle;
        private final ArmorStandCreator as;
        private final World world;

        public Task(ArmorStandCreator as, Particle particle) {
            this.as = as;
            this.loc = as.getLocation();
            this.particle = particle;

            rains.add(loc.clone().add(-2, 3,2 ));
            rains.add(loc.clone().add(-2, 3, -2));
            rains.add(loc.clone().add(2, 3, 2));
            rains.add(loc.clone().add(2, 3, -2));
            world = loc.getWorld() != null ? loc.getWorld() : getPlayer().getWorld();
        }

        @Override
        public void accept(BukkitTask task) {
            // Spawn rain and cloud particles
            for (Location rain : rains) {
                world.spawnParticle(particle, rain, 1);
                world.spawnParticle(Particle.CLOUD, rain.clone().add(0, 0.5, 0), 0);
            }

            loc.setYaw(loc.getYaw() + 20.0F); // Rotate the armor stand
            as.teleport(loc);

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
                Case.animationEnd(getCaseDataBukkit(), getPlayer(), getUuid(), getWinItem());
            }

            i++; // Increment tick counter
        }

        private void handleWinningItem() {
            // Show the winning item and explosion effect
            if (getWinItem().getMaterial().getItemStack().getType() != Material.AIR) {
                as.setEquipment(itemSlot, getWinItem().getMaterial().getItemStack());
            }

            String winGroupDisplayName = Case.getInstance().papi.setPlaceholders(
                    getPlayer(), getWinItem().getMaterial().getDisplayName()
            );
            getWinItem().getMaterial().setDisplayName(winGroupDisplayName);

            as.setAngle(armorStandEulerAngle);
            as.setCustomNameVisible(true);
            as.setCustomName(winGroupDisplayName);
            as.updateMeta();

            Case.animationPreEnd(getCaseDataBukkit(), getPlayer(), getUuid(), getWinItem());

            world.spawnParticle(getParticle("explosion"), loc, 0);
            world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        }

        private void updateRandomItem() {
            CaseDataItem<CaseDataMaterialBukkit> item = getCaseData().getRandomItem();
            String itemDisplayName = Case.getInstance().papi.setPlaceholders(
                    getPlayer(), item.getMaterial().getDisplayName()
            );
            item.getMaterial().setDisplayName(itemDisplayName);

            if (item.getMaterial().getItemStack().getType() != Material.AIR) {
                as.setEquipment(itemSlot, item.getMaterial().getItemStack());
            }

            as.setAngle(armorStandEulerAngle);

            if (item.getMaterial().getDisplayName() != null && !item.getMaterial().getDisplayName().isEmpty()) {
                as.setCustomNameVisible(true);
                as.setCustomName(item.getMaterial().getDisplayName());
            }

            as.updateMeta();
            world.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 5.0F);
        }

        private void playFireworkParticles() {
            // Firework particle effect logic
            t += 0.25;
            Location particleLocation = loc.clone().add(0.0, 0.6, 0.0);

            for (double phi = 0.0; phi <= 9; ++phi) {
                double x = 0.09 * (9 - t * 2.5) * Math.cos(t + phi);
                double z = 0.09 * (9 - t * 2.5) * Math.sin(t + phi);
                particleLocation.add(x, 0.0, z);
                world.spawnParticle(getParticle("fireworks"), loc.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0);
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
