package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static com.jodexindustries.donatecase.dc.Main.customConfig;
import static com.jodexindustries.donatecase.dc.Main.t;

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
        (new BukkitRunnable() {
            int ticks = 0;
            double lastCompletedRotation = 0.0;

            final double speed = customConfig.getAnimations().getDouble("Wheel.CircleSpeed");
            final double radius = customConfig.getAnimations().getDouble("Wheel.CircleRadius");
            final double rotationThreshold = Math.PI / (itemsCount * speed);

            final double offset = 2 * Math.PI / itemsCount;
            final Location location = loc.clone().add(0, -1 + customConfig.getAnimations().getDouble("Wheel.LiftingAlongY"), 0);
            public void run() {
                ticks++;
                double angle = ticks / 20.0;
                angle *= speed;
                angle *= 2 * Math.PI;

                if (ticks < 101) {
                    double baseAngle = loc.getDirection().angle(new Vector(0, 0, 1));
                    for (ArmorStandCreator entity : armorStands) {
                        double x = radius * Math.sin(angle);
                        double y = radius * Math.cos(angle);

                        Vector rotationAxis = loc.getDirection().crossProduct(new Vector(0, 1, 0)).normalize();
                        Location newLoc = location.clone().add(rotationAxis.multiply(x).add(loc.getDirection().multiply(y)));
                        entity.teleport(newLoc);
                        angle += offset;

                        double currentAngle = angle - baseAngle;
                        if (currentAngle - lastCompletedRotation >= rotationThreshold) {
                            if (customConfig.getAnimations().getString("Wheel.Scroll.Sound") != null) {
                                location.getWorld().playSound(location,
                                        Sound.valueOf(customConfig.getAnimations().getString("Wheel.Scroll.Sound")),
                                        customConfig.getAnimations().getInt("Wheel.Scroll.Volume"),
                                        customConfig.getAnimations().getInt("Wheel.Scroll.Pitch"));
                            }
                            lastCompletedRotation = currentAngle;
                        }
                    }
                }
                if (ticks == 101) {
                    String winGroup = groups.get(0);
                    Case.onCaseOpenFinish(c, player, true, winGroup);
                }
                // End

                if (this.ticks >= 120) {
                    this.cancel();
                    for(ArmorStandCreator stand : armorStands) {
                        stand.remove();
                    }
                    Case.animationEnd(c, getName(), player, loc, groups.get(0));
                    items.clear();
                    groups.clear();
                    armorStands.clear();
                }
            }
        }).runTaskTimer(Main.instance, 0L, 2L);
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
        if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            winGroupDisplayName = PAPISupport.setPlaceholders(p, winGroupDisplayName);
        }
        as.setCustomName(t.rc(winGroupDisplayName));
        return as;
    }
}
