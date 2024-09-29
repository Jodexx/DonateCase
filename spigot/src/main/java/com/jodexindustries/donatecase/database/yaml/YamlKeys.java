package com.jodexindustries.donatecase.database.yaml;

import com.jodexindustries.donatecase.api.Case;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Class for yaml keys storage
 * Actually its Keys.yml, but extracted to another class
 */
public class YamlKeys {
    private final File fileKeys;
    private final YamlConfiguration keys;

    /**
     * Default constructor for loading
     */
    public YamlKeys() {
        this.fileKeys = new File(Case.getInstance().getDataFolder(), "Keys.yml");
        keys = YamlConfiguration.loadConfiguration(fileKeys);
    }

    /**
     * Set case keys to a specific player
     * @param caseType Case type
     * @param player Player name
     * @param keys Number of keys
     */
    public void setKeys(String caseType, String player, int keys) {
        this.keys.set("DonateCase.Cases." + caseType + "." + player, keys == 0 ? null : keys);
        save();
    }

    public int getKeys(String caseType, String player) {
        return this.keys.getInt("DonateCase.Cases." + caseType + "." + player);
    }

    public void delAllKeys() {
        this.keys.set("DonateCase.Cases", null);
        save();
    }

    /**
     * Get Configuration
     *
     * @return Bukkit YamlConfiguration class
     */
    public YamlConfiguration get() {
        return keys;
    }

    /**
     * Save Data.yml configuration
     */
    public void save() {
        try {
            keys.save(fileKeys);
        } catch (IOException e) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Keys.yml", e);
        }
    }
}
