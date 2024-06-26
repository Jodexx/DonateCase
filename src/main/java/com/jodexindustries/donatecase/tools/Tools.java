package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.data.*;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.BukkitArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.PacketArmorStandCreator;
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Tools {

    public static ArmorStandCreator createArmorStand() {
        if(Case.getInstance().isUsePackets()) {
            return new PacketArmorStandCreator();
        } else {
            return new BukkitArmorStandCreator();
        }
    }

    public static void launchFirework(Location location) {
        Random r = new Random();
        World world = location.getWorld();
        if(world == null) return;

        Firework firework = (Firework) world.spawnEntity(location.subtract(new Vector(0.0, 0.5, 0.0)), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        Color[] color = new Color[]{Color.RED, Color.AQUA, Color.GREEN, Color.ORANGE, Color.LIME, Color.BLUE, Color.MAROON, Color.WHITE};
        meta.addEffect(FireworkEffect.builder().flicker(false).with(Type.BALL).trail(false).withColor(color[r.nextInt(color.length)], color[r.nextInt(color.length)], color[r.nextInt(color.length)]).build());
        firework.setFireworkMeta(meta);
        firework.setMetadata("case", new FixedMetadataValue(DonateCase.instance, "case"));
        firework.detonate();
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

    @NotNull
    public static String rc(String t) {
        if(t != null) {
            return hex(t);
        } else {
            return rc("&cMessage not found!");
        }
    }

    @NotNull
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
        return createItem(ma, amount, data, dn, null, enchant, rgb, -1);
    }

    public static Color parseColor(String s) {

        Color color;
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
                return getColor(s);

            }
        } else {
            // Name
            return getColor(s);
        }

        return color;
    }
    public static Color getColor(String color) {
        Field[] fields = Color.class.getFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())
                    && field.getType() == Color.class) {

                if (field.getName().equalsIgnoreCase(color)) {
                    try {
                        return (Color) field.get(null);
                    } catch (IllegalArgumentException | IllegalAccessException e1) {
                        throw new RuntimeException(e1);
                    }
                }

            }
        }
        return null;
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
        String[] materialParts = id.split(":");

        MaterialType materialType = getMaterialType(materialParts[0]);
        ItemStack winItem;
        switch (materialType) {
            case HEAD:
                winItem = getPlayerHead(materialParts[1], displayName, null);
                break;
            case HDB:
                winItem = HeadDatabaseSupport.getSkull(materialParts[1], displayName, null);
                break;
            case CH:
                winItem = CustomHeadSupport.getSkull(materialParts[1], materialParts[2], displayName, null);
                break;
            case IA:
                winItem = ItemsAdderSupport.getItem(materialParts[1] + ":" + materialParts[2], displayName, null);
                break;
            case BASE64:
                winItem = getBASE64Skull(materialParts[1], displayName);
                break;
            default:
                byte data = (materialParts.length > 1) ? Byte.parseByte(materialParts[1]) : -1;
                winItem = createItem(Material.getMaterial(materialParts[0]), 1, data, displayName, enchanted, rgb);
                break;
        }

        return winItem;
    }

    public static ItemStack createItem(Material ma, int amount, int data, String dn, List<String> lore, boolean enchant, String[] rgb, int modelData) {
        ItemStack item;
        if(ma == null) return new ItemStack(Material.STONE);
        if(data == -1) {
            item = new ItemStack(ma, amount);
        } else if (Bukkit.getVersion().contains("1.12.2")) {
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

    /**
     * Parse material type from string
     * @param material String, to be parsed
     * @return Parsed enum
     */
    public static MaterialType getMaterialType(String material) {
        switch (material) {
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
        return MaterialType.DEFAULT;
    }

    public static List<Integer> getOpenMaterialSlots(String c) {
        List<Integer> slots = new ArrayList<>();
        CaseData caseData = Case.getCase(c);
        if(caseData == null) return slots;
        GUI gui = caseData.getGui();
        for (GUI.Item item : gui.getItems().values()) {
            String type = item.getType();
            if(type.startsWith("OPEN")) {
                slots.addAll(item.getSlots());
            }
        }
        return slots;
    }

    @NotNull
    public static Map<List<Integer>, String> getOpenMaterialItemsBySlots(String c) {
        Map<List<Integer>, String> map = new HashMap<>();
        CaseData caseData = Case.getCase(c);
        if(caseData == null) return map;
        GUI gui = caseData.getGui();
        if(gui == null) return map;

        for (GUI.Item item : gui.getItems().values()) {
            String type = item.getType();
            if(type.startsWith("OPEN")) {
                List<Integer> slots = item.getSlots();
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

    /**
     * Format string with Bukkit ChatColor and hex
     * @param message String, to be formated
     * @return String with format
     */
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

    /**
     * Parse version from string
     * @param version String, to be parsed
     * @return numbered version.
     * <br>
     * Example: <br>
     * Input text: <code>2.2.2</code> <br>
     * Output: <code>2220</code> <br>
     * Input text: <code>2.2.2.2</code> <br>
     * Output: <code>2222</code>
     */
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

    public static boolean isHasCommandForSender(CommandSender sender, Map<String, List<Map<String, SubCommand>>> addonsMap, String addon) {
        List<Map<String, SubCommand>> commands = addonsMap.get(addon);
        return isHasCommandForSender(sender, commands);
    }

    public static boolean isHasCommandForSender(CommandSender sender, Map<String, List<Map<String, SubCommand>>> addonsMap) {
        for (String addon : addonsMap.keySet()) {
            List<Map<String, SubCommand>> commands = addonsMap.get(addon);
            if(isHasCommandForSender(sender, commands)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check sender for permission to executing commands
     * Checks only if sender has permission for one or more commands, not all
     * @param sender Player or Console
     * @param commands List of commands, that loaded in DonateCase
     * @return true, if sender has permission
     */
    public static boolean isHasCommandForSender(CommandSender sender, List<Map<String, SubCommand>> commands) {
        for (Map<String, SubCommand> command : commands) {
            for (SubCommand subCommand : command.values()) {
                if (hasPermissionForCommand(sender, subCommand)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check sender for permission to executing command
     * @param sender Player or Console
     * @param subCommand Sub command ,that loaded in DonateCase
     * @return true, if sender has permission
     */
    private static boolean hasPermissionForCommand(CommandSender sender, SubCommand subCommand) {
        SubCommandType type = subCommand.getType();

        if (sender.hasPermission("donatecase.admin")) {
            return type == SubCommandType.ADMIN || type == SubCommandType.MODER || type == SubCommandType.PLAYER || type == null;
        } else if (sender.hasPermission("donatecase.mod")) {
            return type == SubCommandType.MODER || type == SubCommandType.PLAYER || type == null;
        } else if (sender.hasPermission("donatecase.player")) {
            return type == SubCommandType.PLAYER || type == null;
        }

        return false;
    }


    /**
     * Parse EulerAngle from string
     * @param angleString String to be parsed
     * @return Alright, its just default Bukkit EulerAngle
     */
    public static EulerAngle getEulerAngleFromString(String angleString) {
        String[] angle;
        if (angleString == null) return new EulerAngle(0,0,0);
        angle = angleString.replaceAll(" ", "").split(",");
        try {
            double x = Double.parseDouble(angle[0]);
            double y = Double.parseDouble(angle[1]);
            double z = Double.parseDouble(angle[2]);
            return new EulerAngle(x, y, z);
        } catch (NumberFormatException ignored) {
            return new EulerAngle(0,0,0);
        }
    }

    /**
     * Get euler angle from Animations.yml
     * @param path Path, used like animation name
     * @return EulerAngle, that used in animations
     */
    public static ArmorStandEulerAngle getArmorStandEulerAngle(String path) {
         ConfigurationSection section = Case.getCustomConfig().getAnimations().getConfigurationSection(path);
         if(section == null) {
             EulerAngle angle = new EulerAngle(0,0,0);
             return new ArmorStandEulerAngle(angle, angle, angle, angle, angle, angle);
         }
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
