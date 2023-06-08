package com.jodexindustries.gui;

import com.jodexindustries.dc.Case;
import com.jodexindustries.dc.Main;
import com.jodexindustries.tools.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class GuiDonatCase {
    public GuiDonatCase(Player p, String c) {
        String title = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Title");
        assert title != null;
        Inventory inv = Bukkit.createInventory(null, 45, Main.t.rc(title));
        final String materialID = Objects.requireNonNull(CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.GuiMaterial")).toUpperCase();
        if(CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.GuiMaterial") != null && !materialID.equalsIgnoreCase("AIR")) {
            final String materialNAME = Objects.requireNonNull(CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.GuiMaterialName"));
            Material material;
            ItemStack f = new ItemStack(Material.STRUCTURE_VOID);
            if (!materialID.startsWith("HEAD") && !materialID.startsWith("BASE64")) {
                material = Material.getMaterial(materialID);
                if (material == null) {
                    material = Material.STONE;
                }
                f = Main.t.createItem(material, 1, 1, materialNAME);
            }
            if (materialID.startsWith("HEAD")) {
                String[] parts = materialID.split(":");
                f = Main.t.getPlayerHead(parts[1], materialNAME);
            }
            if(materialID.startsWith("HDB")) {
                String[] parts = materialID.split(":");
                String id = parts[1];
                if(Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                    f  = Main.t.getHDBSkull(id, materialNAME);
                } else {
                    f = new ItemStack(Material.STONE);
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

        final String opencasematerialID = Objects.requireNonNull(CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.GuiOpenCaseMaterial")).toUpperCase();
        Material opencasematerial;
        ItemStack opencaseitemstack = null;
        String displayname;
        int keys = Case.getKeys(c, p.getName());
        List<String> lore = CustomConfig.getConfig().getStringList("DonatCase.Cases." + c + ".Gui.Lore");
        displayname = Main.t.rc(Objects.requireNonNull(CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.DisplayName"))
                .replace("<key>", String.valueOf(Case.getKeys(c, p.getName()))));
        if(!opencasematerialID.startsWith("HEAD") && !opencasematerialID.startsWith("BASE64") && !opencasematerialID.startsWith("HDB")) {
            opencasematerial = Material.getMaterial(opencasematerialID);
            if (opencasematerial == null) {
                opencasematerial = Material.STONE;
            }
            opencaseitemstack = Main.t.createItem(opencasematerial, 1, 1, displayname, Main.t.rt(lore,"%case:" + c, "%keys:" + keys ));
        }
        if(opencasematerialID.startsWith("HEAD")) {
            String[] parts = opencasematerialID.split(":");
            opencaseitemstack = Main.t.getPlayerHead(parts[1], displayname, Main.t.rt(lore,"%case:" + c, "%keys:" + keys));
        }
        if(opencasematerialID.startsWith("HDB")) {
            String[] parts = opencasematerialID.split(":");
            String id = parts[1];
            if(Main.instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                opencaseitemstack = Main.t.getHDBSkull(id, displayname, Main.t.rt(lore, "%case:" + c, "%keys:" + keys));
            } else {
                opencaseitemstack = Main.t.createItem(Material.STONE, 1, 1, displayname, Main.t.rt(lore,"%case:" + c, "%keys:" + keys));
            }
        }
        inv.setItem(Main.t.c(5, 3), opencaseitemstack);
        p.openInventory(inv);
    }
}
