package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import static com.jodexindustries.donatecase.dc.Main.customConfig;

public class FireworkShape implements Animation {

    @Override
    public String getName() {
        return "DEFAULT FIREWORK";
    }

    public void start(Player player, Location location, String c) {
        final Location lAC = location.clone();
        final String winGroup = Tools.getRandomGroup(c);
        final String winGroupId = Case.getWinGroupId(c, winGroup);
        final String winGroupDisplayName = Case.getWinGroupDisplayName(c, winGroup);
        final boolean winGroupEnchant = Case.getWinGroupEnchant(c, winGroup);
        location.add(0.5, -0.1, 0.5);
        location.setYaw(-70.0F);
        final ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        Case.listAR.add(as);
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
                    winItem = Main.t.createItem(material, 1, 0, winGroupDisplayName, winGroupEnchant);
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
                    } else if (winGroupId.startsWith("BASE64")) {
                        String[] parts = winGroupId.split(":");
                        String base64 = parts[1];
                        winItem = Main.t.getBASE64Skull(base64, winGroupDisplayName);
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
                        Case.onCaseOpenFinish(c, player, true, winGroup);
                    }
                    // end
                    if (this.i >= 30) {
                        as.remove();
                        this.cancel();
                        Case.animationEnd(c, getName(), player, lAC);
                        Case.listAR.remove(as);
                    }
                }



                ++this.i;
            }
        }).runTaskTimer(Main.instance, 0L, 2L);
    }
}
