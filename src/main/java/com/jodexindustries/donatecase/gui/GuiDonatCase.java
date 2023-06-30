package com.jodexindustries.donatecase.gui;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.PAPISupport;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.jodexindustries.donatecase.dc.Main.customConfig;

public class GuiDonatCase {

    public GuiDonatCase(Player p, String c) {
        String title = customConfig.getConfig().getString("DonatCase.Cases." + c + ".Title");
        assert title != null;
        Inventory inv = Bukkit.createInventory(null, 45, Main.t.rc(title));
        String materialID = Objects.requireNonNull(customConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.GuiMaterial"));
        if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            materialID = PAPISupport.setPlaceholders(p, materialID);
        }
        boolean materialEnchant = customConfig.getConfig().getBoolean("DonatCase.Cases." + c + ".Gui.GuiMaterialEnchant");
        if(customConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.GuiMaterial") != null && !materialID.equalsIgnoreCase("AIR")) {
            final String materialNAME = Objects.requireNonNull(customConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.GuiMaterialName"));
            List<String> materialLORE = new ArrayList<>();
            for(String line : customConfig.getConfig().getStringList("DonatCase.Cases." + c + ".Gui.GuiMaterialLore")) {
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
                        f = Main.t.getHDBSkull(id, materialNAME, materialLORE);
                    } else {
                        f = new ItemStack(Material.STONE);
                    }
                } else if (materialID.startsWith("CH")) {
                    String[] parts = materialID.split(":");
                    String category = parts[1];
                    String id = parts[2];
                    if (Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                        f = Main.t.getCHSkull(category, id, materialNAME, materialLORE);
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

        String opencasematerialID = Objects.requireNonNull(customConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.GuiOpenCaseMaterial")).toUpperCase();
        if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            opencasematerialID = PAPISupport.setPlaceholders(p, opencasematerialID);
        }
        boolean opencasematerialEnchant = customConfig.getConfig().getBoolean("DonatCase.Cases." + c + ".Gui.GuiOpenCaseMaterialEnchant");
        Material opencasematerial;
        ItemStack opencaseitemstack = null;
        String displayname;
        int keys = Case.getKeys(c, p.getName());
        List<String> lore = customConfig.getConfig().getStringList("DonatCase.Cases." + c + ".Gui.Lore");
        displayname = Main.t.rc(Objects.requireNonNull(customConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.DisplayName"))
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
                opencaseitemstack = Main.t.getHDBSkull(id, displayname, Main.t.rt(lore, "%case:" + c, "%keys:" + keys));
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
                opencaseitemstack = Main.t.getCHSkull(category, id, displayname, Main.t.rt(lore, "%case:" + c, "%keys:" + keys));
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
