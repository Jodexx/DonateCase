package com.jodexindustries.donatecase.database.yaml;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Class for yaml data storage
 * Actually its Data.yml, but extracted to another class
 */
public class YamlData {
    private final File fileData;
    private final YamlConfiguration data;

    /**
     * Default constructor for loading
     */
    public YamlData() {
        fileData = new File(Case.getInstance().getDataFolder(), "Data.yml");
        data = YamlConfiguration.loadConfiguration(fileData);
    }

    /**
     * Get HistoryData array from Data.yml
     *
     * @param caseType Case type for history retrieve
     * @return Array of HistoryData
     */
    public CaseData.HistoryData[] getHistoryData(String caseType) {
        CaseData.HistoryData[] historyData = new CaseData.HistoryData[10];

        ConfigurationSection dataSection = data.getConfigurationSection("Data");

        if (dataSection != null) {
            ConfigurationSection section = dataSection.getConfigurationSection(caseType);

            if (section != null) {
                for (String i : section.getKeys(false)) {
                    ConfigurationSection caseDataSection = section.getConfigurationSection(i);

                    if (caseDataSection != null) {
                        CaseData.HistoryData data = new CaseData.HistoryData(
                                caseDataSection.getString("Item"),
                                caseType,
                                caseDataSection.getString("Player"),
                                caseDataSection.getLong("Time"),
                                caseDataSection.getString("Group"),
                                caseDataSection.getString("Action"));

                        historyData[Integer.parseInt(i)] = data;
                    }
                }
            }
        }
        return historyData;
    }

    /**
     * Set HistoryData for specific case with index
     *
     * @param caseType    Case type
     * @param index       History index (0-9)
     * @param historyData Case History
     */
    public void setHistoryData(String caseType, int index, CaseData.HistoryData historyData) {
        data.set("Data." + caseType + "." + index + ".Player", historyData.getPlayerName());
        data.set("Data." + caseType + "." + index + ".Time", historyData.getTime());
        data.set("Data." + caseType + "." + index + ".Group", historyData.getGroup());
        data.set("Data." + caseType + "." + index + ".Item", historyData.getItem());
        data.set("Data." + caseType + "." + index + ".Action", historyData.getAction());
        save();
    }

    /**
     * Get count of opened cases by player
     *
     * @param player   Player who opened
     * @param caseType Case type
     * @return opened count
     */
    public int getOpenCount(String player, String caseType) {
        return data.getInt("Open." + player + "." + caseType);
    }

    /**
     * Set count of case opens by player
     *
     * @param player    Player who opened
     * @param caseType  Case type
     * @param openCount Open count
     */
    public void setOpenCount(String player, String caseType, int openCount) {
        data.set("Open." + player + "." + caseType, openCount);
        save();
    }

    /**
     * Get Configuration
     *
     * @return Bukkit YamlConfiguration class
     */
    public YamlConfiguration get() {
        return data;
    }

    /**
     * Save Data.yml configuration
     */
    public void save() {
        try {
            data.save(fileData);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Data.yml");
        }

    }

}
