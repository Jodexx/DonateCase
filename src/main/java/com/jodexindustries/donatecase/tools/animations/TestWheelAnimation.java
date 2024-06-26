package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.data.Animation;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TestWheelAnimation implements Animation {

    List<String> items = new ArrayList<>();
    List<ArmorStandCreator> armorStands = new ArrayList<>();
    private EquipmentSlot itemSlot;
    private ArmorStandEulerAngle armorStandEulerAngle;


    @Override
    public void start(Player player, Location location, UUID uuid, CaseData caseData, CaseData.Item winItem) {
        final Location loc = location.clone();
        float pitch = Math.round(location.getPitch() / 90.0f) * 90.0f;
        float yaw = Math.round(location.getYaw() / 90.0f) * 90.0f;
        loc.setPitch(pitch);
        loc.setYaw(yaw);
        loc.add(0.5, 0, 0.5);

        // register items
        int itemsCount = caseData.getItems().size();
        int i = 1;
        String winGroupDisplayName = PAPISupport.setPlaceholders(player,winItem.getMaterial().getDisplayName());
        winItem.getMaterial().setDisplayName(winGroupDisplayName);



        armorStandEulerAngle = Tools.getArmorStandEulerAngle("FullWheel.Pose");

        boolean needSound = Case.getCustomConfig().getAnimations().getString("FullWheel.Scroll.Sound") != null;
        Sound sound = Sound.valueOf(Case.getCustomConfig().getAnimations().getString("FullWheel.Scroll.Sound"));
        float volume = (float) Case.getCustomConfig().getAnimations().getDouble("FullWheel.Scroll.Volume");
        float vpitch = (float) Case.getCustomConfig().getAnimations().getDouble("FullWheel.Scroll.Pitch");
        int animationTime = Case.getCustomConfig().getAnimations().getInt("FullWheel.Scroll.Time", 100);

        final double speed = Case.getCustomConfig().getAnimations().getDouble("FullWheel.CircleSpeed");
        final double radius = Case.getCustomConfig().getAnimations().getDouble("FullWheel.CircleRadius");
        final boolean useFlame = Case.getCustomConfig().getAnimations().getBoolean("FullWheel.Flame.Enabled");
        final Particle flameParticle = Particle.valueOf(Case.getCustomConfig().getAnimations().getString("FullWheel.Flame.Particle", "FLAME"));

        final Location flocation = loc.clone().add(0 + Case.getCustomConfig().getAnimations().getDouble("FullWheel.LiftingAlongX"),
                -1 + Case.getCustomConfig().getAnimations().getDouble("FullWheel.LiftingAlongY"),
                0 + Case.getCustomConfig().getAnimations().getDouble("FullWheel.LiftingAlongZ"));

        itemSlot = EquipmentSlot.valueOf(Case.getCustomConfig().getAnimations().getString("FullWheel.ItemSlot", "HEAD").toUpperCase());
        boolean small = Case.getCustomConfig().getAnimations().getBoolean("FullWheel.SmallArmorStand", true);
        // win group
        items.add(winItem.getItemName());
        armorStands.add(spawnArmorStand(caseData, location, 0, small));

        // another groups
        for (String itemName : caseData.getItems().keySet()) {
            if(itemName.equalsIgnoreCase(winItem.getItemName())) continue;
            CaseData.Item item = caseData.getItem(itemName);
            String displayName = item.getMaterial().getDisplayName();
            item.getMaterial().setDisplayName(PAPISupport.setPlaceholders(player, displayName));
            items.add(itemName);
            armorStands.add(spawnArmorStand(caseData, location, i, small));
            i++;
        }

        double baseAngle = loc.getDirection().angle(new Vector(0, 0, 1));
        final double[] lastCompletedRotation = {0.0};
        final double rotationThreshold = Math.PI / (itemsCount * speed);
        AtomicInteger ticks = new AtomicInteger();
        final double[] yAx = {0};
        final double[] radiusAx = {radius};
        final double offset = 2 * Math.PI / itemsCount;
        final double[] speedAx = {speed};

        Bukkit.getScheduler().runTaskTimer(Case.getInstance(), (task) -> {

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
                    particleLocation.getWorld().spawnParticle(flameParticle, particleLocation, 1, 0, 0, 0, 0, null);
                    double theta2 = theta + Math.PI;
                    double dx2 = (radiusAx[0] / 1.1) * Math.sin(theta2);
                    double dy2 = (radiusAx[0] / 1.1) * Math.cos(theta2);
                    Location particleLocation2 = flocation.clone().subtract(0, 1.5, 0).add(dx2, 1.5 + yAx[0], dy2);
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
                Case.animationPreEnd(caseData, player, true, winItem);
            }
            // End
            if (ticks.get() >= animationTime + 20) {
                task.cancel();
                for (ArmorStandCreator stand : armorStands) {
                    stand.remove();
                }
                Case.animationEnd(caseData, player, uuid, winItem);
                items.clear();
                armorStands.clear();
            }
            speedAx[0] *= 1 - speed / (animationTime - 2);
        }, 0L, 0L);
    }
    private ArmorStandCreator spawnArmorStand(CaseData c, Location location, int index, boolean small) {
        CaseData.Item item = c.getItem(items.get(index));
        ArmorStandCreator as = Tools.createArmorStand();
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
