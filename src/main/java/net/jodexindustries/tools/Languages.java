package net.jodexindustries.tools;

import java.io.File;
import net.jodexindustries.dc.DonateCase;
import org.bukkit.configuration.file.YamlConfiguration;

public class Languages {
    public YamlConfiguration lang;

    public Languages(String lang) {
        File path = new File(DonateCase.instance.getDataFolder(), "lang");
        File[] listFiles;
        int length = (listFiles = path.listFiles()).length;

        for(int i = 0; i < length; ++i) {
            File l = listFiles[i];
            if (l.getName().toLowerCase().split("_")[0].equalsIgnoreCase(lang)) {
                this.lang = YamlConfiguration.loadConfiguration(l);
            }
        }

    }

    public YamlConfiguration getLang() {
        return this.lang;
    }
}
