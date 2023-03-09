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

public class GuiDonatCase {
    public GuiDonatCase(Player p, String c) {
        String title = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Title");
        Inventory inv = Bukkit.createInventory(null, 45, DonateCase.t.rc(title));
        ItemStack f = DonateCase.t.createItem(Material.WHITE_STAINED_GLASS_PANE, 1, 1, " ");

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
        Material var10003 = Material.TRIPWIRE_HOOK;
        String var10004 = DonateCase.t.rc(CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Gui.DisplayName").replace("<key>", String.valueOf(Case.getKeys(c, p.getName()))));
        Tools var10005 = DonateCase.t;
        List var10006 = CustomConfig.getConfig().getStringList("DonatCase.Cases." + c + ".Gui.Lore");
        String[] var10007 = new String[]{"%case:" + c, null};
        int var10010 = Case.getKeys(c, p.getName());
        var10007[1] = "%keys:" + var10010;
        inv.setItem(var10001, var10002.createItem(var10003, var10004, var10005.rt(var10006, var10007)));
        p.openInventory(inv);
    }
}
