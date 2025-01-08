package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.BukkitArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.PacketArmorStandCreator;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
    public boolean isValidPlayerName(String player) {
        if(instance.api.getConfig().getConfig().getBoolean("DonateCase.CheckPlayerName")) {
            return Arrays.stream(Bukkit.getOfflinePlayers())
                    .map(OfflinePlayer::getName)
                    .anyMatch(name -> name != null && name.equals(player.trim()));
        }
        return true;
    }

    @Override
    public PAPI getPAPI() {
        return instance.papi;
    }

    @Override
    public @NotNull List<String> resolveSDGCompletions(String[] args) {
        List<String> value = new ArrayList<>(instance.api.getConfig().getConfigCases().getCases().keySet());
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(px -> px.startsWith(args[0])).collect(Collectors.toList()));
            return list;
        } else if (args.length >= 3) {
            if (args.length == 4) {
                list.add("-s");
                return list;
            }
            return new ArrayList<>();
        }
        if (args[args.length - 1].isEmpty()) {
            list = value;
        } else {
            list.addAll(value.stream().filter(tmp -> tmp.startsWith(args[args.length - 1])).collect(Collectors.toList()));
        }
        return list;
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
                    itemStack = caseMaterial.getMaterialHandler().handle(context);
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
            DCToolsBukkit.msgRaw(s, instance.api.getConfig().getLang().getString("prefix") + msg);
        }
    }
}
