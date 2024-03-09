package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.data.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jodexindustries.donatecase.DonateCase.*;

public class FullWheelAnimation implements Animation {

    List<String> items = new ArrayList<>();
    List<ArmorStandCreator> armorStands = new ArrayList<>();
    private EquipmentSlot itemSlot;
    private ArmorStandEulerAngle armorStandEulerAngle;


    @Override
    public String getName() {
        return "DEFAULT FULLWHEEL";
    }

    @Override
    public void start(Player player, Location location, CaseData c, CaseData.Item winItem) {
        final Location loc = location.clone();
        float pitch = Math.round(location.getPitch() / 90.0f) * 90.0f;
        float yaw = Math.round(location.getYaw() / 90.0f) * 90.0f;
        loc.setPitch(pitch);
        loc.setYaw(yaw);
        loc.add(0.5, 0, 0.5);
        // register items
        int itemsCount = c.getItems().size();
        int i = 1;
        String winGroupDisplayName = PAPISupport.setPlaceholders(player,winItem.getMaterial().getDisplayName());
        winItem.getMaterial().setDisplayName(winGroupDisplayName);
        armorStandEulerAngle = t.getArmorStandEulerAngle("FullWheel.Pose");

        itemSlot = EquipmentSlot.valueOf(customConfig.getAnimations().getString("FullWheel.ItemSlot", "HEAD").toUpperCase());
        boolean small = customConfig.getAnimations().getBoolean("FullWheel.SmallArmorStand", true);
        // win group
        items.add(winItem.getItemName());
        armorStands.add(spawnArmorStand(c, location, 0, small));

        // another groups
        for (String itemName : c.getItems().keySet()) {
            if(itemName.equalsIgnoreCase(winItem.getItemName())) continue;
            CaseData.Item item = c.getItem(itemName);
            String displayName = item.getMaterial().getDisplayName();
            item.getMaterial().setDisplayName(PAPISupport.setPlaceholders(player, displayName));
            items.add(itemName);
            armorStands.add(spawnArmorStand(c, location, i, small));
            i++;
        }

        double baseAngle = loc.clone().getDirection().angle(new Vector(0, 0, 1));
        final double[] lastCompletedRotation = {0.0};
        final double speed = customConfig.getAnimations().getDouble("FullWheel.CircleSpeed");
        final double radius = customConfig.getAnimations().getDouble("FullWheel.CircleRadius");
        final boolean useFlame = customConfig.getAnimations().getBoolean("FullWheel.UseFlame");
        final double rotationThreshold = Math.PI / (itemsCount * speed);
        AtomicInteger ticks = new AtomicInteger();
        boolean needSound = customConfig.getAnimations().getString("FullWheel.Scroll.Sound") != null;
        Sound sound = Sound.valueOf(customConfig.getAnimations().getString("FullWheel.Scroll.Sound"));
        float volume = (float) customConfig.getAnimations().getDouble("FullWheel.Scroll.Volume");
        float vpitch = (float) customConfig.getAnimations().getDouble("FullWheel.Scroll.Pitch");
        int animationTime = customConfig.getAnimations().getInt("FullWheel.Scroll.Time", 100);
        final double[] yAx = {0};
        final double[] radiusAx = {radius};
        final double offset = 2 * Math.PI / itemsCount;
        final double[] speedAx = {speed};
        final Location flocation = loc.clone().add(0, -1 + customConfig.getAnimations().getDouble("FullWheel.LiftingAlongY"), 0);

        Bukkit.getScheduler().runTaskTimer(instance, (task) -> {

            ticks.getAndIncrement();
            double angle = ticks.get() / 20.0 * speedAx[0] * 2 * Math.PI;

            if (ticks.get() < animationTime) {
                // flame
                if (useFlame) {
                    yAx[0] += 6.0 / animationTime * speedAx[0];
                    radiusAx[0] -= 0.015 / (animationTime / 100.0);
                    double theta = ticks.get() / (20.0 / (speedAx[0] * 6));
                    double dx = (radiusAx[0] / 1.1) * Math.sin(theta);
                    double dy = (radiusAx[0] / 1.1) * Math.cos(theta);
                    Location particleLocation = flocation.clone().subtract(0, 1.5, 0).add(dx, 1.5 + yAx[0], dy);
                    particleLocation.getWorld().spawnParticle(Particle.FLAME, particleLocation, 1, 0, 0, 0, 0, null);
                    double theta2 = theta + Math.PI;
                    double dx2 = (radiusAx[0] / 1.1) * Math.sin(theta2);
                    double dy2 = (radiusAx[0] / 1.1) * Math.cos(theta2);
                    Location particleLocation2 = flocation.clone().subtract(0, 1.5, 0).add(dx2, 1.5 + yAx[0], dy2);
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
                Case.animationEnd(c, getName(), player, loc, winItem);
                items.clear();
                armorStands.clear();
            }
                speedAx[0] *= 1 - speed / (animationTime - 2);
        }, 0L, 0L);
    }
    private ArmorStandCreator spawnArmorStand(CaseData c, Location location, int index, boolean small) {
        CaseData.Item item = c.getItem(items.get(index));
        ArmorStandCreator as = t.createArmorStand();
        as.spawnArmorStand(location);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        if(item.getMaterial().getItemStack().getType() != Material.AIR) {
            as.setEquipment(itemSlot, item.getMaterial().getItemStack());
        }
        as.setPose(armorStandEulerAngle);
        as.setCustomName(item.getMaterial().getDisplayName());
        return as;
    }
}
