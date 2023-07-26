package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.PAPISupport;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.jodexindustries.donatecase.dc.Main.customConfig;
import static com.jodexindustries.donatecase.dc.Main.t;

public class Wheel implements Animation {

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
        loc.setZ(loc.getZ() + 0.5);
        // register items
         int itemscount = customConfig.getAnimations().getInt("Wheel.ItemsCount");
        for (int i = 0; i < itemscount; i++) {
            String winGroup = Tools.getRandomGroup(c);
            String winGroupId = Case.getWinGroupId(c, winGroup);
            String winGroupDisplayName = t.rc(Case.getWinGroupDisplayName(c, winGroup));
            if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                winGroupId = PAPISupport.setPlaceholders(player, winGroupId);
                winGroupDisplayName = PAPISupport.setPlaceholders(player, winGroupDisplayName);
            }
            boolean winGroupEnchant = Case.getWinGroupEnchant(c, winGroup);
            Material material;
            ItemStack winItem;
            if (!winGroupId.contains(":")) {
                material = Material.getMaterial(winGroupId);
                if (material == null) {
                    material = Material.STONE;
                }
                if(material != Material.AIR) {
                    winItem = t.createItem(material, 1, -1, winGroupDisplayName, winGroupEnchant);
                } else {
                    winItem = new ItemStack(Material.AIR);
                    ItemMeta meta = winItem.getItemMeta();
                    winItem.setItemMeta(meta);
                }
            } else {
                if (winGroupId.startsWith("HEAD")) {
                    String[] parts = winGroupId.split(":");
                    winItem = t.getPlayerHead(parts[1], winGroupDisplayName);
                } else if (winGroupId.startsWith("HDB")) {
                    String[] parts = winGroupId.split(":");
                    String id = parts[1];
                    if (Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                        winItem = t.getHDBSkull(id, winGroupDisplayName);
                    } else {
                        winItem = new ItemStack(Material.STONE);
                    }
                } else if (winGroupId.startsWith("CH")) {
                    String[] parts = winGroupId.split(":");
                    String category = parts[1];
                    String id = parts[2];
                    if (Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                        winItem = t.getCHSkull(category, id, winGroupDisplayName);
                    } else {
                        winItem = new ItemStack(Material.STONE);
                    }
                } else if (winGroupId.startsWith("BASE64")) {
                    String[] parts = winGroupId.split(":");
                    String base64 = parts[1];
                    winItem = t.getBASE64Skull(base64, winGroupDisplayName);
                } else {
                    String[] parts = winGroupId.split(":");
                    byte data = -1;
                    if(parts[1] != null) {
                        data = Byte.parseByte(parts[1]);
                    }
                    material = Material.getMaterial(parts[0]);
                    if (material == null) {
                        material = Material.STONE;
                    }
                    winItem = t.createItem(material, data, 1, winGroupDisplayName, winGroupEnchant);
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
            final Location location = loc.clone().add(loc.getDirection().multiply(1).getX() + 0.5, -1, 0);
            public void run() {
                ticks++;
                double angle = ticks / 20.0;
                angle *= speed;
                angle *= 2 * Math.PI;

                if(ticks < 101) {
                    for (ArmorStand entity : armorStands) {
                        double x = radius * Math.sin(angle);
                        double y = radius * Math.cos(angle);
                        Location newLoc = location.clone().add(location.getDirection().multiply(-1).getX() + x, y, 0);
                        entity.teleport(newLoc);
                        angle += offset;

                        double targetAngle = 0.5 * Math.PI;
                        double currentAngle = angle % (2 * Math.PI);
                        double threshold = 0.1;
                        if (Math.abs(currentAngle - targetAngle) < threshold) {
                            if(customConfig.getAnimations().getString("Wheel.Scroll.Sound") != null) {
                                Objects.requireNonNull(location.getWorld()).playSound(location,
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
                    for(ArmorStand stand : armorStands) {
                        Case.listAR.remove(stand);
                        stand.remove();
                    }
                    Case.animationEnd(c, getName(), player, loc);
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
