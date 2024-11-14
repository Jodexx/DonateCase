package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.manager.AnimationManager;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.ToolsBukkit;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class FireworkAnimation extends JavaAnimationBukkit {
    private EquipmentSlot itemSlot;
    private ArmorStandEulerAngle armorStandEulerAngle;

    public static void register(AnimationManager<JavaAnimationBukkit, CaseDataMaterialBukkit, ItemStack, Player, Location, CaseDataBukkit> manager) {
        CaseAnimation<JavaAnimationBukkit, CaseDataMaterialBukkit, ItemStack> caseAnimation = manager.builder("FIREWORK")
                .animation(FireworkAnimation.class)
                .description("Fireworks fly to the skies and a prize appears")
                .requireSettings(true)
                .build();

        manager.registerAnimation(caseAnimation);
    }

    @Override
    public void start() {
        itemSlot = EquipmentSlot.valueOf(getSettings().getString("ItemSlot", "HEAD").toUpperCase());
        armorStandEulerAngle = Tools.getArmorStandEulerAngle(getSettings().getConfigurationSection("Pose"));
        String displayName = getWinItem().getMaterial().getDisplayName();
        getWinItem().getMaterial().setDisplayName(Case.getInstance().papi.setPlaceholders(getPlayer(), displayName));
        getLocation().add(0.5, 1, 0.5);
        ArmorStandCreator as = ToolsBukkit.createArmorStand(getLocation());

        boolean small = getSettings().getBoolean("SmallArmorStand", true);
        as.setSmall(small);
        as.setVisible(false);
        as.setGravity(false);
        as.spawn();
        Bukkit.getScheduler().runTaskTimer(Case.getInstance(), new Task(as), 0L, 2L);
    }

    private class Task implements Consumer<BukkitTask> {

        private int i; //ticks count
        private final Location l;
        private final ArmorStandCreator as;
        private final World world;

        public Task(ArmorStandCreator as) {
            this.as = as;
            this.l = as.getLocation();
            world = l.getWorld() != null ? l.getWorld() : getPlayer().getWorld();
        }

        @Override
        public void accept(BukkitTask task) {
            if (this.i == 1) {
                Firework firework = world.spawn(l, Firework.class);
                FireworkMeta data = firework.getFireworkMeta();
                data.addEffects(FireworkEffect.builder().withColor(Color.PURPLE).withColor(Color.RED).with(FireworkEffect.Type.BALL).withFlicker().build());
                for (String color : getSettings().getStringList("FireworkColors")) {
                    data.addEffect(FireworkEffect.builder().withColor(Tools.parseColor(color)).build());
                }
                data.setPower(getSettings().getInt("Power"));
                firework.setFireworkMeta(data);
            }
            if (this.i >= 7) {
                l.setYaw(l.getYaw() + 20F);
                as.teleport(l);

                if (this.i == 10) {
                    if (getWinItem().getMaterial().getItemStack().getType() != Material.AIR) {
                        as.setEquipment(itemSlot, getWinItem().getMaterial().getItemStack());
                    }
                    as.setAngle(armorStandEulerAngle);
                    if (getWinItem().getMaterial().getDisplayName() != null && !getWinItem().getMaterial().getDisplayName().isEmpty())
                        as.setCustomNameVisible(true);
                    as.setCustomName(getWinItem().getMaterial().getDisplayName());
                    as.updateMeta();
                    Case.animationPreEnd(getCaseDataBukkit(), getPlayer(), getUuid(), getWinItem());
                }
                if (this.i >= 30) {
                    as.remove();
                    task.cancel();
                    Case.animationEnd(getCaseDataBukkit(), getPlayer(), getUuid(), getWinItem());
                }
            }

            ++this.i;
        }
    }
}