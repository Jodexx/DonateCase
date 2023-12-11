package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.jodexindustries.donatecase.dc.Main.customConfig;
import static com.jodexindustries.donatecase.dc.Main.t;

public class WheelAnimation implements Animation {

    List<ItemStack> items = new ArrayList<>();
    List<String> groups = new ArrayList<>();
    List<ArmorStand> armorStands = new ArrayList<>();
    Player p;
    String c;

    @Override
    public String getName() {
        return "DEFAULT WHEEL";
    }

    @Override
    public void start(Player player, Location location, String c) {
        p = player;
        this.c = c;
        final Location loc = location.clone();
        float pitch = loc.getPitch();
        pitch = (float) (Math.round(pitch / 45.0) * 45.0);
        if (Math.abs(pitch) < 10) {
            pitch = 90;
        }
        pitch = pitch > 180 ? pitch - 360 : pitch;
        loc.setPitch(pitch);
        loc.setZ(loc.getZ() + 0.5);
        // register items
        int itemsCount = customConfig.getAnimations().getInt("Wheel.ItemsCount");
        for (int i = 0; i < itemsCount; i++) {
            String winGroup = Tools.getRandomGroup(c);
            ItemStack winItem = t.getWinItem(c, winGroup, player);
            items.add(winItem);
            groups.add(winGroup);
            armorStands.add(spawnArmorStand(location, i));
        }
        (new BukkitRunnable() {
            int ticks = 0;
            double lastCompletedRotation = 0.0;

            final double speed = customConfig.getAnimations().getDouble("Wheel.CircleSpeed");
            final double radius = customConfig.getAnimations().getDouble("Wheel.CircleRadius");
            final double rotationThreshold = Math.PI / (itemsCount * speed);

            final double offset = 2 * Math.PI / itemsCount;
            final Location location = loc.clone().add(loc.getDirection().getX() + 0.5, -1 + customConfig.getAnimations().getDouble("Wheel.LiftingAlongY"), 0);
            public void run() {
                ticks++;
                double angle = ticks / 20.0;
                angle *= speed;
                angle *= 2 * Math.PI;

                if (ticks < 101) {
                    double baseAngle = loc.getDirection().angle(new Vector(0, 0, 1));
                    for (ArmorStand entity : armorStands) {
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
                    for(ArmorStand stand : armorStands) {
                        Case.listAR.remove(stand);
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
    private ArmorStand spawnArmorStand(Location location, int index) {
        ArmorStand as = (ArmorStand) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.ARMOR_STAND);
        as.setVisible(false);
        Case.listAR.add(as);
        as.setGravity(false);
        as.setSmall(true);
        as.setCustomNameVisible(true);
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
