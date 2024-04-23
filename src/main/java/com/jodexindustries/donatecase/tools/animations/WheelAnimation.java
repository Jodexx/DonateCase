package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.data.Animation;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jodexindustries.donatecase.DonateCase.*;

public class WheelAnimation implements Animation {

    List<CaseData.Item> items = new ArrayList<>();
    List<ArmorStandCreator> armorStands = new ArrayList<>();
    private EquipmentSlot itemSlot;
    private ArmorStandEulerAngle armorStandEulerAngle;

    @Override
    public String getName() {
        return "DEFAULT WHEEL";
    }

    @Override
    public void start(Player player, Location location, UUID uuid, CaseData c, CaseData.Item winItem) {
        final Location loc = location.clone();
        float pitch = Math.round(location.getPitch() / 90.0f) * 90.0f;
        float yaw = Math.round(location.getYaw() / 90.0f) * 90.0f;
        loc.setPitch(pitch);
        loc.setYaw(yaw);
        loc.add(0.5, 0, 0.5);
        int itemsCount = customConfig.getAnimations().getInt("Wheel.ItemsCount");
        armorStandEulerAngle = Tools.getArmorStandEulerAngle("Wheel.Pose");
        itemSlot = EquipmentSlot.valueOf(customConfig.getAnimations().getString("Wheel.ItemSlot", "HEAD").toUpperCase());
        boolean small = customConfig.getAnimations().getBoolean("Wheel.SmallArmorStand", true);
        int animationTime = customConfig.getAnimations().getInt("Wheel.Scroll.Time", 100);
        final Location flocation = loc.clone().add(0 + customConfig.getAnimations().getDouble("Wheel.LiftingAlongX"),
                -1 + customConfig.getAnimations().getDouble("Wheel.LiftingAlongY"),
                0 + customConfig.getAnimations().getDouble("Wheel.LiftingAlongZ"));
        boolean needSound = customConfig.getAnimations().getString("Wheel.Scroll.Sound") != null;
        Sound sound = Sound.valueOf(customConfig.getAnimations().getString("Wheel.Scroll.Sound"));
        float volume = (float) customConfig.getAnimations().getDouble("Wheel.Scroll.Volume");
        float vpitch = (float) customConfig.getAnimations().getDouble("Wheel.Scroll.Pitch");
        final double speed = customConfig.getAnimations().getDouble("Wheel.CircleSpeed");
        final double radius = customConfig.getAnimations().getDouble("Wheel.CircleRadius");
        final boolean useFlame = customConfig.getAnimations().getBoolean("Wheel.UseFlame");
        // register items
        items.add(winItem);
        for (int i = 0; i < itemsCount; i++) {
            CaseData.Item tempWinItem = Case.getRandomItem(c);
            items.add(tempWinItem);
            armorStands.add(spawnArmorStand(location, i, small));
        }
        double baseAngle = loc.clone().getDirection().angle(new Vector(0, 0, 1));
        final double[] lastCompletedRotation = {0.0};
        final double rotationThreshold = Math.PI / (itemsCount * speed);
        AtomicInteger ticks = new AtomicInteger();
        final double[] yAx = {0};
        final double[] radiusAx = {radius};
        final double offset = 2 * Math.PI / itemsCount;
        final double[] speedAx = {speed};

        Bukkit.getScheduler().runTaskTimer(instance, (task) -> {
            ticks.getAndIncrement();
            double angle = ticks.get() / (20.0 * (animationTime / 100D) ) * speedAx[0] * 2 * Math.PI;

            if (ticks.get() < animationTime + 1) {
                // flame
                if (useFlame) {
                    yAx[0] += (radius + 4.0) / animationTime * speedAx[0];
                    radiusAx[0] -= 0.015 / (animationTime / 100.0);
                    double theta = ticks.get() / (20.0 / (speedAx[0] * 6));
                    double dx = (radiusAx[0] / 1.1) * Math.sin(theta);
                    double dy = (radiusAx[0] / 1.1) * Math.cos(theta);
                    Location particleLocation = flocation.clone().add(dx, yAx[0], dy);
                    particleLocation.getWorld().spawnParticle(Particle.FLAME, particleLocation, 1, 0, 0, 0, 0, null);
                    double theta2 = theta + Math.PI;
                    double dx2 = (radiusAx[0] / 1.1) * Math.sin(theta2);
                    double dy2 = (radiusAx[0] / 1.1) * Math.cos(theta2);
                    Location particleLocation2 = flocation.clone().add(dx2, yAx[0], dy2);
                    particleLocation2.getWorld().spawnParticle(Particle.FLAME, particleLocation2, 1, 0, 0, 0, 0, null);
                }
                // armor stands
                for (ArmorStandCreator entity : armorStands) {
                    double x = radius * Math.sin(angle);
                    double y = radius * Math.cos(angle);

                    Vector rotationAxis = loc.getDirection().crossProduct(new Vector(0, 1, 0)).normalize();
                    Location newLoc = flocation.clone().add(rotationAxis.multiply(x).add(loc.getDirection().multiply(y)));
                    entity.teleport(newLoc);
                    angle += offset;

                    double currentAngle = angle - baseAngle;
                    if (currentAngle - lastCompletedRotation[0] >= rotationThreshold) {
                        if (needSound) {
                            flocation.getWorld().playSound(flocation,
                                    sound,
                                    volume,
                                    vpitch);
                        }
                        lastCompletedRotation[0] = currentAngle;
                    }
                }
            }
            if (ticks.get() == animationTime + 1) {
                Case.onCaseOpenFinish(c, player, true, winItem);
            }
            // End
            if (ticks.get() >= animationTime + 20) {
                task.cancel();
                for (ArmorStandCreator stand : armorStands) {
                    stand.remove();
                }
                Case.animationEnd(c, getName(), player, uuid, winItem);
                items.clear();
                armorStands.clear();
            }
            if (ticks.get() < animationTime + 1) {
                speedAx[0] *= 1 - (speed / (animationTime - 2) );
            }
            }, 0L, 0L);
    }
    private ArmorStandCreator spawnArmorStand(Location location, int index, boolean small) {
        CaseData.Item item = items.get(index);
        ArmorStandCreator as = Tools.createArmorStand();
        as.spawnArmorStand(location);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        if(item.getMaterial().getItemStack().getType() != Material.AIR) {
            as.setEquipment(itemSlot, items.get(index).getMaterial().getItemStack());
        }
        as.setPose(armorStandEulerAngle);
        as.setCustomName(item.getMaterial().getDisplayName());
        return as;
    }
}
