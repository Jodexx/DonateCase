package net.jodexindustries.tools;

import java.text.NumberFormat;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.jodexindustries.dc.DonateCase;
import org.bukkit.OfflinePlayer;

public class Placeholder extends PlaceholderExpansion {
    public Placeholder() {
    }

    public String getAuthor() {
        return "JodexIndustries";
    }

    public String getIdentifier() {
        return "DonateCase";
    }

    public String getVersion() {
        return "2.0.1";
    }

    public String onRequest(OfflinePlayer player, String params) {
        if (params.startsWith("keys_")) {
            String[] parts = params.split("_", 2);
            int s;
            if (DonateCase.Tconfig) {
                s = CustomConfig.getKeys().getInt("DonatCase.Cases." + parts[1] + "." + player.getName().toLowerCase());
                return NumberFormat.getNumberInstance().format(s);
            } else {
                s = DonateCase.mysql.getKey(parts[1], player.getName().toLowerCase());
                return NumberFormat.getNumberInstance().format(s);
            }
        } else {
            return null;
        }
    }
}
