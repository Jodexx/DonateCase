package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.CustomConfig;
import com.jodexindustries.donatecase.tools.StartAnimation;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import static com.jodexindustries.donatecase.dc.Main.customConfig;

public class FireworkShape {

    public FireworkShape(final Player player, Location location, final String c) {
        final Location lAC = location.clone();
        Main.ActiveCase.put(lAC, c);

        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (Main.openCase.containsKey(pl) && Main.t.isHere(location, Main.openCase.get(pl))) {
                pl.closeInventory();
            }
        }
        final String winGroup = Main.t.getRandomGroup(c);
        final String winGroupId = customConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Item.ID").toUpperCase();
        final String winGroupDisplayName = customConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Item.DisplayName");
        location.add(0.5, -0.1, 0.5);
        location.setYaw(-70.0F);
        final ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        Main.listAR.add(as);
        as.setGravity(false);
        as.setSmall(true);
        as.setVisible(false);
        as.setCustomNameVisible(false);
        (new BukkitRunnable() {
            int i; //ticks count
            Location l;

            public void run() {
                Material material;
                ItemStack winItem = null;
                if(!winGroupId.contains(":")) {
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
                if (this.i == 0) {
                    this.l = as.getLocation();
                }
                if (this.i == 1) {
                    Location loc = this.l.clone().add(0.0, 1.0, 0);
                    Firework firework = loc.getWorld().spawn(loc, Firework.class);
                    FireworkMeta data = firework.getFireworkMeta();
                    data.addEffects(FireworkEffect.builder().withColor(Color.PURPLE).withColor(Color.RED).with(FireworkEffect.Type.BALL).withFlicker().build());
                    for (String color : customConfig.getAnimations().getStringList("Firework.FireworkColors")) {
                        data.addEffect(FireworkEffect.builder().withColor(Main.t.parseColor(color)).build());
                    }
                    data.setPower(customConfig.getAnimations().getInt("FireWork.Power"));
                    firework.setFireworkMeta(data);
                }
                Location las = as.getLocation().clone();
                las.setYaw(las.getYaw() + 20.0F);
                as.teleport(las);
                this.l = this.l.add(0.0, 0.14, 0.0);
                // armor stand up :D
                if (this.i <= 7) {
                    this.l.setYaw(las.getYaw());
                    as.teleport(this.l);
                }

                if (this.i >= 7) {
                    if (this.i == 10) {
                        as.setCustomNameVisible(true);
                        as.setHelmet(winItem);
                        as.setCustomName(winItem.getItemMeta().getDisplayName());
                        Main.t.onCaseOpenFinish(c, player, true, winGroup);
                    }
                    // end
                    if (this.i >= 30) {
                        as.remove();
                        this.cancel();
                        Main.ActiveCase.remove(lAC);
                        Main.listAR.remove(as);
                        StartAnimation.caseOpen.remove(player);
                    }
                }



                ++this.i;
            }
        }).runTaskTimer(Main.instance, 0L, 2L);
    }
}
