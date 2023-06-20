package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.dc.Main;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jodexindustries.donatecase.dc.Main.customConfig;

public class Tools {


    public static String getRandomGroup(String casename) {
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
    public void convertCasesLocation() {
        ConfigurationSection cases_ = customConfig.getCases().getConfigurationSection("DonatCase.Cases");
        for (String name : cases_.getValues(false).keySet()) {
            if(customConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                return;
            } else {
                String stringlocation = customConfig.getCases().getString("DonatCase.Cases." + name + ".location");
                Location lv = fromString(stringlocation);
                String world = "Undefined";
                if(lv != null) {
                    if(lv.getWorld() != null) {
                        world = lv.getWorld().getName();
                    }
                    String location = world + ";" + lv.getX() + ";" + lv.getY() + ";" + lv.getZ() + ";" + lv.getPitch() + ";" + lv.getYaw();
                    customConfig.getCases().set("DonatCase.Cases." + name + ".location", location);
                }
            }
        }
        customConfig.getCases().set("config", "1.0");
        customConfig.saveCases();
        Main.instance.getLogger().info("Conversion successful!");
    }
    public Location fromString(String str) {
        String regex = "Location\\{world=CraftWorld\\{name=(.*?)},x=(.*?),y=(.*?),z=(.*?),pitch=(.*?),yaw=(.*?)}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            World world = null;
            if (!matcher.group(1).equals("null")) {
                world = Bukkit.getWorld(matcher.group(1));
            }
            double x = Double.parseDouble(matcher.group(2));
            double y = Double.parseDouble(matcher.group(3));
            double z = Double.parseDouble(matcher.group(4));
            float pitch = Float.parseFloat(matcher.group(5));
            float yaw = Float.parseFloat(matcher.group(6));

            return new Location(world, x, y, z, yaw, pitch);
        }

        return null;
    }

    public List<String> rc(List<String> t) {
        ArrayList<String> a = new ArrayList<>();

        for (String s : t) {
            a.add(rc(s));
        }

        return a;
    }

    public ItemStack createItem(Material ma, int amount, int data, String dn, boolean enchant) {
        return createItem(ma, data, amount, dn, null, enchant);
    }

    public ItemStack createItem(Material ma, String dn, List<String> lore, boolean enchant) {
        return createItem(ma, 0, 1, dn, lore, enchant);
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
    public ItemStack getCHSkull(String category, String id, String displayname) {
        if(Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
            ItemStack item = new CustomHeadSupport().getSkull(category, id, displayname);
            return item;
        } else {
            return null;
        }
    }
    public ItemStack getCHSkull(String category, String id, String displayname, List<String> lore) {
        if(Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
            ItemStack item = new CustomHeadSupport().getSkull(category, id, displayname, lore);
            return item;
        } else {
            return null;
        }
    }

    public ItemStack createItem(Material ma, int data, int amount, String dn, List<String> lore, boolean enchant) {
        ItemStack item = new ItemStack(ma, amount);
        if(enchant) {
            item.addUnsafeEnchantment(Enchantment.LURE, 1);
        }
        ItemMeta m = item.getItemMeta();
        if (dn != null) {
            m.setDisplayName(rc(dn));
        }

        if (lore != null) {
            m.setLore(this.rc(lore));
        }
        if (enchant) {
            m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(m);
        return item;
    }

}
