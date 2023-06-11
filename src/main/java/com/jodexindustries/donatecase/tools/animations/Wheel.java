package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.CustomConfig;
import com.jodexindustries.donatecase.tools.StartAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.jodexindustries.donatecase.dc.Main.customConfig;

public class Wheel {

    List<ItemStack> items = new ArrayList<>();
    List<String> groups = new ArrayList<>();
    List<ArmorStand> armorStands = new ArrayList<>();
     public Wheel(final Player player, Location location, final String c) {
        final Location lAC = location.clone();
        // make case active
        Main.ActiveCase.put(lAC, c);
        // close inventory
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (Main.openCase.containsKey(pl) && Main.t.isHere(location, Main.openCase.get(pl))) {
                pl.closeInventory();
            }
        }
        // register items
         int itemscount = customConfig.getAnimations().getInt("Wheel.ItemsCount");;
        for (int i = 0; i < itemscount; i++) {
            String winGroup = Main.t.getRandomGroup(c);
            String winGroupId = customConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Item.ID").toUpperCase();
            String winGroupDisplayName = customConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Item.DisplayName");
            Material material;
            ItemStack winItem = null;
            if (!winGroupId.startsWith("HEAD") && !winGroupId.startsWith("BASE64")) {
                material = Material.getMaterial(winGroupId);
                if (material == null) {
                    material = Material.STONE;
                }
                winItem = Main.t.createItem(material, 1, 0, winGroupDisplayName);
            } else if (winGroupId.startsWith("HEAD")) {
                String[] parts = winGroupId.split(":");
                winItem = Main.t.getPlayerHead(parts[1], winGroupDisplayName);
            } else if (winGroupId.startsWith("HDB")) {
                String[] parts = winGroupId.split(":");
                String id = parts[1];
                if (Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                    winItem = Main.t.getHDBSkull(id, winGroupDisplayName);
                } else {
                    winItem = new ItemStack(Material.STONE);
                }
            } else if(winGroupId.startsWith("CH")) {
                String[] parts = winGroupId.split(":");
                String category = parts[1];
                String id = parts[2];
                if (Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                    winItem = Main.t.getCHSkull(category, id, winGroupDisplayName);
                } else {
                    winItem = new ItemStack(Material.STONE);
                }
            }
            items.add(winItem);
            groups.add(winGroup);
            armorStands.add(spawnArmorStand(location, i));
        }

        (new BukkitRunnable() {
            int ticks = 0;

            // Configurable variables:
            final double speed = customConfig.getAnimations().getDouble("Wheel.CircleSpeed");; // rotation speed; how many times an element does a whole circle per second
            final double radius = customConfig.getAnimations().getDouble("Wheel.CircleRadius"); // the radius of the circle, in blocks

            // Constants:
            final double offset = 2 * Math.PI / itemscount; // 'entities' is your collection of entities
            Location location = lAC.clone().add(0.5, -1, 0);

            public void run() {
                ticks++;
                double angle = ticks / 20.0; // the seconds for which the runnable has been running
                angle *= speed; // apply speed
                angle *= 2 * Math.PI; // scale to radians (2PI = 360 degrees = 1 circle)
                if(ticks < 101) {
                    for (ArmorStand entity : armorStands) {
                        double x = radius * Math.sin(angle);
                        double y = radius * Math.cos(angle);

                        // add the (x, y) to the >center< location,
                        // as they are an offset, not the resulting location.
                        // remember to clone the center!
                        Location newLoc = location.clone().add(x, y, 0);
                        // teleport to the new offset location
                        entity.teleport(newLoc);

                        angle += offset; // Update angle

                        double targetAngle = 0.5 * Math.PI; // Adjust this value to the desired angle
                        double currentAngle = angle % (2 * Math.PI); // Normalize the angle to [0, 2PI)
                        double threshold = 0.1; // Adjust this value to set the proximity threshold
                        if (Math.abs(currentAngle - targetAngle) < threshold) {
                            if(customConfig.getAnimations().getString("Wheel.Scroll.Sound") != null) {
                                location.getWorld().playSound(location,
                                        Sound.valueOf(customConfig.getAnimations().getString("Wheel.Scroll.Sound")),
                                        customConfig.getAnimations().getInt("Wheel.Scroll.Volume"),
                                        customConfig.getAnimations().getInt("Wheel.Scroll.Pitch"));
                            }
                        }
                    }
                }
                if (ticks == 101) {
                    String winGroup = groups.get(groups.size() / 2);
                    Main.t.onCaseOpenFinish(c, player, false, winGroup);
                    if(customConfig.getAnimations().getString("Wheel.Finish.Sound") != null) {
                        location.getWorld().playSound(location, Sound.valueOf(customConfig.getAnimations().getString("Wheel.Finish.Sound")),
                                customConfig.getAnimations().getInt("Wheel.Finish.Volume"),
                                customConfig.getAnimations().getInt("Wheel.Finish.Pitch"));
                    }
                }
                // End

                if (this.ticks >= 120) {
                    this.cancel();
                    Main.ActiveCase.remove(lAC);
                    for(ArmorStand stand : armorStands) {
                        Main.listAR.remove(stand);
                        stand.remove();
                    }
                    StartAnimation.caseOpen.remove(player);
                }
            }
        }).runTaskTimer(Main.instance, 0L, 2L);
    }
    private ArmorStand spawnArmorStand(Location location, int index) {
        ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        as.setVisible(false);
        Main.listAR.add(as);
        as.setGravity(false);
        as.setSmall(true);
        as.setCustomNameVisible(true);
        as.setHelmet(items.get(index));
        as.setCustomName(items.get(index).getItemMeta().getDisplayName());
        return as;
    }
}
