package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.dc.Main;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.jodexindustries.donatecase.dc.Main.customConfig;

public class Tools {


    public void launchFirework(Location l) {
        Random r = new Random();
        Firework fw = (Firework)l.getWorld().spawnEntity(l.subtract(new Vector(0.0, 0.5, 0.0)), EntityType.FIREWORK);
        FireworkMeta meta = fw.getFireworkMeta();
        Color[] c = new Color[]{Color.RED, Color.AQUA, Color.GREEN, Color.ORANGE, Color.LIME, Color.BLUE, Color.MAROON, Color.WHITE};
        meta.addEffect(FireworkEffect.builder().flicker(false).with(Type.BALL).trail(false).withColor(c[r.nextInt(c.length)], c[r.nextInt(c.length)], c[r.nextInt(c.length)]).build());
        fw.setFireworkMeta(meta);
        fw.setMetadata("case", new FixedMetadataValue(Main.instance, "case"));
        fw.detonate();
    }

    public int c(int x, int y) {
        int x2 = x - 1;
        int y2 = y - 1;
        return x2 + y2 * 9;
    }

    public boolean isHere(Location l1, Location l2) {
        return l1.getWorld() == l2.getWorld() && (int)l1.distance(l2) == 0;
    }

    public String getRandomGroup(String casename) {
        Random random = new Random();
        int maxChance = 0;
        int from = 0;

        Set<String> itemKeys = customConfig.getConfig().getConfigurationSection("DonatCase.Cases." + casename + ".Items").getKeys(false);

        for (String item : itemKeys) {
            maxChance += customConfig.getConfig().getInt("DonatCase.Cases." + casename + ".Items." + item + ".Chance");
        }

        int rand = random.nextInt(maxChance);

        for (String item : itemKeys) {
            int itemChance = customConfig.getConfig().getInt("DonatCase.Cases." + casename + ".Items." + item + ".Chance");
            if (from <= rand && rand < from + itemChance) {
                return item;
            }
            from += itemChance;
        }

        return null;
    }

    public void msg(CommandSender s, String msg) {
        if (s != null) {
            msg_(s, Main.lang.getString("Prefix") + msg);
        }
    }

    public void msg_(CommandSender s, String msg) {
        if (s != null) {
            s.sendMessage(rc(msg));
        }
    }

    public String rc(String t) {
        return ChatColor.translateAlternateColorCodes('&', t);
    }

    public String rt(String text, String... repl) {
        for (String s : repl) {
            int l = s.split(":")[0].length();
            if(text != null) {
                text = text.replace(s.substring(0, l), s.substring(l + 1));
            } else {
                text = rc("&cMessage not found! Update lang file!");
            }
        }

        return text;
    }

    public List<String> rt(List<String> text, String... repl) {
        ArrayList<String> rt = new ArrayList<>();

        for (String t : text) {
            rt.add(rt(t, repl));
        }

        return rt;
    }

    public List<String> rc(List<String> t) {
        ArrayList<String> a = new ArrayList<>();

        for (String s : t) {
            a.add(rc(s));
        }

        return a;
    }

    public ItemStack createItem(Material ma, int amount, int data, String dn) {
        return createItem(ma, data, amount, dn, null);
    }

    public ItemStack createItem(Material ma, String dn, List<String> lore) {
        return createItem(ma, 0, 1, dn, lore);
    }

    public ItemStack getPlayerHead(String player, String displayname) {
        Material type = Material.PLAYER_HEAD;
        ItemStack item = new ItemStack(type, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(player)));
        item.setItemMeta(meta);
        ItemMeta itemmeta = item.getItemMeta();
        Objects.requireNonNull(itemmeta).setDisplayName(rc(displayname));
        item.setItemMeta(itemmeta);

