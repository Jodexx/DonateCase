package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.MaterialType;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.support.CustomHeadSupport;
import com.jodexindustries.donatecase.tools.support.HeadDatabaseSupport;
import com.jodexindustries.donatecase.tools.support.ItemsAdderSupport;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jodexindustries.donatecase.dc.Main.*;
import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

public class Tools {


    public static String getRandomGroup(String c) {
        Random random = new Random();
        int maxChance = 0;
        int from = 0;

        Set<String> itemKeys = casesConfig.getCase(c).getConfigurationSection("case.Items").getKeys(false);

        for (String item : itemKeys) {
            maxChance += casesConfig.getCase(c).getInt("case.Items." + item + ".Chance");
        }

        int rand = random.nextInt(maxChance);

        for (String item : itemKeys) {
            int itemChance = casesConfig.getCase(c).getInt("case.Items." + item + ".Chance");
            if (from <= rand && rand < from + itemChance) {
                return item;
            }
            from += itemChance;
        }

        return null;
    }

    public void launchFirework(Location l) {
        Random r = new Random();
        Firework fw = (Firework) Objects.requireNonNull(l.getWorld()).spawnEntity(l.subtract(new Vector(0.0, 0.5, 0.0)), EntityType.FIREWORK);
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
        if(t != null) {
            return hex(t);
        } else {
            return rc("&cMessage not found!");
        }
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
        if(cases_ != null) {
            for (String name : cases_.getValues(false).keySet()) {
                if (customConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                    return;
                } else {
                    String stringlocation = customConfig.getCases().getString("DonatCase.Cases." + name + ".location");
                    Location lv = fromString(stringlocation);
                    String world = "Undefined";
                    if (lv != null) {
                        if (lv.getWorld() != null) {
                            world = lv.getWorld().getName();
                        }
                        String location = world + ";" + lv.getX() + ";" + lv.getY() + ";" + lv.getZ() + ";" + lv.getPitch() + ";" + lv.getYaw();
                        customConfig.getCases().set("DonatCase.Cases." + name + ".location", location);
                    }
                }
            }
        }
        customConfig.getCases().set("config", "1.0");
        customConfig.saveCases();
        Logger.log("&aConversion successful!");
    }

    public void convertCases() {
        ConfigurationSection cases = customConfig.getConfig().getConfigurationSection("DonatCase.Cases");
        if (cases != null) {
            for (String caseName : cases.getKeys(false)) {
                File folder = new File(Main.instance.getDataFolder(), "cases");
                File caseFile;
                try {
                    caseFile = new File(folder, caseName + ".yml");
                    caseFile.createNewFile();
                    YamlConfiguration caseConfig = YamlConfiguration.loadConfiguration(caseFile);
                    caseConfig.set("case", customConfig.getConfig().getConfigurationSection("DonatCase.Cases." + caseName));
                    String defaultMaterial = customConfig.getConfig().getString("DonatCase.Cases." + caseName + ".Gui.GuiMaterial");
                    String defaultDisplayName = customConfig.getConfig().getString("DonatCase.Cases." + caseName + ".Gui.GuiMaterialName");
                    boolean defaultEnchanted = customConfig.getConfig().getBoolean("DonatCase.Cases." + caseName + ".Gui.GuiMaterialEnchant");
                    List<String> defaultLore = customConfig.getConfig().getStringList("DonatCase.Cases." + caseName + ".Gui.GuiMaterialLore");
                    List<Integer> defaultSlots = new ArrayList<>();
                    defaultSlots.add(0);
                    defaultSlots.add(8);

                    String openMaterial = customConfig.getConfig().getString("DonatCase.Cases." + caseName + ".Gui.GuiOpenCaseMaterial");
                    String openDisplayName = customConfig.getConfig().getString("DonatCase.Cases." + caseName + ".Gui.DisplayName");
                    boolean openEnchanted = customConfig.getConfig().getBoolean("DonatCase.Cases." + caseName + ".Gui.GuiOpenCaseMaterialEnchant");
                    List<String> openLore = customConfig.getConfig().getStringList("DonatCase.Cases." + caseName + ".Gui.Lore");
                    List<Integer> openSlots = new ArrayList<>();
                    openSlots.add(22);

                    caseConfig.set("case.Gui", null);
                    caseConfig.save(caseFile);
                    caseConfig.set("case.Gui.Items.1.DisplayName", defaultDisplayName);
                    caseConfig.set("case.Gui.Items.1.Enchanted", defaultEnchanted);
                    caseConfig.set("case.Gui.Items.1.Lore", defaultLore);
                    caseConfig.set("case.Gui.Items.1.Material", defaultMaterial);
                    caseConfig.set("case.Gui.Items.1.Type", "DEFAULT");
                    caseConfig.set("case.Gui.Items.1.Slots", defaultSlots);

                    caseConfig.set("case.Gui.Items.Open.DisplayName", openDisplayName);
                    caseConfig.set("case.Gui.Items.Open.Enchanted", openEnchanted);
                    caseConfig.set("case.Gui.Items.Open.Lore", openLore);
                    caseConfig.set("case.Gui.Items.Open.Material", openMaterial);
                    caseConfig.set("case.Gui.Items.Open.Type", "OPEN");
                    caseConfig.set("case.Gui.Items.Open.Slots", openSlots);

                    caseConfig.set("case.Gui.Size", 45);
                    caseConfig.save(caseFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        customConfig.getConfig().set("DonatCase.Cases", null);
        customConfig.saveConfig();
    }
    public List<File> getCasesInFolder() {
        List<File> files = new ArrayList<>();
        File directory = new File(Main.instance.getDataFolder(), "cases");
        Collections.addAll(files, Objects.requireNonNull(directory.listFiles()));
        return files;
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

    public String getLocalPlaceholder(String string) {
        Pattern pattern = Pattern.compile("%(.*?)%");
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            int startIndex = string.indexOf("%") + 1;
            int endIndex = string.lastIndexOf("%");
            return string.substring(startIndex, endIndex);
        } else {
            return "null";
        }
    }

    public ItemStack createItem(Material ma, int amount, int data, String dn, boolean enchant, String[] rgb) {
        return createItem(ma, data, amount, dn, null, enchant, rgb);
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
                                throw new RuntimeException(e1);
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
                            throw new RuntimeException(e1);
                        }
                    }

                }
            }

        }

