package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    public static ArmorStandCreator createArmorStand() {
        if(Case.getInstance().isUsePackets()) {
            return new PacketArmorStandCreator();
        } else {
            return new BukkitArmorStandCreator();
        }
    }

    public static void launchFirework(Location l) {
        Random r = new Random();
        Firework fw = (Firework) Objects.requireNonNull(l.getWorld()).spawnEntity(l.subtract(new Vector(0.0, 0.5, 0.0)), EntityType.FIREWORK);
        FireworkMeta meta = fw.getFireworkMeta();
        Color[] c = new Color[]{Color.RED, Color.AQUA, Color.GREEN, Color.ORANGE, Color.LIME, Color.BLUE, Color.MAROON, Color.WHITE};
        meta.addEffect(FireworkEffect.builder().flicker(false).with(Type.BALL).trail(false).withColor(c[r.nextInt(c.length)], c[r.nextInt(c.length)], c[r.nextInt(c.length)]).build());
        fw.setFireworkMeta(meta);
        fw.setMetadata("case", new FixedMetadataValue(DonateCase.instance, "case"));
        fw.detonate();
    }


    public static void msg(CommandSender s, String msg) {
        if (s != null) {
            msgRaw(s, Case.getCustomConfig().getLang().getString("prefix") + msg);
        }
    }

    public static void msgRaw(CommandSender s, String msg) {
        if (s != null) {
            s.sendMessage(rc(msg));
        }
    }

    public static String rc(String t) {
        if(t != null) {
            return hex(t);
        } else {
            return rc("&cMessage not found!");
        }
    }

    public static String rt(String text, String... repl) {
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

    public static List<String> rt(List<String> text, String... repl) {
        ArrayList<String> rt = new ArrayList<>();

        for (String t : text) {
            rt.add(rt(t, repl));
        }

        return rt;
    }
    public static void convertCasesLocation() {
        ConfigurationSection cases_ = Case.getCustomConfig().getCases().getConfigurationSection("DonatCase.Cases");
        if(cases_ != null) {
            for (String name : cases_.getValues(false).keySet()) {
                if (Case.getCustomConfig().getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                    return;
                } else {
                    String locationString = Case.getCustomConfig().getCases().getString("DonatCase.Cases." + name + ".location");
                    Location lv = fromString(locationString);
                    String world = "Undefined";
                    if (lv != null) {
                        if (lv.getWorld() != null) {
                            world = lv.getWorld().getName();
                        }
                        String location = world + ";" + lv.getX() + ";" + lv.getY() + ";" + lv.getZ() + ";" + lv.getPitch() + ";" + lv.getYaw();
                        Case.getCustomConfig().getCases().set("DonatCase.Cases." + name + ".location", location);
                    }
                }
            }
        }
        Case.getCustomConfig().getCases().set("config", "1.0");
        Case.getCustomConfig().saveCases();
        Logger.log("&aConversion successful!");
    }

    public static void convertCases() {
        ConfigurationSection cases = Case.getCustomConfig().getConfig().getConfigurationSection("DonatCase.Cases");
        if (cases != null) {
            for (String caseName : cases.getKeys(false)) {
                File folder = new File(Case.getInstance().getDataFolder(), "cases");
                File caseFile;
                try {
                    caseFile = new File(folder, caseName + ".yml");
                    caseFile.createNewFile();
                    YamlConfiguration caseConfig = YamlConfiguration.loadConfiguration(caseFile);
                    caseConfig.set("case", Case.getCustomConfig().getConfig().getConfigurationSection("DonatCase.Cases." + caseName));
                    String defaultMaterial = Case.getCustomConfig().getConfig().getString("DonatCase.Cases." + caseName + ".Gui.GuiMaterial");
                    String defaultDisplayName = Case.getCustomConfig().getConfig().getString("DonatCase.Cases." + caseName + ".Gui.GuiMaterialName");
                    boolean defaultEnchanted = Case.getCustomConfig().getConfig().getBoolean("DonatCase.Cases." + caseName + ".Gui.GuiMaterialEnchant");
                    List<String> defaultLore = Case.getCustomConfig().getConfig().getStringList("DonatCase.Cases." + caseName + ".Gui.GuiMaterialLore");
                    List<Integer> defaultSlots = new ArrayList<>();
                    defaultSlots.add(0);
                    defaultSlots.add(8);

                    String openMaterial = Case.getCustomConfig().getConfig().getString("DonatCase.Cases." + caseName + ".Gui.GuiOpenCaseMaterial");
                    String openDisplayName = Case.getCustomConfig().getConfig().getString("DonatCase.Cases." + caseName + ".Gui.DisplayName");
                    boolean openEnchanted = Case.getCustomConfig().getConfig().getBoolean("DonatCase.Cases." + caseName + ".Gui.GuiOpenCaseMaterialEnchant");
                    List<String> openLore = Case.getCustomConfig().getConfig().getStringList("DonatCase.Cases." + caseName + ".Gui.Lore");
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
        Case.getCustomConfig().getConfig().set("DonatCase.Cases", null);
        Case.getCustomConfig().saveConfig();
    }

    public static void convertLanguage(YamlConfiguration config) {
        config.set("config", "2.6");

        String prefix = config.getString("Prefix");
        config.set("Prefix", null);
        config.set("prefix", prefix);

        String noPermission = config.getString("NoPermission");
        config.set("NoPermission", null);
        config.set("no-permission", noPermission);

        String updateCheck = config.getString("UpdateCheck");
        config.set("UpdateCheck", null);
        config.set("new-update", updateCheck);

        String caseNotExist = config.getString("CaseNotExist");
        config.set("CaseNotExist", null);
        config.set("case-does-not-exist", caseNotExist);

        String caseAlreadyHasByName = config.getString("CaseAlreadyHasByName");
        config.set("CaseAlreadyHasByName", null);
        config.set("case-already-exist", caseAlreadyHasByName);

        String hasDonatCase = config.getString("HasDonatCase");
        config.set("HasDonatCase", null);
        config.set("case-already-created", hasDonatCase);

        String addDonatCase = config.getString("AddDonatCase");
        config.set("AddDonatCase", null);
        config.set("case-added", addDonatCase);

        String RemoveDonatCase = config.getString("RemoveDonatCase");
        config.set("RemoveDonatCase", null);
        config.set("case-removed", RemoveDonatCase);

        String blockDontDonatCase = config.getString("BlockDontDonatCase");
        config.set("BlockDontDonatCase", null);
        config.set("block-is-not-case", blockDontDonatCase);

        String giveKeys = config.getString("GiveKeys");
        config.set("GiveKeys", null);
        config.set("keys-given", giveKeys);

        String giveKeysTarget = config.getString("GiveKeysTarget");
        config.set("GiveKeysTarget", null);
        config.set("keys-given-target", giveKeysTarget);

        String setKeys = config.getString("SetKeys");
        config.set("SetKeys", null);
        config.set("keys-sets", setKeys);

        String setKeysTarget = config.getString("SetKeysTarget");
        config.set("SetKeysTarget", null);
        config.set("keys-sets-target", setKeysTarget);

        String clearKeys = config.getString("ClearKeys");
        config.set("ClearKeys", null);
        config.set("keys-cleared", clearKeys);

        String clearAllKeys = config.getString("ClearAllKeys");
        config.set("ClearAllKeys", null);
        config.set("all-keys-cleared", clearAllKeys);

        String destoryDonatCase = config.getString("DestoryDonatCase");
        config.set("DestoryDonatCase", null);
        config.set("case-destroy-disallow", destoryDonatCase);

        String noKey = config.getString("NoKey");
        config.set("NoKey", null);
        config.set("no-keys", noKey);

        String haveOpenCase = config.getString("HaveOpenCase");
        config.set("HaveOpenCase", null);
        config.set("case-opens", haveOpenCase);

        String reloadConfig = config.getString("ReloadConfig");
        config.set("ReloadConfig", null);
        config.set("config-reloaded", reloadConfig);

        String casesList = config.getString("CasesList");
        config.set("CasesList", null);
        config.set("list-of-cases", casesList);

        String NumberFormatException = config.getString("NumberFormatException");
        config.set("NumberFormatException", null);
        config.set("number-format-exception", NumberFormatException);

        List<String> help = config.getStringList("Help");
        config.set("Help", null);
        config.set("help", help);

        String helpAddonsFormatAddonName = config.getString("HelpAddons.Format.AddonName");
        config.set("HelpAddons.Format.AddonName", null);
        config.set("help-addons.format.name", helpAddonsFormatAddonName);

        String helpAddonsFormatAddonDescription = config.getString("HelpAddons.Format.AddonDescription");
        config.set("HelpAddons.Format.AddonDescription", null);
        config.set("help-addons.format.description", helpAddonsFormatAddonDescription);

        String helpAddonsFormatAddonCommand = config.getString("HelpAddons.Format.AddonCommand");
        config.set("HelpAddons.Format.AddonCommand", null);
        config.set("help-addons.format.command", helpAddonsFormatAddonCommand);

        List<String> helpPlayer = config.getStringList("HelpPlayer");
        config.set("HelpPlayer", null);
        config.set("help-player", helpPlayer);

        List<String> myKeys = config.getStringList("MyKeys");
        config.set("MyKeys", null);
        config.set("my-keys", myKeys);

        List<String> playerKeys = config.getStringList("PlayerKeys");
        config.set("PlayerKeys", null);
        config.set("player-keys", playerKeys);

        try {
            config.save(config.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static List<File> getCasesInFolder() {
        List<File> files = new ArrayList<>();
        File directory = new File(Case.getInstance().getDataFolder(), "cases");
        Collections.addAll(files, Objects.requireNonNull(directory.listFiles()));
        return files;
    }
    public static Location fromString(String str) {
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

    public static List<String> rc(List<String> t) {
        ArrayList<String> a = new ArrayList<>();

        for (String s : t) {
            a.add(rc(s));
        }

        return a;
    }

    public static String getLocalPlaceholder(String string) {
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

    public static ItemStack createItem(Material ma, int amount, int data, String dn, boolean enchant, String[] rgb) {
        return createItem(ma, data, amount, dn, null, enchant, rgb, -1);
    }

    public static Color parseColor(String s) {

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

    public static ItemStack getPlayerHead(String player, String displayName, List<String> lore) {
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
    public static ItemStack getBASE64Skull(String url, String displayName, List<String> lore) {
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
    public static ItemStack getBASE64Skull(String url, String displayName) {
        return getBASE64Skull(url, displayName, null);
    }

    @NotNull
    public static ItemStack getCaseItem(String displayName, String id, boolean enchanted, String[] rgb) {
        MaterialType materialType = getMaterialType(id);
        Material material;
        ItemStack winItem;
        if (!id.contains(":")) {
            material = Material.getMaterial(id);
            if (material == null) {
                material = Material.STONE;
            }
            if(material != Material.AIR) {
                winItem = createItem(material, 1, -1, displayName, enchanted, rgb);
            } else {
                winItem = new ItemStack(Material.AIR);
            }
        } else {
            if (materialType == MaterialType.HEAD) {
                String[] parts = id.split(":");
                winItem = getPlayerHead(parts[1], displayName, null);
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
                winItem = getBASE64Skull(base64, displayName);
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
                winItem = createItem(material, data, 1, displayName, enchanted, rgb);
            }
        }
        return winItem;
    }

    public static ItemStack createItem(Material ma, int data, int amount, String dn, List<String> lore, boolean enchant, String[] rgb, int modelData) {
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

                m.setLore(rc(lore));
            }
            if(modelData != -1) {
                m.setCustomModelData(modelData);
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
    public static MaterialType getMaterialType(String material) {
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

    public static List<Integer> getOpenMaterialSlots(String c) {
        List<Integer> slots = new ArrayList<>();
        for (String item : Case.getCasesConfig().getCase(c).getConfigurationSection("case.Gui.Items").getKeys(false)) {
            if(Case.getCasesConfig().getCase(c).getString("case.Gui.Items." + item + ".Type", "").startsWith("OPEN")) {
                List<Integer> list = Case.getCasesConfig().getCase(c).getIntegerList("case.Gui.Items." + item + ".Slots");
                slots.addAll(list);
            }
        }
        return slots;
    }
    public static Map<List<Integer>, String> getOpenMaterialItemsBySlots(String c) {
        Map<List<Integer>, String> map = new HashMap<>();
        ConfigurationSection section = Case.getCasesConfig().getCase(c).getConfigurationSection("case.Gui.Items");
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

    public static String getOpenMaterialTypeByMapBySlot(String c, int slot) {
        for (Map.Entry<List<Integer>, String> entry : getOpenMaterialItemsBySlots(c).entrySet()) {
            if(entry.getKey().contains(slot)) {
                return entry.getValue();
            }
        }
        return null;
    }


    public static String hex(String message) {
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

    public static int getPluginVersion(String version) {
        StringBuilder builder = new StringBuilder();
        version = version.replaceAll("\\.", "");
        if(version.length() < 4) {
            builder.append(version).append("0");
        } else {
            builder.append(version);
        }
        return Integer.parseInt(builder.toString());
    }
    public static boolean isHasCommandForSender(CommandSender sender, Map<String, List<Map<String, SubCommand>>> addonsMap) {
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
    public static boolean isHasCommandForSender(CommandSender sender, Map<String, List<Map<String, SubCommand>>> addonsMap, String addon) {
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
    public static EulerAngle getEulerAngleFromString(String angleString) {
        String[] angle;
        if (angleString == null) return new EulerAngle(0,0,0);
        angle = angleString.replace(" ", "").split(",");
        try {
            double x = Double.parseDouble(angle[0]);
            double y = Double.parseDouble(angle[1]);
            double z = Double.parseDouble(angle[2]);
            return new EulerAngle(x, y, z);
        } catch (NumberFormatException ignored) {
            return new EulerAngle(0,0,0);
        }
    }
    public static ArmorStandEulerAngle getArmorStandEulerAngle(String path) {
         ConfigurationSection section = Case.getCustomConfig().getAnimations().getConfigurationSection(path);
         if(section == null) return new ArmorStandEulerAngle(new EulerAngle(0,0,0), new EulerAngle(0,0,0), new EulerAngle(0,0,0), new EulerAngle(0,0,0), new EulerAngle(0,0,0), new EulerAngle(0,0,0));
         EulerAngle head = getEulerAngleFromString(section.getString("Head"));
         EulerAngle body = getEulerAngleFromString(section.getString("Body"));
         EulerAngle rightArm = getEulerAngleFromString(section.getString("RightArm"));
         EulerAngle leftArm = getEulerAngleFromString(section.getString("LeftArm"));
         EulerAngle rightLeg = getEulerAngleFromString(section.getString("RightLeg"));
         EulerAngle leftLeg = getEulerAngleFromString(section.getString("LeftLeg"));
         return new ArmorStandEulerAngle(head,body,rightArm, leftArm, rightLeg,leftLeg);
    }

    /**
     * This method used in SetKeyCommand, DelKeyCommand and GiveKeyCommand classes
     * Not for API usable
     * @param args tab completion args
     * @return list of completions
     */
    @NotNull
    public static List<String> resolveSDGCompletions(String[] args) {
        List<String> value = new ArrayList<>(Case.getCasesConfig().getCases().keySet());
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(px -> px.startsWith(args[0])).collect(Collectors.toList()));
            return list;
        } else if (args.length >= 3) {
            return new ArrayList<>();
        }
        if (args[args.length - 1].isEmpty()) {
            list = value;
        } else {
            list.addAll(value.stream().filter(tmp -> tmp.startsWith(args[args.length - 1])).collect(Collectors.toList()));
        }
        return list;
    }

}