        return item;
    }

    public Color parseColor(String s) {

        Color color = null;
        String[] split = s.split(" ");
        if (split.length > 2) {
            try {
                // RGB
                int red = Integer.parseInt(split[0]);
                int green = Integer.parseInt(split[1]);
                int blue = Integer.parseInt(split[2]);
                color = Color.fromRGB(red, green, blue);

            } catch (NumberFormatException e) {
                // Name
                Field[] fields = Color.class.getFields();
                for (Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers())
                            && field.getType() == Color.class) {

                        if (field.getName().equalsIgnoreCase(s)) {
                            try {
                                return (Color) field.get(null);
                            } catch (IllegalArgumentException | IllegalAccessException e1) {
                                e1.printStackTrace();
                            }
                        }

                    }
                }

            }
        } else {
            // Name
            Field[] fields = Color.class.getFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())
                        && field.getType() == Color.class) {

                    if (field.getName().equalsIgnoreCase(s)) {
                        try {
                            return (Color) field.get(null);
                        } catch (IllegalArgumentException | IllegalAccessException e1) {
                            e1.printStackTrace();
                        }
                    }

                }
            }

        }

        return color;
    }

    public ItemStack getPlayerHead(String player, String displayname, List<String> lore) {
        Material type = Material.PLAYER_HEAD;
        ItemStack item = new ItemStack(type, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        Objects.requireNonNull(meta).setOwner(player);
        item.setItemMeta(meta);
        ItemMeta itemmeta = item.getItemMeta();
        Objects.requireNonNull(itemmeta).setDisplayName(rc(displayname));
        itemmeta.setLore(rc(lore));
        item.setItemMeta(itemmeta);

        return item;
    }
    public ItemStack getHDBSkull(String id, String displayname, List<String> lore) {
        HeadDatabaseAPI api = new HeadDatabaseAPI();
        ItemStack item = new ItemStack(Material.STONE);
        try {
            item = api.getItemHead(id);
        } catch (NullPointerException nullPointerException) {
            Main.instance.getLogger().info("Could not find the head you were looking for");
        }
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(rc(displayname));
        if(lore != null) {
            itemMeta.setLore(rc(lore));
        }
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack getHDBSkull(String id, String displayname) {
        HeadDatabaseAPI api = new HeadDatabaseAPI();
        ItemStack item = new ItemStack(Material.STONE);
        try {
            item = api.getItemHead(id);
        } catch (NullPointerException nullPointerException) {
            Main.instance.getLogger().info("Could not find the head you were looking for");
        }
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(rc(displayname));
        item.setItemMeta(itemMeta);
        return item;
    }

    public ItemStack createItem(Material ma, int data, int amount, String dn, List<String> lore) {
        ItemStack item = new ItemStack(ma, amount);
        ItemMeta m = item.getItemMeta();
        if (dn != null) {
            m.setDisplayName(rc(dn));
        }

        if (lore != null) {
            m.setLore(this.rc(lore));
        }

        item.setItemMeta(m);
        return item;
    }

    public void onCaseOpenFinish(String casename, Player player, boolean needsound, String winGroup) {
        String sound;
        String casetitle = customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Title");
        String winGroupDisplayName = customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Items." + winGroup + ".Item.DisplayName");
        String winGroupGroup = customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Items." + winGroup + ".Group");
        // Give command
        String givecommand = customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Items." + winGroup + ".GiveCommand");
        if (customConfig.getConfig().getBoolean("DonatCase.LevelGroup")) {
            String playergroup = Main.getPermissions().getPrimaryGroup(player).toLowerCase();
            if (customConfig.getConfig().getConfigurationSection("DonatCase.LevelGroups").contains(playergroup) &&
                    customConfig.getConfig().getInt("DonatCase.LevelGroups." + playergroup) >=
                            customConfig.getConfig().getInt("DonatCase.LevelGroups." + winGroupGroup)) {
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.t.rt(givecommand, "%player:" + player.getName(), "%group:" + winGroupGroup));
            }
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.t.rt(givecommand, "%player:" + player.getName(), "%group:" + winGroupGroup));
        }
        // Custom commands
        for (String command : customConfig.getConfig().getStringList("DonatCase.Cases." + casename + ".Items." + winGroup + ".Commands")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.t.rt(command, "%player:" + player.getName(), "%group:" + winGroupGroup));
        }
        // Sound
        if (needsound) {
            if (customConfig.getConfig().getString("DonatCase.Cases." + casename + ".AnimationSound") != null) {
                sound = Objects.requireNonNull(customConfig.getConfig().getString("DonatCase.Cases." + casename + ".AnimationSound"));
                Sound sound1;
                sound1 = Sound.valueOf(sound.toUpperCase());
                if (sound1 == null) {
                    sound1 = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
                }
                player.playSound(player.getLocation(), sound1, 1.0F, 5.0F);
            }
        }
        // Title && SubTitle
        String title;
        if (customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Item." + winGroup + ".Title") != null) {

            title = Main.t.rc(Main.t.rt(customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Item." + winGroup + ".Title"),
                    "%groupdisplayname:" + winGroupDisplayName, "%group:" + winGroup));
        } else {
            title = "";
        }
        String subtitle;
        if (customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Item." + winGroup + ".Title") != null) {
            subtitle = Main.t.rc(Main.t.rt(customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Item." + winGroup + ".SubTitle"),
                    "%groupdisplayname:" + winGroupDisplayName, "%group:" + winGroup));
        } else {
            subtitle = "";
        }
        player.sendTitle(title, subtitle, 5, 60, 5);

        // Broadcast
        for (String cmd2 : customConfig.getConfig().getStringList("DonatCase.Cases." + casename + ".Items." + winGroup + ".Broadcast")) {
            Bukkit.broadcastMessage(Main.t.rc(Main.t.rt(cmd2, "%player:" + player.getName(), "%group:" + winGroupDisplayName, "%case:" + casetitle)));
        }
    }
}