        return color;
    }

    public ItemStack getPlayerHead(String player, String displayName, List<String> lore) {
        Material type = Material.getMaterial("SKULL_ITEM");
        ItemStack item;
        if (type == null) {
            item = new ItemStack(Objects.requireNonNull(Material.getMaterial("PLAYER_HEAD")));
        } else {
            item = new ItemStack(Objects.requireNonNull(Material.getMaterial("SKULL_ITEM")), 1, (short) 3);
        }
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwner(player);
            meta.setDisplayName(rc(displayName));
            if(lore != null) {
                meta.setLore(rc(lore));
            }
            item.setItemMeta(meta);
        }

        return item;
    }
    public ItemStack getBASE64Skull(String url, String displayName, List<String> lore) {
        ItemStack item = MojangHeads.getSkull("http://textures.minecraft.net/texture/" + url);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(rc(displayName));
            if (lore != null) {
                meta.setLore(rc(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }
    public ItemStack getBASE64Skull(String url, String displayName) {
        return getBASE64Skull(url, displayName, null);
    }

    public ItemStack getWinItem(String c, String winGroup, Player player) {
        String winGroupId = Case.getWinGroupId(c, winGroup);
        MaterialType materialType = t.getMaterialType(winGroupId);
        String winGroupDisplayName = t.rc(Case.getWinGroupDisplayName(c, winGroup));
        String[] rgb = Case.getWinGroupRgb(c, winGroup);
        if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            winGroupId = PAPISupport.setPlaceholders(player, winGroupId);
            winGroupDisplayName = PAPISupport.setPlaceholders(player, winGroupDisplayName);
        }
        boolean winGroupEnchant = Case.getWinGroupEnchant(c, winGroup);
        Material material;
        ItemStack winItem;
        if (!winGroupId.contains(":")) {
            material = Material.getMaterial(winGroupId);
            if (material == null) {
                material = Material.STONE;
            }
            if(material != Material.AIR) {
                winItem = t.createItem(material, 1, -1, winGroupDisplayName, winGroupEnchant, rgb);
            } else {
                winItem = new ItemStack(Material.AIR);
                ItemMeta meta = winItem.getItemMeta();
                winItem.setItemMeta(meta);
            }
        } else {
            if (materialType == MaterialType.HEAD) {
                String[] parts = winGroupId.split(":");
                winItem = t.getPlayerHead(parts[1], winGroupDisplayName, null);
            } else if (materialType == MaterialType.HDB) {
                String[] parts = winGroupId.split(":");
                String id = parts[1];
                if (Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                    winItem = HeadDatabaseSupport.getSkull(id, winGroupDisplayName, null);
                } else {
                    winItem = new ItemStack(Material.STONE);
                }
            } else if (materialType == MaterialType.CH) {
                String[] parts = winGroupId.split(":");
                String category = parts[1];
                String id = parts[2];
                if (Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                    winItem = CustomHeadSupport.getSkull(category, id, winGroupDisplayName, null);
                } else {
                    winItem = new ItemStack(Material.STONE);
                }
            } else if (materialType == MaterialType.IA) {
                String[] parts = winGroupId.split(":");
                String namespace = parts[1];
                String id = parts[2];
                if(instance.getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
                    winItem = ItemsAdderSupport.getItem(namespace + ":" + id, winGroupDisplayName, null);
                } else {
                    winItem = new ItemStack(Material.STONE);
                    instance.getLogger().warning("ItemsAdder not loaded! Group: " + winGroup + " Case: " + c);
                }
            }
            else if (materialType == MaterialType.BASE64) {
                String[] parts = winGroupId.split(":");
                String base64 = parts[1];
                winItem = t.getBASE64Skull(base64, winGroupDisplayName);
            } else {
                String[] parts = winGroupId.split(":");
                byte data = -1;
                if(parts[1] != null) {
                    data = Byte.parseByte(parts[1]);
                }
                material = Material.getMaterial(parts[0]);
                if (material == null) {
                    material = Material.STONE;
                }
                winItem = t.createItem(material, data, 1, winGroupDisplayName, winGroupEnchant, rgb);
            }
        }
        return winItem;
    }

    public ItemStack createItem(Material ma, int data, int amount, String dn, List<String> lore, boolean enchant, String[] rgb) {
        ItemStack item;
        if(data == -1) {
            item = new ItemStack(ma, amount);
        } else if (Bukkit.getVersion().contains("1.12.2")){
            item = new ItemStack(ma, amount, (short) 1, (byte) data);
        } else {
            item = new ItemStack(ma, amount);
        }
        if(enchant && !ma.equals(Material.AIR)) {
            item.addUnsafeEnchantment(Enchantment.LURE, 1);
        }
        ItemMeta m = item.getItemMeta();
        if(m != null) {
            if (dn != null) {
                m.setDisplayName(rc(dn));
            }

            if (lore != null) {

                m.setLore(this.rc(lore));
            }
            if (enchant && !ma.equals(Material.AIR)) {
                m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            item.setItemMeta(m);

            if (rgb != null && m instanceof LeatherArmorMeta) {
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) m;
                int r = Integer.parseInt(rgb[0]);
                int g = Integer.parseInt(rgb[1]);
                int b = Integer.parseInt(rgb[2]);
                leatherArmorMeta.setColor(Color.fromRGB(r, g, b));
                item.setItemMeta(leatherArmorMeta);
            }
        }
        return item;
    }
    public MaterialType getMaterialType(String material) {
        if (material.contains(":")) {
            if (material.startsWith("HEAD")) {
                return MaterialType.HEAD;
            } else if (material.startsWith("HDB")) {
                return MaterialType.HDB;
            } else if (material.startsWith("CH")) {
                return MaterialType.CH;
            } else if (material.startsWith("BASE64")) {
                return MaterialType.BASE64;
            } else if(material.startsWith("IA")) {
                return MaterialType.IA;
            }
        }
        return MaterialType.DEFAULT;
    }
    public List<Integer> getOpenMaterialSlots(String c) {
        List<Integer> slots = new ArrayList<>();
        for (String item : casesConfig.getCase(c).getConfigurationSection("case.Gui.Items").getKeys(false)) {
            if(casesConfig.getCase(c).getString("case.Gui.Items." + item + ".Type").equalsIgnoreCase("OPEN")) {
                List<Integer> list = casesConfig.getCase(c).getIntegerList("case.Gui.Items." + item + ".Slots");
                slots.addAll(list);
            }
        }
        return slots;
    }

    public String hex(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public int getPluginVersion(String version) {
        StringBuilder builder = new StringBuilder();
        version = version.replaceAll("\\.", "");
        if(version.length() < 4) {
            builder.append(version).append("0");
        } else {
            builder.append(version);
        }
        return Integer.parseInt(builder.toString());
    }

}
