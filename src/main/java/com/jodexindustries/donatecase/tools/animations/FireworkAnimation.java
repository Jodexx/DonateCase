package com.jodexindustries.donatecase.tools.animations;

import com.jodexindustries.donatecase.api.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

import static com.jodexindustries.donatecase.dc.Main.customConfig;
import static com.jodexindustries.donatecase.dc.Main.t;

public class FireworkAnimation implements Animation {

    @Override
    public String getName() {
        return "DEFAULT FIREWORK";
    }

    public void start(Player player, Location location, CaseData c, CaseData.Item winItem) {
        final Location lAC = location.clone();
        String displayName = winItem.getMaterial().getDisplayName();
        winItem.getMaterial().setDisplayName(PAPISupport.setPlaceholders(player, displayName));
        location.add(0.5, -0.1, 0.5);
        location.setYaw(-70.0F);
        ArmorStandCreator as = t.createArmorStand();
        as.spawnArmorStand(location);
        as.setSmall(true);
        as.setVisible(false);
        as.setGravity(false);
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
                    for (String color : customConfig.getAnimations().getStringList("Firework.FireworkColors")) {
                        data.addEffect(FireworkEffect.builder().withColor(t.parseColor(color)).build());
                    }
                    data.setPower(customConfig.getAnimations().getInt("FireWork.Power"));
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
                        if(winItem.getMaterial().getItemStack().getType() != Material.AIR) {
                            as.setHelmet(winItem.getMaterial().getItemStack());
                        }
                        as.setCustomName(displayName);
                        Case.onCaseOpenFinish(c, player, true, winItem);
                    }
                    if (this.i >= 30) {
                        as.remove();
                        this.cancel();
                        Case.animationEnd(c, getName(), player, lAC, winItem);
                    }
                }

                ++this.i;
            }
        }).runTaskTimer(Main.instance, 0L, 2L);
    }
}
