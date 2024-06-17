package com.jodexindustries.donatecase.gui;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.MaterialType;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.CustomHeadSupport;
import com.jodexindustries.donatecase.tools.support.HeadDatabaseSupport;
import com.jodexindustries.donatecase.tools.support.ItemsAdderSupport;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jodexindustries.donatecase.api.Case.playersGui;

public class CaseGui {
    private Inventory inventory;

    public CaseGui(Player p, CaseData caseData) {
        String title = caseData.getCaseTitle();
        String c = caseData.getCaseType();
        YamlConfiguration configCase = Case.getCasesConfig().getCase(c);
        int size = configCase.getInt("case.Gui.Size", 45);
        if(size >= 9 && size <= 54 && size % 9 == 0) {
            inventory = Bukkit.createInventory(null, configCase.getInt("case.Gui.Size", 45), Tools.rc(title));
            ConfigurationSection items = configCase.getConfigurationSection("case.Gui.Items");
            Bukkit.getScheduler().runTaskAsynchronously(Case.getInstance(), () -> {
                List<CaseData.HistoryData> globalHistoryData = Case.getSortedHistoryData();
                if (items != null) {
                    for (String item : items.getKeys(false)) {
                        String material = configCase.getString("case.Gui.Items." + item + ".Material", "STONE");
                        String displayName = configCase.getString("case.Gui.Items." + item + ".DisplayName", "None");
                        if (Case.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                            displayName = PAPISupport.setPlaceholders(p, displayName);
                        }
                        boolean enchanted = configCase.getBoolean("case.Gui.Items." + item + ".Enchanted");
                        String itemType = configCase.getString("case.Gui.Items." + item + ".Type", "DEFAULT");
                        List<String> lore = Tools.rc(configCase.getStringList("case.Gui.Items." + item + ".Lore"));
                        int modelData = configCase.getInt("case.Gui.Items." + item + ".ModelData", -1);
                        String[] rgb = null;
                        List<String> pLore = new ArrayList<>();
                        for (String line : lore) {
                            if (Case.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                line = PAPISupport.setPlaceholders(p, line);
                            }
                            pLore.add(line);
                        }
                        lore = Tools.rc(pLore);
                        if (material.toUpperCase().startsWith("LEATHER_")) {
                            String rgbString = configCase.getString("case.Gui.Items." + item + ".Rgb");
                            if (rgbString != null) {
                                rgb = rgbString.replace(" ", "").split(",");
                            }
                        }
                        if (itemType.startsWith("HISTORY")) {
                            String[] typeArgs = itemType.split("-");
                            int index = Integer.parseInt(typeArgs[1]);
                            String caseType = (typeArgs.length >= 3) ? typeArgs[2] : c;
                            boolean isGlobal = caseType.equalsIgnoreCase("GLOBAL");

                            CaseData historyCaseData = isGlobal ? null : Case.getCase(caseType);
                            if (historyCaseData == null && !isGlobal) {
                                Case.getInstance().getLogger().warning("Case " + caseType + " HistoryData is null!");
                                continue;
                            }
                            if (!isGlobal) {
                                historyCaseData = historyCaseData.clone();
                            }

                            CaseData.HistoryData data = null;
                            if (isGlobal) {
                                if (globalHistoryData.size() <= index) continue;
                                data = globalHistoryData.get(index);
                            } else {
                                if (!Case.getInstance().sql) {
                                    data = historyCaseData.getHistoryData()[index];
                                } else {
                                    List<CaseData.HistoryData> dbData = Case.sortHistoryDataByCase(globalHistoryData, caseType);
                                    if (!dbData.isEmpty()) {
                                        if (dbData.size() <= index) continue;
                                        data = dbData.get(index);
                                    }
                                }
                            }
                            if (data == null) continue;
                            if (isGlobal) historyCaseData = Case.getCase(data.getCaseType());
                            if (historyCaseData == null) continue;

                            CaseData.Item historyItem = historyCaseData.getItem(data.getItem());
                            if (historyItem == null) continue;
                            material = configCase.getString("case.Gui.Items." + item + ".Material", "HEAD:" + data.getPlayerName());
                            if (material.equalsIgnoreCase("DEFAULT")) {
                                material = historyItem.getMaterial().getId();
                            }
                            CaseData.Item.RandomAction randomAction = historyItem.getRandomAction(data.getAction());
                            String randomActionDisplayname = randomAction != null ? randomAction.getDisplayName() : "";
                            DateFormat formatter = new SimpleDateFormat(Case.getCustomConfig().getConfig().getString("DonatCase.DateFormat", "dd.MM HH:mm:ss"));
                            String dateFormatted = formatter.format(new Date(data.getTime()));
                            String groupDisplayName = data.getItem() != null ? historyItem.getMaterial().getDisplayName() : "open_case_again";
                            String[] template = {"%action%:" + data.getAction(), "%actiondisplayname%:" + randomActionDisplayname, "%casedisplayname%:" + historyCaseData.getCaseDisplayName(), "%casename%:" + data.getCaseType(), "%casetitle%:" + historyCaseData.getCaseTitle(), "%time%:" + dateFormatted, "%group%:" + data.getGroup(), "%player%:" + data.getPlayerName(), "%groupdisplayname%:" + groupDisplayName};
                            displayName = Tools.rt(displayName, template);
                            lore = Tools.rt(lore, template);
                        }

                        List<String> slots = new ArrayList<>();
                        if (configCase.isList("case.Gui.Items." + item + ".Slots")) {
                            List<String> temp = configCase.getStringList("case.Gui.Items." + item + ".Slots");
                            for (String slot : temp) {
                                String[] values = slot.split("-", 2);
                                if (values.length == 2) {
                                    for (int i = Integer.parseInt(values[0]); i <= Integer.parseInt(values[1]); i++) {
                                        slots.add(String.valueOf(i));
                                    }
                                } else {
                                    slots.add(slot);
                                }
                            }
                        } else {
                            String[] slotArgs = configCase.getString("case.Gui.Items." + item + ".Slots", "0-0").split("-");
                            int range1 = Integer.parseInt(slotArgs[0]);
                            int range2 = range1;
                            if (slotArgs.length >= 2) {
                                range2 = Integer.parseInt(slotArgs[1]);
                            }
                            slots.addAll(IntStream.rangeClosed(range1, range2).mapToObj(String::valueOf).collect(Collectors.toList()));
                        }

                        ItemStack itemStack = getItem(material, displayName, lore, c, p, enchanted, rgb, modelData);
                        for (String slot : slots) {
                            inventory.setItem(Integer.parseInt(slot), itemStack);
                        }
                    }
                }
            });
            p.openInventory(inventory);
        } else {
            Tools.msg(p, "&cSomething wrong! Contact with server administrator!");
            Case.getInstance().getLogger().warning("Wrong GUI size! Use: 9, 18, 27, 36, 45, 54");
            playersGui.remove(p.getUniqueId());
        }
    }
    public Inventory getInventory() {
        return inventory;
    }
    private ItemStack getItem(String material, String displayName, List<String> lore, String c, Player p, boolean enchanted, String[] rgb, int modelData) {
        List<String> newLore = new ArrayList<>();
        if (lore != null) {
            for (String string : lore) {
                String placeholder = Tools.getLocalPlaceholder(string);
                if (placeholder.startsWith("keys")) {
                    String caseName = c;
                    if (placeholder.startsWith("keys_")) {
                        String[] parts = placeholder.split("_");
                        if (parts.length >= 2) {
                            caseName = parts[1];
                        }
                    }
                    newLore.add(string.replace("%" + placeholder + "%", String.valueOf(Case.getKeys(caseName, p.getName()))));
                } else {
                    newLore.add(string);
                }
            }
        }

        String[] materialParts = material.split(":");
        MaterialType materialType = Tools.getMaterialType(materialParts[0]);

        if (materialType == null) {
            Case.getInstance().getLogger().warning("Material \"" + materialParts[0] + "\" not found! Case: " + c);
            return new ItemStack(Material.STONE);
        }

        switch (materialType) {
            case HEAD:
                return Tools.getPlayerHead(materialParts[1], displayName, Tools.rt(newLore, "%case%:" + c));
            case HDB:
                return HeadDatabaseSupport.getSkull(materialParts[1], displayName, Tools.rt(newLore, "%case%:" + c));
            case CH:
                return CustomHeadSupport.getSkull(materialParts[1], materialParts[2], displayName, Tools.rt(newLore, "%case%:" + c));
            case IA:
                return ItemsAdderSupport.getItem(materialParts[1] + ":" + materialParts[2], displayName, Tools.rt(newLore, "%case%:" + c));
            case BASE64:
                return Tools.getBASE64Skull(materialParts[1], displayName, Tools.rt(newLore, "%case%:" + c));
            default:
                byte data = -1;
                if(materialParts.length > 1)  {
                    try {
                        data = Byte.parseByte(materialParts[1]);
                    } catch (NumberFormatException ignored) {}
                }
                return Tools.createItem(Material.getMaterial(materialParts[0]), data, 1, displayName, Tools.rt(newLore, "%case%:" + c), enchanted, rgb, modelData);
        }
    }

}
