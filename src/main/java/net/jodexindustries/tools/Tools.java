package net.jodexindustries.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.jodexindustries.dc.DonateCase;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class Tools {
    public Tools() {
    }

    public void launchFirework(Location l) {
        Random r = new Random();
        Firework fw = (Firework)l.getWorld().spawnEntity(l.subtract(new Vector(0.0, 0.5, 0.0)), EntityType.FIREWORK);
        FireworkMeta meta = fw.getFireworkMeta();
        Color[] c = new Color[]{Color.RED, Color.AQUA, Color.GREEN, Color.ORANGE, Color.LIME, Color.BLUE, Color.MAROON, Color.WHITE};
        meta.addEffect(FireworkEffect.builder().flicker(false).with(Type.BALL).trail(false).withColor(c[r.nextInt(c.length)], c[r.nextInt(c.length)], c[r.nextInt(c.length)]).build());
        fw.setFireworkMeta(meta);
        fw.setMetadata("case", new FixedMetadataValue(DonateCase.instance, "case"));
        fw.detonate();
    }

    public String getLoc(Location loc) {
        return loc == null ? "" : String.valueOf(String.valueOf(String.valueOf(loc.getWorld().getName()))) + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
    }

    public Location getLoc(String loc) {
        if (loc == null) {
            return null;
        } else {
            String[] ex = loc.split(";");
            String w = ex[0];
            int x = Integer.parseInt(ex[1]);
            int y = Integer.parseInt(ex[2]);
            int z = Integer.parseInt(ex[3]);
            Location l = new Location(Bukkit.getWorld(w), (double)x, (double)y, (double)z);
            return l;
        }
    }

    public int c(int x, int y) {
        int x2 = x - 1;
        int y2 = y - 1;
        return x2 + y2 * 9;
    }

    public String getEnding(int k, String... s) {
        return k >= 2 && k < 5 ? s[0] : (k != 0 && k < 5 ? "" : s[1]);
    }

    public boolean isHere(Location l1, Location l2) {
        return l1.getWorld() == l2.getWorld() && (int)l1.distance(l2) == 0;
    }

    public String getRandomGroup(String casename) {
        Random random = new Random();
        int maxChance = 0;
        int from = 0;

        String item;
        for(Iterator var5 = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases." + casename + ".Items").getValues(true).keySet().iterator(); var5.hasNext(); maxChance += CustomConfig.getConfig().getInt("DonatCase.Cases." + casename + ".Items." + item + ".Chance")) {
            item = (String)var5.next();
        }

        int rand = random.nextInt(maxChance);

        String item2;
        for(Iterator var9 = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases." + casename + ".Items").getValues(true).keySet().iterator(); var9.hasNext(); from += CustomConfig.getConfig().getInt("DonatCase.Cases." + casename + ".Items." + item2 + ".Chance")) {
            item2 = (String)var9.next();
            if (from <= rand && rand < from + CustomConfig.getConfig().getInt("DonatCase.Cases." + casename + ".Items." + item2 + ".Chance")) {
                return item2;
            }
        }

        return null;
    }

    public void msg(CommandSender s, String msg) {
        String var10002 = String.valueOf(String.valueOf(String.valueOf(DonateCase.lang.getString("Prefix"))));
        this.msg_(s, var10002 + msg);
    }

    public void msg_(CommandSender s, String msg) {
        s.sendMessage(this.rc(msg));
    }

    public String rc(String t) {
        return t.replace("&", "§");
    }

    public String rt(String text, String... repl) {
        String[] var3 = repl;
        int var4 = repl.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String s = var3[var5];
            int l = s.split(":")[0].length();
            text = text.replace(s.substring(0, l), s.substring(l + 1));
        }

        return text;
    }

    public List<String> rt(List<String> text, String... repl) {
        ArrayList<String> rt = new ArrayList();
        Iterator var4 = text.iterator();

        while(var4.hasNext()) {
            String t = (String)var4.next();
            rt.add(this.rt(t, repl));
        }

        return rt;
    }

    public List<String> rc(List<String> t) {
        ArrayList<String> a = new ArrayList();
        Iterator var3 = t.iterator();

        while(var3.hasNext()) {
            String s = (String)var3.next();
            a.add(this.rc(s));
        }

        return a;
    }

    public ItemStack createItem(Material ma) {
        return this.createItem(ma, 0, 1, (String)null, (List)null);
    }

    public ItemStack createItem(Material ma, List<String> lore) {
        return this.createItem(ma, 0, 1, "", lore);
    }

    public ItemStack createItem(Material ma, int amount) {
        return this.createItem(ma, 0, amount, "", (List)null);
    }

    public ItemStack createItem(Material ma, int amount, int data) {
        return this.createItem(ma, data, amount, "", (List)null);
    }

    public ItemStack createItem(Material ma, int amount, int data, String dn) {
        return this.createItem(ma, data, amount, dn, (List)null);
    }

    public ItemStack createItem(Material ma, String dn, List<String> lore) {
        return this.createItem(ma, 0, 1, dn, lore);
    }

    public ItemStack createItem(Material ma, String dn) {
        return this.createItem(ma, 0, 1, dn, (List)null);
    }

    public ItemStack createItem(Material ma, int data, int amount, String dn, List<String> lore) {
        ItemStack item = new ItemStack(ma, amount);
        ItemMeta m = item.getItemMeta();
        if (dn != null) {
            m.setDisplayName(this.rc(dn));
        }

        if (lore != null) {
            m.setLore(this.rc(lore));
        }

        item.setItemMeta(m);
        return item;
    }
}
