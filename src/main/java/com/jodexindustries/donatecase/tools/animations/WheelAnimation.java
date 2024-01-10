package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jodexindustries.donatecase.dc.Main.*;

public class WheelAnimation implements Animation {

    List<ItemStack> items = new ArrayList<>();
    List<String> groups = new ArrayList<>();
    List<ArmorStandCreator> armorStands = new ArrayList<>();
    Player p;
    String c;

    @Override
    public String getName() {
        return "DEFAULT WHEEL";
    }

    @Override
    public void start(Player player, Location location, String c, String winGroup) {
        p = player;
        this.c = c;
        final Location loc = location.clone();
        float pitch = Math.round(location.getPitch() / 90.0f) * 90.0f;
        float yaw = Math.round(location.getYaw() / 90.0f) * 90.0f;
        loc.setPitch(pitch);
        loc.setYaw(yaw);
        loc.add(0.5, 0, 0.5);
        // register items
        int itemsCount = customConfig.getAnimations().getInt("Wheel.ItemsCount");
        groups.add(winGroup);
        for (int i = 0; i < itemsCount; i++) {
            String tempWinGroup = Tools.getRandomGroup(c);
            ItemStack winItem = t.getWinItem(c, tempWinGroup, player);
            items.add(winItem);
            groups.add(tempWinGroup);
            armorStands.add(spawnArmorStand(location, i));
        }
        double baseAngle = loc.clone().getDirection().angle(new Vector(0, 0, 1));
        final double[] lastCompletedRotation = {0.0};
        final double speed = customConfig.getAnimations().getDouble("Wheel.CircleSpeed");
        final double radius = customConfig.getAnimations().getDouble("Wheel.CircleRadius");
        final boolean useFlame = customConfig.getAnimations().getBoolean("Wheel.UseFlame");
        final double rotationThreshold = Math.PI / (itemsCount * speed);
        AtomicInteger ticks = new AtomicInteger();
        boolean needSound = customConfig.getAnimations().getString("Wheel.Scroll.Sound") != null;
        Sound sound = Sound.valueOf(customConfig.getAnimations().getString("Wheel.Scroll.Sound"));
        float volume = (float) customConfig.getAnimations().getDouble("Wheel.Scroll.Volume");
        float vpitch = (float) customConfig.getAnimations().getDouble("Wheel.Scroll.Pitch");
        final double[] yAx = {0};
        final double[] radiusAx = {radius};
        final double offset = 2 * Math.PI / itemsCount;
        final double[] speedAx = {speed};
        int animationTime = customConfig.getAnimations().getInt("Wheel.Scroll.Time", 100);
        final Location flocation = loc.clone().add(0, -1 + customConfig.getAnimations().getDouble("FullWheel.LiftingAlongY"), 0);

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
                Case.onCaseOpenFinish(c, player, true, groups.get(0));
            }
            // End
            if (ticks.get() >= animationTime + 20) {
                task.cancel();
                for (ArmorStandCreator stand : armorStands) {
                    stand.remove();
                }
                Case.animationEnd(c, getName(), player, loc, groups.get(0));
                items.clear();
                groups.clear();
                armorStands.clear();
            }
            if (ticks.get() < animationTime + 1) {
                speedAx[0] *= 1 - (speed / (animationTime - 2) );
            }
            }, 0L, 0L);
    }
    private ArmorStandCreator spawnArmorStand(Location location, int index) {
        ArmorStandCreator as = t.createArmorStand();
        as.spawnArmorStand(location);
        as.setSmall(true);
        as.setVisible(false);
        as.setGravity(false);
        if(items.get(index).getType() != Material.AIR) {
                as.setHelmet(items.get(index));
        }
        String winGroupDisplayName = Case.getWinGroupDisplayName(c, groups.get(index));
        if(instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            winGroupDisplayName = PAPISupport.setPlaceholders(p, winGroupDisplayName);
        }
        as.setCustomName(t.rc(winGroupDisplayName));
        return as;
    }
}
