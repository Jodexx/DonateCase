package com.jodexindustries.donatecase.gui;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.MaterialType;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.support.CustomHeadSupport;
import com.jodexindustries.donatecase.tools.support.HeadDatabaseSupport;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.jodexindustries.donatecase.dc.Main.casesConfig;
import static com.jodexindustries.donatecase.dc.Main.t;

public class CaseGui {

    public CaseGui(Player p, String c) {
        String title = Case.getCaseTitle(c);
        Inventory inv = Bukkit.createInventory(null, 45, t.rc(title));
        ConfigurationSection items = casesConfig.getCase(c).getConfigurationSection("case.Gui.Items");
        for (String item : items.getKeys(false)) {
            String material = casesConfig.getCase(c).getString("case.Gui.Items." + item + ".Material", "STONE");
            String displayName = casesConfig.getCase(c).getString("case.Gui.Items." + item + ".DisplayName");
            boolean enchanted = casesConfig.getCase(c).getBoolean("case.Gui.Items." + item + ".Enchanted");
            List<Integer> slots = new ArrayList<>();
            if(casesConfig.getCase(c).isList("case.Gui.Items." + item + ".Slots")) {
                slots = casesConfig.getCase(c).getIntegerList("case.Gui.Items." + item + ".Slots");
            } else {
                String[] slotArgs = casesConfig.getCase(c).getString("case.Gui.Items." + item + ".Slots", "0-0").split("-");
                int range1 = Integer.parseInt(slotArgs[0]);
                int range2 = Integer.parseInt(slotArgs[1]);
                for (int i = range1; i <= range2; i++) {
                    slots.add(i);
                }
            }
            List<String> lore = t.rc(casesConfig.getCase(c).getStringList("case.Gui.Items." + item + ".Lore"));
            ItemStack itemStack = getItem(material, displayName, lore, c, p, Case.getKeys(c, p.getName()), enchanted);
            for (Integer slot : slots) {
                inv.setItem(slot, itemStack);
            }
        }
        p.openInventory(inv);
    }
    private ItemStack getItem(String material, String displayName, List<String> lore, String c, Player p, int keys, boolean enchanted) {
        MaterialType materialType = t.getMaterialType(material);
        Material itemMaterial;
        ItemStack item;
        if(!material.contains(":")) {
            itemMaterial = Material.getMaterial(material);
            if (itemMaterial == null) {
                itemMaterial = Material.STONE;
            }
            item = t.createItem(itemMaterial, -1, 1, displayName, t.rt(lore,"%case:" + c, "%keys:" + keys), enchanted);
        } else
        if(materialType == MaterialType.HEAD) {
            String[] parts = material.split(":");
            item = t.getPlayerHead(parts[1], displayName, t.rt(lore,"%case:" + c, "%keys:" + keys));
        } else
        if(materialType == MaterialType.HDB) {
            String[] parts = material.split(":");
            String id = parts[1];
            if(Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                item = HeadDatabaseSupport.getSkull(id, displayName, t.rt(lore, "%case:" + c, "%keys:" + keys));
            } else {
                if(!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    item = t.createItem(Material.STONE, 1, 1, displayName, t.rt(lore, "%case:" + c, "%keys:" + keys), enchanted);
                } else {
                    List<String> pLore = new ArrayList<>();
                    for(String line : lore) {
                        if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                            line = PAPISupport.setPlaceholders(p, line);
                        }
                        pLore.add(line);
                    }
                    item = t.createItem(Material.STONE, 1, 1, displayName, t.rt(pLore, "%case:" + c, "%keys:" + keys), enchanted);

                }
            }
        } else
        if(materialType == MaterialType.CH) {
            String[] parts = material.split(":");
            String category = parts[1];
            String id = parts[2];
            if(Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                item = CustomHeadSupport.getSkull(category, id, displayName, t.rt(lore, "%case:" + c, "%keys:" + keys));
            } else {
                item = t.createItem(Material.STONE, 1, 1, displayName, t.rt(lore,"%case:" + c, "%keys:" + keys), enchanted);
            }
        } else if (materialType == MaterialType.BASE64) {
            String[] parts = material.split(":");
            String base64 = parts[1];
            item = t.getBASE64Skull(base64, displayName, t.rt(lore,"%case:" + c, "%keys:" + keys));
        } else {
            String[] parts = material.split(":");
            byte data = -1;
            if(parts[1] != null) {
                data = Byte.parseByte(parts[1]);
            }
            itemMaterial = Material.getMaterial(parts[0]);
            if (itemMaterial == null) {
                itemMaterial = Material.STONE;
            }
            item = t.createItem(itemMaterial, data, 1, displayName, t.rt(lore,"%case:" + c, "%keys:" + keys), enchanted);
        }
        return item;
    }
}