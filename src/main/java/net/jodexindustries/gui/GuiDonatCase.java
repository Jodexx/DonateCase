package net.jodexindustries.gui;

import net.jodexindustries.dc.Case;
import net.jodexindustries.dc.DonateCase;
import net.jodexindustries.tools.CustomConfig;
import net.jodexindustries.tools.Tools;
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
        Inventory inv = Bukkit.createInventory(null, 45, DonateCase.t.rc(title));
        final String materialID = Objects.requireNonNull(CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.GuiMaterial")).toUpperCase();
        Material material;
        material = Material.getMaterial(materialID);
        if (material == null) {
            material = Material.STONE;
        }
        ItemStack f = DonateCase.t.createItem(material, 1, 1, " ");

        for(int a = 0; a < 2; ++a) {
            int var7;
            for(var7 = 1; var7 <= 9; ++var7) {
                inv.setItem(DonateCase.t.c(var7, a == 0 ? 1 : 5), f);
            }

            for(var7 = 2; var7 <= 4; ++var7) {
                inv.setItem(DonateCase.t.c(a == 0 ? 1 : 9, var7), f);
            }
        }

        int var10001 = DonateCase.t.c(5, 3);
        Tools var10002 = DonateCase.t;
        final String opencasematerialID = Objects.requireNonNull(CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.GuiOpenCaseMaterial")).toUpperCase();
        Material opencasematerial;
        opencasematerial = Material.getMaterial(opencasematerialID);
        if (opencasematerial == null) {
            opencasematerial = Material.STONE;
        }
        String displayname = DonateCase.t.rc(Objects.requireNonNull(CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.DisplayName")).replace("<key>", String.valueOf(Case.getKeys(c, p.getName()))));
        List<String> lore = CustomConfig.getConfig().getStringList("DonatCase.Cases." + c + ".Gui.Lore");
        int keys = Case.getKeys(c, p.getName());
        inv.setItem(var10001, var10002.createItem(opencasematerial, displayname, DonateCase.t.rt(lore, "%case:" + c, "%keys:" + keys)));
        p.openInventory(inv);
    }
}
