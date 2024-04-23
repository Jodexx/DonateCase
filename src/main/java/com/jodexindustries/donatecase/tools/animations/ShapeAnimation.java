package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.data.Animation;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

import static com.jodexindustries.donatecase.DonateCase.customConfig;

public class ShapeAnimation implements Animation {
    private EquipmentSlot itemSlot;
    private ArmorStandEulerAngle armorStandEulerAngle;

    @Override
    public String getName() {
        return "DEFAULT SHAPE";
    }

    @Override
    public void start(Player player, Location location, UUID uuid, CaseData c, CaseData.Item winItem) {
        location.add(0.5, -0.1, 0.5);
        location.setYaw(-70.0F);
        final ArmorStandCreator as = Tools.createArmorStand();
        as.spawnArmorStand(location);
        armorStandEulerAngle = Tools.getArmorStandEulerAngle("Shape.Pose");
        itemSlot = EquipmentSlot.valueOf(customConfig.getAnimations().getString("Shape.ItemSlot", "HEAD").toUpperCase());
        boolean small = customConfig.getAnimations().getBoolean("Shape.SmallArmorStand", true);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);

        float whiteSize = (float) customConfig.getAnimations().getDouble("Shape.Particle.White.Size");
        float orangeSize = (float) customConfig.getAnimations().getDouble("Shape.Particle.Orange.Size");
        String rgbString = customConfig.getAnimations().getString("Shape.Particle.Orange.Rgb");
        String whiteRgbString = customConfig.getAnimations().getString("Shape.Particle.White.Rgb");
        int red;
        int green;
        int blue;
        Color orangeColor = Color.ORANGE;
        Color whiteColor = Color.WHITE;
        if(rgbString != null) {
            String[] rgb = rgbString.replaceAll(" ", "").split(",");
            red = Integer.parseInt(rgb[0]);
            green = Integer.parseInt(rgb[1]);
            blue = Integer.parseInt(rgb[2]);
            orangeColor = Color.fromRGB(red, green, blue);
        }
        if(whiteRgbString != null) {
            String[] rgb = whiteRgbString.replaceAll(" ", "").split(",");
            red = Integer.parseInt(rgb[0]);
            green = Integer.parseInt(rgb[1]);
            blue = Integer.parseInt(rgb[2]);
            whiteColor = Color.fromRGB(red, green, blue);
        }
        Color finalOrangeColor = orangeColor;
        Color finalWhiteColor = whiteColor;
        (new BukkitRunnable() {
            int i; //ticks count
            double t;
            Location l;

            public void run() {
                if (i == 0) {
                    l = as.getLocation();
                }
                if (i >= 7) {
                    if (i == 16) {
                        if(winItem.getMaterial().getItemStack().getType() != Material.AIR) {
                            as.setEquipment(itemSlot, winItem.getMaterial().getItemStack());
                        }
                        as.setPose(armorStandEulerAngle);
                        as.setCustomName(winItem.getMaterial().getDisplayName());
                        Tools.launchFirework(this.l.clone().add(0.0, 0.8, 0.0));
                        Case.onCaseOpenFinish(c, player, true, winItem);

                    }
                }

                if (i <= 15) {
                    CaseData.Item winItem = Case.getRandomItem(c);
                    if(winItem.getMaterial().getItemStack().getType() != Material.AIR) {
                        as.setPose(armorStandEulerAngle);
                        as.setEquipment(itemSlot, winItem.getMaterial().getItemStack());
                    }
                    String winGroupDisplayName = PAPISupport.setPlaceholders(player,winItem.getMaterial().getDisplayName());
                    winItem.getMaterial().setDisplayName(winGroupDisplayName);
                    as.setCustomName(Tools.rc(winGroupDisplayName));
                    if (this.i <= 8) {
                        if (!Bukkit.getVersion().contains("1.12")) {
                            Particle.DustOptions dustOptions = new Particle.DustOptions(finalOrangeColor,orangeSize);
                            Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.REDSTONE, this.l.clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0, dustOptions);
                        } else {
                            Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.REDSTONE, this.l.clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0);
                        }
                    }
                }

                Location las = as.getLocation().clone();
                las.setYaw(las.getYaw() + 20.0F);
                as.teleport(las);
                this.l = this.l.add(0.0, 0.13, 0.0);
                if (this.i <= 7) {
                    this.l.setYaw(las.getYaw());
                    as.teleport(this.l);
                }
                //white trail
                if (this.i <= 15) {
                    this.t += 0.25;
                    Location loc = this.l.clone();
                    loc = loc.add(0.0, 0.5, 0.0);
                    for (double phi = 0.0; phi <= 9.4; phi += 1) {
                        final double x = 0.09 * (9.5 - this.t * 2.5) * Math.cos(this.t + phi);
                        final double z = 0.09 * (9.5 - this.t * 2.5) * Math.sin(this.t + phi);
                        loc.add(x, 0.0, z);
                        if (!Bukkit.getVersion().contains("1.12")) {
                            Particle.DustOptions dustOptions = new Particle.DustOptions(finalWhiteColor, whiteSize);
                            Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.REDSTONE, this.l.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0, dustOptions);
                        } else {
                            Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.FIREWORKS_SPARK, this.l.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0);
                        }
                        loc.subtract(x, 0.0, z);
                        if (this.t >= 22) {
                            loc.add(x, 0.0, z);
                            this.t = 0.0;
                        }
                    }
                }
                //end
                if (this.i >= 40) {
                    as.remove();
                    this.cancel();
                    Case.animationEnd(c, getName(), player, uuid, winItem);
                }

                ++this.i;
            }
        }).runTaskTimer(DonateCase.instance, 0L, 2L);
    }
}