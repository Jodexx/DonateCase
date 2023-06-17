package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.dc.Main;
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

public class Wheel extends Animation {

    List<ItemStack> items = new ArrayList<>();
    List<String> groups = new ArrayList<>();
    List<ArmorStand> armorStands = new ArrayList<>();
    @Override
     public void start(Player player, Location location, String c) {
        final Location lAC = location.clone();
        // register items
         int itemscount = customConfig.getAnimations().getInt("Wheel.ItemsCount");;
        for (int i = 0; i < itemscount; i++) {
            String winGroup = Case.getRandomGroup(c);
            String winGroupId = Case.getWinGroupId(c, winGroup);
            String winGroupDisplayName = Case.getWinGroupDisplayName(c, winGroupId);
            Material material;
            ItemStack winItem = null;
            if (!winGroupId.contains(":")) {
                material = Material.getMaterial(winGroupId);
                if (material == null) {
                    material = Material.STONE;
                }
                winItem = Main.t.createItem(material, 1, 0, winGroupDisplayName);
            } else {
                if (winGroupId.startsWith("HEAD")) {
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
                } else if (winGroupId.startsWith("CH")) {
                    String[] parts = winGroupId.split(":");
                    String category = parts[1];
                    String id = parts[2];
                    if (Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                        winItem = Main.t.getCHSkull(category, id, winGroupDisplayName);
                    } else {
                        winItem = new ItemStack(Material.STONE);
                    }
                }
            }
            items.add(winItem);
            groups.add(winGroup);
            armorStands.add(spawnArmorStand(location, i));
        }

        (new BukkitRunnable() {
            int ticks = 0;

            final double speed = customConfig.getAnimations().getDouble("Wheel.CircleSpeed");
            final double radius = customConfig.getAnimations().getDouble("Wheel.CircleRadius");

            final double offset = 2 * Math.PI / itemscount;
            final Location location = lAC.clone().add(0.5, -1, 0);

            public void run() {
                ticks++;
                double angle = ticks / 20.0;
                angle *= speed;
                angle *= 2 * Math.PI;

                if(ticks < 101) {
                    for (ArmorStand entity : armorStands) {
                        double x = radius * Math.sin(angle);
                        double y = radius * Math.cos(angle);
                        double z = Math.sin(Math.toRadians(location.getPitch())) * Math.cos(angle);

                        Location newLoc = location.clone().add(location.getDirection().getX() + x, y, 0);
                        entity.teleport(newLoc);
                        angle += offset;

                        double targetAngle = 0.5 * Math.PI;
                        double currentAngle = angle % (2 * Math.PI);
                        double threshold = 0.1;
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
                    Case.onCaseOpenFinish(c, player, true, winGroup);
                }
                // End

                if (this.ticks >= 120) {
                    this.cancel();
                    Case.ActiveCase.remove(lAC);
                    for(ArmorStand stand : armorStands) {
                        Case.listAR.remove(stand);
                        stand.remove();
                    }
                    Case.caseOpen.remove(player);
                }
            }
        }).runTaskTimer(Main.instance, 0L, 2L);
    }
    private ArmorStand spawnArmorStand(Location location, int index) {
        ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        as.setVisible(false);
        Case.listAR.add(as);
        as.setGravity(false);
        as.setSmall(true);
        as.setCustomNameVisible(true);
        as.setHelmet(items.get(index));
        as.setCustomName(items.get(index).getItemMeta().getDisplayName());
        return as;
    }
}
