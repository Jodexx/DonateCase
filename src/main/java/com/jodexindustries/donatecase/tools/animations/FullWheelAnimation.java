package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.jodexindustries.donatecase.dc.Main.*;

public class FullWheelAnimation implements Animation {

    List<ItemStack> items = new ArrayList<>();
    List<String> groups = new ArrayList<>();
    List<ArmorStandCreator> armorStands = new ArrayList<>();
    Player p;
    String c;
    Location location;

    @Override
    public String getName() {
        return "DEFAULT FULLWHEEL";
    }

    @Override
    public void start(Player player, Location location, String c, String winGroup) {
        p = player;
        this.c = c;
        final Location loc = location.clone();
        this.location = loc;
        float pitch = loc.getPitch();
        pitch = (float) (Math.round(pitch / 45.0) * 45.0);
        if (Math.abs(pitch) < 10) {
            pitch = 90;
        }
        pitch = pitch > 180 ? pitch - 360 : pitch;
        loc.setPitch(pitch);
        loc.setZ(loc.getZ() + 0.5);
        // register items
        Set<String> configGroups = casesConfig.getCase(c).getConfigurationSection("case.Items").getKeys(false);
        int itemsCount = configGroups.size();
        configGroups.remove(winGroup);
        int i = 1;
        // win group
        addWinGroup(player, winGroup);
        // another groups
        for (String tempWinGroup : configGroups) {
            ItemStack winItem = t.getWinItem(c, tempWinGroup, player);
            items.add(winItem);
            groups.add(tempWinGroup);
            armorStands.add(spawnArmorStand(location, i));
            i++;
        }
        (new BukkitRunnable() {
            int ticks = 0;
            double lastCompletedRotation = 0.0;

            final double speed = customConfig.getAnimations().getDouble("FullWheel.CircleSpeed");
            final double radius = customConfig.getAnimations().getDouble("FullWheel.CircleRadius");
            final double rotationThreshold = Math.PI / (itemsCount * speed);

            final double offset = 2 * Math.PI / itemsCount;
            final Location location = loc.clone().add(loc.getDirection().getX() + 0.5, -1 + customConfig.getAnimations().getDouble("FullWheel.LiftingAlongY"), 0);
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
                            if (customConfig.getAnimations().getString("FullWheel.Scroll.Sound") != null) {
                                location.getWorld().playSound(location,
                                        Sound.valueOf(customConfig.getAnimations().getString("FullWheel.Scroll.Sound")),
                                        customConfig.getAnimations().getInt("FullWheel.Scroll.Volume"),
                                        customConfig.getAnimations().getInt("FullWheel.Scroll.Pitch"));
                            }
                            lastCompletedRotation = currentAngle;
                        }
                    }
                }
                if (ticks == 101) {
                    Case.onCaseOpenFinish(c, player, true, groups.get(0));
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
    private void addWinGroup(Player player, String finalWinGroup) {
        ItemStack winItem = t.getWinItem(c, finalWinGroup, player);
        items.add(winItem);
        groups.add(finalWinGroup);
        armorStands.add(spawnArmorStand(location, 0));
    }
}
