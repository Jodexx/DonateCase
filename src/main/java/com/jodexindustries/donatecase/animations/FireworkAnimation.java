package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.data.JavaAnimation;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class FireworkAnimation extends JavaAnimation {
    private final EquipmentSlot itemSlot;
    private final ArmorStandEulerAngle armorStandEulerAngle;

    public FireworkAnimation() {
        itemSlot = EquipmentSlot.valueOf(Case.getConfig().getAnimations().getString("Firework.ItemSlot", "HEAD").toUpperCase());
        armorStandEulerAngle = Tools.getArmorStandEulerAngle("Firework.Pose");
    }

    @Override
    public void start() {
        String displayName = getWinItem().getMaterial().getDisplayName();
        getWinItem().getMaterial().setDisplayName(PAPISupport.setPlaceholders(getPlayer(), displayName));
        getLocation().add(0.5, -0.1, 0.5);
        getLocation().setYaw(-70.0F);
        ArmorStandCreator as = Tools.createArmorStand(getLocation());

        boolean small = Case.getConfig().getAnimations().getBoolean("Firework.SmallArmorStand", true);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        as.spawn();
        (new BukkitRunnable() {
            int i; //ticks count
            Location l;

            public void run() {
                if (this.i == 0) {
                    this.l = as.getLocation();
                }
                if (this.i == 1) {
                    Location loc = this.l.clone().add(0.0, 1.0, 0);
                    Firework firework = Objects.requireNonNull(loc.getWorld()).spawn(loc, Firework.class);
                    FireworkMeta data = firework.getFireworkMeta();
                    data.addEffects(FireworkEffect.builder().withColor(Color.PURPLE).withColor(Color.RED).with(FireworkEffect.Type.BALL).withFlicker().build());
                    for (String color : Case.getConfig().getAnimations().getStringList("Firework.FireworkColors")) {
                        data.addEffect(FireworkEffect.builder().withColor(Tools.parseColor(color)).build());
                    }
                    data.setPower(Case.getConfig().getAnimations().getInt("Firework.Power"));
                    firework.setFireworkMeta(data);
                }
                Location las = as.getLocation().clone();
                las.setYaw(las.getYaw() + 20.0F);
                as.teleport(las);
                this.l = this.l.add(0.0, 0.14, 0.0);
                if (this.i <= 7) {
                    this.l.setYaw(las.getYaw());
                    as.teleport(this.l);
                }
                if (this.i >= 7) {
                    if (this.i == 10) {
                        if(getWinItem().getMaterial().getItemStack().getType() != Material.AIR) {
                            as.setEquipment(itemSlot, getWinItem().getMaterial().getItemStack());
                        }
                        as.setAngle(armorStandEulerAngle);
                        as.setCustomName(displayName);
                        as.setCustomNameVisible(true);
                        as.updateMeta();
                        Case.animationPreEnd(getCaseData(), getPlayer(), true, getWinItem(), getLocation());
                    }
                    if (this.i >= 30) {
                        as.remove();
                        this.cancel();
                        Case.animationEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());
                    }
                }

                ++this.i;
            }
        }).runTaskTimer(DonateCase.instance, 0L, 2L);
    }
}
