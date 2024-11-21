package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.BukkitArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.PacketArmorStandCreator;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Random;

public class ToolsImpl implements DCToolsBukkit {
    
    private final DonateCase instance;
    
    public ToolsImpl(DonateCase instance) {
        this.instance = instance;
    }

    @Override
    public ArmorStandCreator createArmorStand(Location location) {
        if(instance.usePackets) {
            return new PacketArmorStandCreator(location);
        } else {
            return new BukkitArmorStandCreator(location);
        }
    }

    @Override
    public ItemStack loadCaseItem(String id) {
        ItemStack itemStack = null;

        if(id != null && Material.getMaterial(id) == null) {
            String temp = instance.api.getMaterialManager().getByStart(id);


            if (temp != null) {
                CaseMaterial<ItemStack> caseMaterial = instance.api.getMaterialManager().getRegisteredMaterial(temp);
                if (caseMaterial != null) {
                    String context = id.replace(temp, "").replaceFirst(":", "").trim();
                    itemStack = caseMaterial.handle(context);
                }
            }
        }

        if(itemStack == null) itemStack = DCToolsBukkit.createItem(id);

        return itemStack;
    }

    @Override
    public void launchFirework(Location location) {
        Random r = new Random();
        World world = location.getWorld();
        if(world == null) return;

        Firework firework = world.spawn(location.subtract(new Vector(0.0, 0.5, 0.0)), Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        Color[] color = new Color[]{Color.RED, Color.AQUA, Color.GREEN, Color.ORANGE, Color.LIME, Color.BLUE, Color.MAROON, Color.WHITE};
        meta.addEffect(FireworkEffect.builder().flicker(false).with(FireworkEffect.Type.BALL).trail(false).withColor(color[r.nextInt(color.length)], color[r.nextInt(color.length)], color[r.nextInt(color.length)]).build());
        firework.setFireworkMeta(meta);
        firework.setMetadata("case", new FixedMetadataValue(Case.getInstance(), "case"));
        firework.detonate();
    }
    
    @Override
    public void msg(CommandSender s, String msg) {
        if (s != null) {
            DCToolsBukkit.msgRaw(s, Case.getConfig().getLang().getString("prefix") + msg);
        }
    }
}
