package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.data.MaterialType;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.BukkitArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.PacketArmorStandCreator;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.tools.support.CustomHeadSupport;
import com.jodexindustries.donatecase.tools.support.HeadDatabaseSupport;
import com.jodexindustries.donatecase.tools.support.ItemsAdderSupport;
import day.dean.skullcreator.SkullCreator;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
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

import static com.jodexindustries.donatecase.DonateCase.*;

public class Tools {


    public static CaseData.Item getRandomGroup(CaseData data) {
        Random random = new Random();
        int maxChance = 0;
        int from = 0;
        for (String item : data.clone().getItems().keySet()) {
            maxChance += data.clone().getItem(item).getChance();
        }
        int rand = random.nextInt(maxChance);

        for (String item : data.clone().getItems().keySet()) {
            int itemChance = data.clone().getItem(item).getChance();
            if (from <= rand && rand < from + itemChance) {
                return data.clone().getItem(item);
            }
            from += itemChance;
        }

        return null;
    }
    public ArmorStandCreator createArmorStand() {
        if(instance.isUsePackets()) {
            return new PacketArmorStandCreator();
        } else {
            return new BukkitArmorStandCreator();
        }
    }

    public void launchFirework(Location l) {
        Random r = new Random();
        Firework fw = (Firework) Objects.requireNonNull(l.getWorld()).spawnEntity(l.subtract(new Vector(0.0, 0.5, 0.0)), EntityType.FIREWORK);
        FireworkMeta meta = fw.getFireworkMeta();
        Color[] c = new Color[]{Color.RED, Color.AQUA, Color.GREEN, Color.ORANGE, Color.LIME, Color.BLUE, Color.MAROON, Color.WHITE};
        meta.addEffect(FireworkEffect.builder().flicker(false).with(Type.BALL).trail(false).withColor(c[r.nextInt(c.length)], c[r.nextInt(c.length)], c[r.nextInt(c.length)]).build());
        fw.setFireworkMeta(meta);
        fw.setMetadata("case", new FixedMetadataValue(DonateCase.instance, "case"));
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
            msg_(s, DonateCase.customConfig.getLang().getString("Prefix") + msg);
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
            if(s != null) {
                int l = s.split(":")[0].length();
                if (text != null) {
                    text = text.replace(s.substring(0, l), s.substring(l + 1));
                } else {
                    text = rc("&cMessage not found! Update lang file!");
                }
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
                File folder = new File(DonateCase.instance.getDataFolder(), "cases");
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
        File directory = new File(DonateCase.instance.getDataFolder(), "cases");
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
        return createItem(ma, data, amount, dn, null, enchant, rgb, -1);
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
        ItemStack item = SkullCreator.itemFromUrl("http://textures.minecraft.net/texture/" + url);
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

    public ItemStack getCaseItem(String displayName, String id, boolean enchanted, String[] rgb) {
        MaterialType materialType = t.getMaterialType(id);
        Material material;
        ItemStack winItem;
        if (!id.contains(":")) {
            material = Material.getMaterial(id);
            if (material == null) {
                material = Material.STONE;
            }
            if(material != Material.AIR) {
                winItem = t.createItem(material, 1, -1, displayName, enchanted, rgb);
            } else {
                winItem = new ItemStack(Material.AIR);
            }
        } else {
            if (materialType == MaterialType.HEAD) {
                String[] parts = id.split(":");
                winItem = t.getPlayerHead(parts[1], displayName, null);
            } else if (materialType == MaterialType.HDB) {
                String[] parts = id.split(":");
                String skullId = parts[1];
                winItem = HeadDatabaseSupport.getSkull(skullId, displayName, null);
            } else if (materialType == MaterialType.CH) {
                String[] parts = id.split(":");
                String category = parts[1];
                String skullId = parts[2];
                winItem = CustomHeadSupport.getSkull(category, skullId, displayName, null);
            } else if (materialType == MaterialType.IA) {
                String[] parts = id.split(":");
                String namespace = parts[1];
                String skullId = parts[2];
                winItem = ItemsAdderSupport.getItem(namespace + ":" + skullId, displayName, null);
            }
            else if (materialType == MaterialType.BASE64) {
                String[] parts = id.split(":");
                String base64 = parts[1];
                winItem = t.getBASE64Skull(base64, displayName);
            } else {
                String[] parts = id.split(":");
                byte data = -1;
                if(parts[1] != null) {
                    data = Byte.parseByte(parts[1]);
                }
                material = Material.getMaterial(parts[0]);
                if (material == null) {
                    material = Material.STONE;
                }
                winItem = t.createItem(material, data, 1, displayName, enchanted, rgb);
            }
        }
        return winItem;
    }

    public ItemStack createItem(Material ma, int data, int amount, String dn, List<String> lore, boolean enchant, String[] rgb, int modeldata) {
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
            if(modeldata != -1) {
                m.setCustomModelData(modeldata);
            }
            if (!ma.equals(Material.AIR)) {
                m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                m.addItemFlags(ItemFlag.HIDE_DYE);
                m.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
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
            String prefix = material.substring(0, material.indexOf(":"));
            switch (prefix) {
                case "HEAD":
                    return MaterialType.HEAD;
                case "HDB":
                    return MaterialType.HDB;
                case "CH":
                    return MaterialType.CH;
                case "BASE64":
                    return MaterialType.BASE64;
                case "IA":
                    return MaterialType.IA;
                default:
                    break;
            }
        }
        return MaterialType.DEFAULT;
    }

    public List<Integer> getOpenMaterialSlots(String c) {
        List<Integer> slots = new ArrayList<>();
        for (String item : casesConfig.getCase(c).getConfigurationSection("case.Gui.Items").getKeys(false)) {
            if(casesConfig.getCase(c).getString("case.Gui.Items." + item + ".Type", "").startsWith("OPEN")) {
                List<Integer> list = casesConfig.getCase(c).getIntegerList("case.Gui.Items." + item + ".Slots");
                slots.addAll(list);
            }
        }
        return slots;
    }
    public Map<List<Integer>, String> getOpenMaterialItemsBySlots(String c) {
        Map<List<Integer>, String> map = new HashMap<>();
        ConfigurationSection section = casesConfig.getCase(c).getConfigurationSection("case.Gui.Items");
        for (String item : section.getKeys(false)) {
            String type = section.getString(item + ".Type", "");
            if(type.startsWith("OPEN")) {
                List<Integer> slots = section.getIntegerList(item + ".Slots");
                type = type.contains("_") ? type.split("_")[1] : c;
                map.put(slots, type);
            }
        }
        return map;
    }

    public String getOpenMaterialTypeByMapBySlot(String c, int slot) {
        for (Map.Entry<List<Integer>, String> entry : getOpenMaterialItemsBySlots(c).entrySet()) {
            if(entry.getKey().contains(slot)) {
                return entry.getValue();
            }
        }
        return null;
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
    public boolean isHasCommandForSender(CommandSender sender, Map<String, List<Map<String, SubCommand>>> addonsMap) {
        for (String addon : addonsMap.keySet()) {
            List<Map<String, SubCommand>> commands = addonsMap.get(addon);
            for (Map<String, SubCommand> command : commands) {
                for (String commandName : command.keySet()) {
                    SubCommand subCommand = command.get(commandName);
                    if(sender.hasPermission("donatecase.admin")) {
                        if (subCommand.getType() == SubCommandType.ADMIN || subCommand.getType() == SubCommandType.MODER || subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                            return true;
                        }
                    } else if (sender.hasPermission("donatecase.mod") && !sender.hasPermission("donatecase.admin")) {
                        if (subCommand.getType() == SubCommandType.MODER || subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                            return true;
                        }
                    } else if (sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
                        if (subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public boolean isHasCommandForSender(CommandSender sender, Map<String, List<Map<String, SubCommand>>> addonsMap, String addon) {
            List<Map<String, SubCommand>> commands = addonsMap.get(addon);
            for (Map<String, SubCommand> command : commands) {
                for (String commandName : command.keySet()) {
                    SubCommand subCommand = command.get(commandName);
                    if (sender.hasPermission("donatecase.admin")) {
                        if (subCommand.getType() == SubCommandType.ADMIN || subCommand.getType() == SubCommandType.MODER || subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                            return true;
                        }
                    } else if (sender.hasPermission("donatecase.mod") && !sender.hasPermission("donatecase.admin")) {
                        if (subCommand.getType() == SubCommandType.MODER || subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                            return true;
                        }
                    } else if (sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
                        if (subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                            return true;
                        }
                    }
                }
            }
        return false;
    }

}
