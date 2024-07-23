package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.api.data.MaterialType;
import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {
    private final Config config;

    public Converter(Config config) {
        this.config = config;
    }

    public void convertBASE64() {
        for (String caseType : config.getCasesConfig().getCases().keySet()) {
            Pair<File, YamlConfiguration> pair = config.getCasesConfig().getCases().get(caseType);
            YamlConfiguration caseConfig = pair.getSecond();
            String version = caseConfig.getString("config");

            if (version != null) {
                continue;
            }

            ConfigurationSection caseSection = caseConfig.getConfigurationSection("case");
            if (caseSection == null) {
                continue;
            }

            // Convert materials in the GUI section
            convertMaterialsInSection(caseSection.getConfigurationSection("Gui.Items"), "Material");

            // Convert materials in the win items section
            convertMaterialsInSection(caseSection.getConfigurationSection("Items"), "Item.ID");

            // Set the config version
            caseConfig.set("config", "1.0");

            try {
                caseConfig.save(pair.getFirst());
                config.getPlugin().getLogger().info("BASE64 converted successfully for case type: " + caseType);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save config for case type: " + caseType, e);
            }
        }
    }

    /**
     * Converts materials in the specified section by replacing BASE64 materials with MCURL.
     *
     * @param section the configuration section to process
     * @param materialKey the key used to identify material strings within the section
     */
    private void convertMaterialsInSection(ConfigurationSection section, String materialKey) {
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection itemSection = section.getConfigurationSection(key);
            if (itemSection == null) {
                continue;
            }

            String material = itemSection.getString(materialKey);
            if (material == null) {
                continue;
            }

            String[] materialParts = material.split(":");
            MaterialType materialType = MaterialType.fromString(materialParts[0]);
            if (materialType != MaterialType.BASE64) {
                continue;
            }

            material = material.replace(materialParts[0], MaterialType.MCURL.name());
            itemSection.set(materialKey, material);
        }
    }


    public void convertCasesLocation() {
        ConfigurationSection cases_ = config.getCases().getConfigurationSection("DonatCase.Cases");
        if(cases_ != null) {
            for (String name : cases_.getValues(false).keySet()) {
                if (cases_.getString(name + ".location") == null) {
                    return;
                } else {
                    String locationString = cases_.getString( name + ".location");
                    Location lv = fromString(locationString);
                    String world = "Undefined";
                    if (lv != null) {
                        if (lv.getWorld() != null) {
                            world = lv.getWorld().getName();
                        }
                        String location = world + ";" + lv.getX() + ";" + lv.getY() + ";" + lv.getZ() + ";" + lv.getPitch() + ";" + lv.getYaw();
                        config.getCases().set("DonatCase.Cases." + name + ".location", location);
                    }
                }
            }
        }
        config.getCases().set("config", "1.0");
        config.saveCases();
        Logger.log("&aConversion successful!");
    }

    public void convertCases() {
        ConfigurationSection cases = config.getConfig().getConfigurationSection("DonatCase.Cases");
        if (cases != null) {
            for (String caseName : cases.getKeys(false)) {
                File folder = new File(config.getPlugin().getDataFolder(), "cases");
                File caseFile;
                try {
                    caseFile = new File(folder, caseName + ".yml");
                    caseFile.createNewFile();
                    YamlConfiguration caseConfig = YamlConfiguration.loadConfiguration(caseFile);
                    caseConfig.set("case", config.getConfig().getConfigurationSection("DonatCase.Cases." + caseName));
                    String defaultMaterial = config.getConfig().getString("DonatCase.Cases." + caseName + ".Gui.GuiMaterial");
                    String defaultDisplayName = config.getConfig().getString("DonatCase.Cases." + caseName + ".Gui.GuiMaterialName");
                    boolean defaultEnchanted = config.getConfig().getBoolean("DonatCase.Cases." + caseName + ".Gui.GuiMaterialEnchant");
                    List<String> defaultLore = config.getConfig().getStringList("DonatCase.Cases." + caseName + ".Gui.GuiMaterialLore");
                    List<Integer> defaultSlots = new ArrayList<>();
                    defaultSlots.add(0);
                    defaultSlots.add(8);

                    String openMaterial = config.getConfig().getString("DonatCase.Cases." + caseName + ".Gui.GuiOpenCaseMaterial");
                    String openDisplayName = config.getConfig().getString("DonatCase.Cases." + caseName + ".Gui.DisplayName");
                    boolean openEnchanted = config.getConfig().getBoolean("DonatCase.Cases." + caseName + ".Gui.GuiOpenCaseMaterialEnchant");
                    List<String> openLore = config.getConfig().getStringList("DonatCase.Cases." + caseName + ".Gui.Lore");
                    List<Integer> openSlots = new ArrayList<>();
                    openSlots.add(22);

                    caseConfig.set("case.Gui", null);
                    caseConfig.save(caseFile);
                    caseConfig.set("case.Gui.Items.1.DisplayName", defaultDisplayName);
                    caseConfig.set("case.Gui.Items.1.Enchanted", defaultEnchanted);
                    caseConfig.set("case.Gui.Items.1.Lore", defaultLore);
                    caseConfig.set("case.Gui.Items.1.Material", defaultMaterial);
                    caseConfig.set("case.Gui.Items.1.Type", "DEFAULT");
                    caseConfig.set("case.Gui.Items.1.Slots", defaultSlots);

                    caseConfig.set("case.Gui.Items.Open.DisplayName", openDisplayName);
                    caseConfig.set("case.Gui.Items.Open.Enchanted", openEnchanted);
                    caseConfig.set("case.Gui.Items.Open.Lore", openLore);
                    caseConfig.set("case.Gui.Items.Open.Material", openMaterial);
                    caseConfig.set("case.Gui.Items.Open.Type", "OPEN");
                    caseConfig.set("case.Gui.Items.Open.Slots", openSlots);

                    caseConfig.set("case.Gui.Size", 45);
                    caseConfig.save(caseFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        config.getConfig().set("DonatCase.Cases", null);
       config.saveConfig();
    }

    public void convertLanguage() {
        YamlConfiguration lang = this.config.getLang();
        lang.set("config", "2.6");

        String prefix = lang.getString("Prefix");
        lang.set("Prefix", null);
        lang.set("prefix", prefix);

        String noPermission = lang.getString("NoPermission");
        lang.set("NoPermission", null);
        lang.set("no-permission", noPermission);

        String updateCheck = lang.getString("UpdateCheck");
        lang.set("UpdateCheck", null);
        lang.set("new-update", updateCheck);

        String caseNotExist = lang.getString("CaseNotExist");
        lang.set("CaseNotExist", null);
        lang.set("case-does-not-exist", caseNotExist);

        String caseAlreadyHasByName = lang.getString("CaseAlreadyHasByName");
        lang.set("CaseAlreadyHasByName", null);
        lang.set("case-already-exist", caseAlreadyHasByName);

        String hasDonatCase = lang.getString("HasDonatCase");
        lang.set("HasDonatCase", null);
        lang.set("case-already-created", hasDonatCase);

        String addDonatCase = lang.getString("AddDonatCase");
        lang.set("AddDonatCase", null);
        lang.set("case-added", addDonatCase);

        String RemoveDonatCase = lang.getString("RemoveDonatCase");
        lang.set("RemoveDonatCase", null);
        lang.set("case-removed", RemoveDonatCase);

        String blockDontDonatCase = lang.getString("BlockDontDonatCase");
        lang.set("BlockDontDonatCase", null);
        lang.set("block-is-not-case", blockDontDonatCase);

        String giveKeys = lang.getString("GiveKeys");
        lang.set("GiveKeys", null);
        lang.set("keys-given", giveKeys);

        String giveKeysTarget = lang.getString("GiveKeysTarget");
        lang.set("GiveKeysTarget", null);
        lang.set("keys-given-target", giveKeysTarget);

        String setKeys = lang.getString("SetKeys");
        lang.set("SetKeys", null);
        lang.set("keys-sets", setKeys);

        String setKeysTarget = lang.getString("SetKeysTarget");
        lang.set("SetKeysTarget", null);
        lang.set("keys-sets-target", setKeysTarget);

        String clearKeys = lang.getString("ClearKeys");
        lang.set("ClearKeys", null);
        lang.set("keys-cleared", clearKeys);

        String clearAllKeys = lang.getString("ClearAllKeys");
        lang.set("ClearAllKeys", null);
        lang.set("all-keys-cleared", clearAllKeys);

        String destoryDonatCase = lang.getString("DestoryDonatCase");
        lang.set("DestoryDonatCase", null);
        lang.set("case-destroy-disallow", destoryDonatCase);

        String noKey = lang.getString("NoKey");
        lang.set("NoKey", null);
        lang.set("no-keys", noKey);

        String haveOpenCase = lang.getString("HaveOpenCase");
        lang.set("HaveOpenCase", null);
        lang.set("case-opens", haveOpenCase);

        String reloadConfig = lang.getString("ReloadConfig");
        lang.set("ReloadConfig", null);
        lang.set("config-reloaded", reloadConfig);

        String casesList = lang.getString("CasesList");
        lang.set("CasesList", null);
        lang.set("list-of-cases", casesList);

        String NumberFormatException = lang.getString("NumberFormatException");
        lang.set("NumberFormatException", null);
        lang.set("number-format-exception", NumberFormatException);

        List<String> help = lang.getStringList("Help");
        lang.set("Help", null);
        lang.set("help", help);

        String helpAddonsFormatAddonName = lang.getString("HelpAddons.Format.AddonName");
        lang.set("help-addons.format.name", helpAddonsFormatAddonName);

        String helpAddonsFormatAddonDescription =lang.getString("HelpAddons.Format.AddonDescription");
        lang.set("help-addons.format.description", helpAddonsFormatAddonDescription);

        String helpAddonsFormatAddonCommand = lang.getString("HelpAddons.Format.AddonCommand");
        lang.set("help-addons.format.command", helpAddonsFormatAddonCommand);

        lang.set("HelpAddons", null);


        List<String> helpPlayer = lang.getStringList("HelpPlayer");
        lang.set("HelpPlayer", null);
        lang.set("help-player", helpPlayer);

        List<String> myKeys = lang.getStringList("MyKeys");
        lang.set("MyKeys", null);
        lang.set("my-keys", myKeys);

        List<String> playerKeys = lang.getStringList("PlayerKeys");
        lang.set("PlayerKeys", null);
        lang.set("player-keys", playerKeys);

        this.config.saveLang();
    }

    public static Location fromString(String str) {
        String regex = "Location\\{world=CraftWorld\\{name=(.*?)},x=(.*?),y=(.*?),z=(.*?),pitch=(.*?),yaw=(.*?)}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            World world = null;
            if (!matcher.group(1).equals("null")) {
                world = Bukkit.getWorld(matcher.group(1));
            }
            double x = Double.parseDouble(matcher.group(2));
            double y = Double.parseDouble(matcher.group(3));
            double z = Double.parseDouble(matcher.group(4));
            float pitch = Float.parseFloat(matcher.group(5));
            float yaw = Float.parseFloat(matcher.group(6));

            return new Location(world, x, y, z, yaw, pitch);
        }

        return null;
    }
}
