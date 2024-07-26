package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.JavaAnimation;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class WheelAnimation extends JavaAnimation {

    final List<CaseData.Item> items = new ArrayList<>();
    final List<ArmorStandCreator> armorStands = new ArrayList<>();
    private EquipmentSlot itemSlot;
    private ArmorStandEulerAngle armorStandEulerAngle;

    /**
     * Constructor for initializing
     *
     * @param player   Player who opened case
     * @param location Case location
     * @param uuid     Active case uuid
     * @param caseData Case data
     * @param winItem  winItem
     */
    public WheelAnimation(Player player, Location location, UUID uuid, CaseData caseData, CaseData.Item winItem) {
        super(player, location, uuid, caseData, winItem);
    }

    @Override
    public void start() {
        final Location loc = getLocation().clone();
        float pitch = Math.round(getLocation().getPitch() / 90.0f) * 90.0f;
        float yaw = Math.round(getLocation().getYaw() / 90.0f) * 90.0f;
        loc.setPitch(pitch);
        loc.setYaw(yaw);
        loc.add(0.5, 0, 0.5);
        int itemsCount = Case.getConfig().getAnimations().getInt("Wheel.ItemsCount");
        armorStandEulerAngle = Tools.getArmorStandEulerAngle("Wheel.Pose");
        itemSlot = EquipmentSlot.valueOf(Case.getConfig().getAnimations().getString("Wheel.ItemSlot", "HEAD").toUpperCase());
        boolean small = Case.getConfig().getAnimations().getBoolean("Wheel.SmallArmorStand", true);
        int animationTime = Case.getConfig().getAnimations().getInt("Wheel.Scroll.Time", 100);
        final Location flocation = loc.clone().add(0 + Case.getConfig().getAnimations().getDouble("Wheel.LiftingAlongX"),
                -1 + Case.getConfig().getAnimations().getDouble("Wheel.LiftingAlongY"),
                0 + Case.getConfig().getAnimations().getDouble("Wheel.LiftingAlongZ"));
        boolean needSound = Case.getConfig().getAnimations().getString("Wheel.Scroll.Sound") != null;
        Sound sound = Sound.valueOf(Case.getConfig().getAnimations().getString("Wheel.Scroll.Sound"));
        float volume = (float) Case.getConfig().getAnimations().getDouble("Wheel.Scroll.Volume");
        float vpitch = (float) Case.getConfig().getAnimations().getDouble("Wheel.Scroll.Pitch");
        final double speed = Case.getConfig().getAnimations().getDouble("Wheel.CircleSpeed");
        final double radius = Case.getConfig().getAnimations().getDouble("Wheel.CircleRadius");
        final boolean useFlame = Case.getConfig().getAnimations().getBoolean("Wheel.Flame.Enabled");
        final Particle flameParticle = Particle.valueOf(Case.getConfig().getAnimations().getString("Wheel.Flame.Particle", "FLAME"));
        // register items
        items.add(getWinItem());
        for (int i = 0; i < itemsCount; i++) {
            CaseData.Item tempWinItem = getCaseData().getRandomItem();
            items.add(tempWinItem);
            armorStands.add(spawnArmorStand(getLocation(), i, small));
        }
        double baseAngle = loc.clone().getDirection().angle(new Vector(0, 0, 1));
        final double[] lastCompletedRotation = {0.0};
        final double rotationThreshold = Math.PI / (itemsCount * speed);
        AtomicInteger ticks = new AtomicInteger();
        final double[] yAx = {0};
        final double[] radiusAx = {radius};
        final double offset = 2 * Math.PI / itemsCount;
        final double[] speedAx = {speed};

        Bukkit.getScheduler().runTaskTimer(Case.getInstance(), (task) -> {
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
                    particleLocation.getWorld().spawnParticle(flameParticle, particleLocation, 1, 0, 0, 0, 0, null);
                    double theta2 = theta + Math.PI;
                    double dx2 = (radiusAx[0] / 1.1) * Math.sin(theta2);
                    double dy2 = (radiusAx[0] / 1.1) * Math.cos(theta2);
                    Location particleLocation2 = flocation.clone().add(dx2, yAx[0], dy2);
                    particleLocation2.getWorld().spawnParticle(flameParticle, particleLocation2, 1, 0, 0, 0, 0, null);
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
                Case.animationPreEnd(getCaseData(), getPlayer(), true, getWinItem());
            }
            // End
            if (ticks.get() >= animationTime + 20) {
                task.cancel();
                for (ArmorStandCreator stand : armorStands) {
                    stand.remove();
                }
                Case.animationEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());
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
        ArmorStandCreator as = Tools.createArmorStand(location);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        as.setAngle(armorStandEulerAngle);
        as.setCustomName(item.getMaterial().getDisplayName());
        as.setCustomNameVisible(true);
        as.spawn();
        if(item.getMaterial().getItemStack().getType() != Material.AIR) {
            as.setEquipment(itemSlot, items.get(index).getMaterial().getItemStack());
        }
        return as;
    }
}
