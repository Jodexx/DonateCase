package com.jodexindustries.donatecase.gui;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.support.CustomHeadSupport;
import com.jodexindustries.donatecase.tools.support.HeadDatabaseSupport;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GuiDonatCase {

    public GuiDonatCase(Player p, String c) {
        String title = Case.getCaseTitle(c);
        assert title != null;
        Inventory inv = Bukkit.createInventory(null, 45, Main.t.rc(title));
        String materialID = Main.casesConfig.getCase(c).getString("case.Gui.GuiMaterial");
        if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            materialID = PAPISupport.setPlaceholders(p, materialID);
        }
        boolean materialEnchant = Main.casesConfig.getCase(c).getBoolean("case.Gui.GuiMaterialEnchant");
        if(Main.casesConfig.getCase(c).getString("case.Gui.GuiMaterial") != null && !materialID.equalsIgnoreCase("AIR")) {
            String materialNAME = Objects.requireNonNull(Main.casesConfig.getCase(c).getString("case.Gui.GuiMaterialName"));
            if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                materialNAME = PAPISupport.setPlaceholders(p, materialNAME);
            }
            List<String> materialLORE = new ArrayList<>();
            for(String line : Main.casesConfig.getCase(c).getStringList("case.Gui.GuiMaterialLore")) {
                if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    line = PAPISupport.setPlaceholders(p, line);
                }
                materialLORE.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            Material material;
            ItemStack f;
            if (!materialID.contains(":")) {
                material = Material.getMaterial(materialID);
                if (material == null) {
                    material = Material.STONE;
                }
                f = Main.t.createItem(material, -1, 1, materialNAME, materialLORE, materialEnchant);
            } else {
                if (materialID.startsWith("HEAD")) {
                    String[] parts = materialID.split(":");
                    f = Main.t.getPlayerHead(parts[1], materialNAME, materialLORE);
                } else if (materialID.startsWith("HDB")) {
                    String[] parts = materialID.split(":");
                    String id = parts[1];
                    if (Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                        f = HeadDatabaseSupport.getSkull(id, materialNAME, materialLORE);
                    } else {
                        f = new ItemStack(Material.STONE);
                    }
                } else if (materialID.startsWith("CH")) {
                    String[] parts = materialID.split(":");
                    String category = parts[1];
                    String id = parts[2];
                    if (Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                        f = CustomHeadSupport.getSkull(category, id, materialNAME, materialLORE);
                    } else {
                        f = new ItemStack(Material.STONE);
                    }
                } else if (materialID.startsWith("BASE64")) {
                    String[] parts = materialID.split(":");
                    String base64 = parts[1];
                    f = Main.t.getBASE64Skull(base64, materialNAME, materialLORE);
                } else {
                    String[] parts = materialID.split(":");
                    byte data = -1;
                    if(parts[1] != null) {
                        data = Byte.parseByte(parts[1]);
                    }
                    material = Material.getMaterial(parts[0]);
                    if (material == null) {
                        material = Material.STONE;
                    }
                    f = Main.t.createItem(material, data, 1, materialNAME, materialLORE, materialEnchant);
                }
            }
            for (int a = 0; a < 2; ++a) {
                int i;
                for (i = 1; i <= 9; ++i) {
                    inv.setItem(Main.t.c(i, a == 0 ? 1 : 5), f);
                }

                for (i = 2; i <= 4; ++i) {
                    inv.setItem(Main.t.c(a == 0 ? 1 : 9, i), f);
                }
            }
        }

        String opencasematerialID = Main.casesConfig.getCase(c).getString("case.Gui.GuiOpenCaseMaterial").toUpperCase();
        if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            opencasematerialID = PAPISupport.setPlaceholders(p, opencasematerialID);
        }
        boolean opencasematerialEnchant = Main.casesConfig.getCase(c).getBoolean("case.Gui.GuiOpenCaseMaterialEnchant");
        Material opencasematerial;
        ItemStack opencaseitemstack;
        String displayname;
        int keys = Case.getKeys(c, p.getName());
        List<String> lore = Main.casesConfig.getCase(c).getStringList("case.Gui.Lore");
        displayname = Main.t.rc(Main.casesConfig.getCase(c).getString("case.Gui.DisplayName")
                .replace("<key>", String.valueOf(Case.getKeys(c, p.getName()))));
        if(!opencasematerialID.contains(":")) {
            opencasematerial = Material.getMaterial(opencasematerialID);
            if (opencasematerial == null) {
                opencasematerial = Material.STONE;
            }
            opencaseitemstack = Main.t.createItem(opencasematerial, -1, 1, displayname, Main.t.rt(lore,"%case:" + c, "%keys:" + keys), opencasematerialEnchant);
        } else
        if(opencasematerialID.startsWith("HEAD")) {
            String[] parts = opencasematerialID.split(":");
            opencaseitemstack = Main.t.getPlayerHead(parts[1], displayname, Main.t.rt(lore,"%case:" + c, "%keys:" + keys));
        } else
        if(opencasematerialID.startsWith("HDB")) {
            String[] parts = opencasematerialID.split(":");
            String id = parts[1];
            if(Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                opencaseitemstack = HeadDatabaseSupport.getSkull(id, displayname, Main.t.rt(lore, "%case:" + c, "%keys:" + keys));
            } else {
                if(!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    opencaseitemstack = Main.t.createItem(Material.STONE, 1, 1, displayname, Main.t.rt(lore, "%case:" + c, "%keys:" + keys), opencasematerialEnchant);
                } else {
                    List<String> pLore = new ArrayList<>();
                    for(String line : lore) {
                        if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                            line = PAPISupport.setPlaceholders(p, line);
                        }
                        pLore.add(line);
                    }
                    opencaseitemstack = Main.t.createItem(Material.STONE, 1, 1, displayname, Main.t.rt(pLore, "%case:" + c, "%keys:" + keys), opencasematerialEnchant);

                }
            }
        } else
        if(opencasematerialID.startsWith("CH")) {
            String[] parts = opencasematerialID.split(":");
            String category = parts[1];
            String id = parts[2];
            if(Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                opencaseitemstack = CustomHeadSupport.getSkull(category, id, displayname, Main.t.rt(lore, "%case:" + c, "%keys:" + keys));
            } else {
                opencaseitemstack = Main.t.createItem(Material.STONE, 1, 1, displayname, Main.t.rt(lore,"%case:" + c, "%keys:" + keys), opencasematerialEnchant);
            }
        } else if (opencasematerialID.startsWith("BASE64")) {
            String[] parts = opencasematerialID.split(":");
            String base64 = parts[1];
            opencaseitemstack = Main.t.getBASE64Skull(base64, displayname, Main.t.rt(lore,"%case:" + c, "%keys:" + keys));
        } else {
            String[] parts = opencasematerialID.split(":");
            byte data = -1;
            if(parts[1] != null) {
                data = Byte.parseByte(parts[1]);
            }
            opencasematerial = Material.getMaterial(parts[0]);
            if (opencasematerial == null) {
                opencasematerial = Material.STONE;
            }
            opencaseitemstack = Main.t.createItem(opencasematerial, data, 1, displayname, Main.t.rt(lore,"%case:" + c, "%keys:" + keys), opencasematerialEnchant);
        }
        inv.setItem(Main.t.c(5, 3), opencaseitemstack);
        p.openInventory(inv);
    }
}
