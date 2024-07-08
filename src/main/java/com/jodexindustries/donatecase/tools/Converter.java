package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.MaterialType;
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

    public static void convertBASE64(DonateCase plugin) {
        for (String caseType : plugin.casesConfig.getCases().keySet()) {
            Pair<File, YamlConfiguration> pair = plugin.casesConfig.getCases().get(caseType);
            YamlConfiguration config = pair.getSecond();
            String version = config.getString("config");

            if (version != null) {
                continue;
            }

            ConfigurationSection caseSection = config.getConfigurationSection("case");
            if (caseSection == null) {
                continue;
            }

            // Convert materials in the GUI section
            convertMaterialsInSection(caseSection.getConfigurationSection("Gui"), "Material");

            // Convert materials in the win items section
            convertMaterialsInSection(caseSection.getConfigurationSection("Items"), "Item.ID");

            // Set the config version
            config.set("config", "1.0");

            try {
                config.save(pair.getFirst());
                plugin.getLogger().info("BASE64 converted successfully for case type: " + caseType);
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
    private static void convertMaterialsInSection(ConfigurationSection section, String materialKey) {
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


    public static void convertCasesLocation() {
        ConfigurationSection cases_ = Case.getCustomConfig().getCases().getConfigurationSection("DonatCase.Cases");
        if(cases_ != null) {
            for (String name : cases_.getValues(false).keySet()) {
                if (Case.getCustomConfig().getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                    return;
                } else {
                    String locationString = Case.getCustomConfig().getCases().getString("DonatCase.Cases." + name + ".location");
                    Location lv = fromString(locationString);
                    String world = "Undefined";
                    if (lv != null) {
                        if (lv.getWorld() != null) {
                            world = lv.getWorld().getName();
                        }
                        String location = world + ";" + lv.getX() + ";" + lv.getY() + ";" + lv.getZ() + ";" + lv.getPitch() + ";" + lv.getYaw();
                        Case.getCustomConfig().getCases().set("DonatCase.Cases." + name + ".location", location);
                    }
                }
            }
        }
        Case.getCustomConfig().getCases().set("config", "1.0");
        Case.getCustomConfig().saveCases();
        Logger.log("&aConversion successful!");
    }

    public static void convertCases() {
        ConfigurationSection cases = Case.getCustomConfig().getConfig().getConfigurationSection("DonatCase.Cases");
        if (cases != null) {
            for (String caseName : cases.getKeys(false)) {
                File folder = new File(Case.getInstance().getDataFolder(), "cases");
                File caseFile;
                try {
                    caseFile = new File(folder, caseName + ".yml");
                    caseFile.createNewFile();
                    YamlConfiguration caseConfig = YamlConfiguration.loadConfiguration(caseFile);
                    caseConfig.set("case", Case.getCustomConfig().getConfig().getConfigurationSection("DonatCase.Cases." + caseName));
                    String defaultMaterial = Case.getCustomConfig().getConfig().getString("DonatCase.Cases." + caseName + ".Gui.GuiMaterial");
                    String defaultDisplayName = Case.getCustomConfig().getConfig().getString("DonatCase.Cases." + caseName + ".Gui.GuiMaterialName");
                    boolean defaultEnchanted = Case.getCustomConfig().getConfig().getBoolean("DonatCase.Cases." + caseName + ".Gui.GuiMaterialEnchant");
                    List<String> defaultLore = Case.getCustomConfig().getConfig().getStringList("DonatCase.Cases." + caseName + ".Gui.GuiMaterialLore");
                    List<Integer> defaultSlots = new ArrayList<>();
                    defaultSlots.add(0);
                    defaultSlots.add(8);

                    String openMaterial = Case.getCustomConfig().getConfig().getString("DonatCase.Cases." + caseName + ".Gui.GuiOpenCaseMaterial");
                    String openDisplayName = Case.getCustomConfig().getConfig().getString("DonatCase.Cases." + caseName + ".Gui.DisplayName");
                    boolean openEnchanted = Case.getCustomConfig().getConfig().getBoolean("DonatCase.Cases." + caseName + ".Gui.GuiOpenCaseMaterialEnchant");
                    List<String> openLore = Case.getCustomConfig().getConfig().getStringList("DonatCase.Cases." + caseName + ".Gui.Lore");
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
        Case.getCustomConfig().getConfig().set("DonatCase.Cases", null);
        Case.getCustomConfig().saveConfig();
    }

    public static void convertLanguage(YamlConfiguration config) {
        config.set("config", "2.6");

        String prefix = config.getString("Prefix");
        config.set("Prefix", null);
        config.set("prefix", prefix);

        String noPermission = config.getString("NoPermission");
        config.set("NoPermission", null);
        config.set("no-permission", noPermission);

        String updateCheck = config.getString("UpdateCheck");
        config.set("UpdateCheck", null);
        config.set("new-update", updateCheck);

        String caseNotExist = config.getString("CaseNotExist");
        config.set("CaseNotExist", null);
        config.set("case-does-not-exist", caseNotExist);

        String caseAlreadyHasByName = config.getString("CaseAlreadyHasByName");
        config.set("CaseAlreadyHasByName", null);
        config.set("case-already-exist", caseAlreadyHasByName);

        String hasDonatCase = config.getString("HasDonatCase");
        config.set("HasDonatCase", null);
        config.set("case-already-created", hasDonatCase);

        String addDonatCase = config.getString("AddDonatCase");
        config.set("AddDonatCase", null);
        config.set("case-added", addDonatCase);

        String RemoveDonatCase = config.getString("RemoveDonatCase");
        config.set("RemoveDonatCase", null);
        config.set("case-removed", RemoveDonatCase);

        String blockDontDonatCase = config.getString("BlockDontDonatCase");
        config.set("BlockDontDonatCase", null);
        config.set("block-is-not-case", blockDontDonatCase);

        String giveKeys = config.getString("GiveKeys");
        config.set("GiveKeys", null);
        config.set("keys-given", giveKeys);

        String giveKeysTarget = config.getString("GiveKeysTarget");
        config.set("GiveKeysTarget", null);
        config.set("keys-given-target", giveKeysTarget);

        String setKeys = config.getString("SetKeys");
        config.set("SetKeys", null);
        config.set("keys-sets", setKeys);

        String setKeysTarget = config.getString("SetKeysTarget");
        config.set("SetKeysTarget", null);
        config.set("keys-sets-target", setKeysTarget);

        String clearKeys = config.getString("ClearKeys");
        config.set("ClearKeys", null);
        config.set("keys-cleared", clearKeys);

        String clearAllKeys = config.getString("ClearAllKeys");
        config.set("ClearAllKeys", null);
        config.set("all-keys-cleared", clearAllKeys);

        String destoryDonatCase = config.getString("DestoryDonatCase");
        config.set("DestoryDonatCase", null);
        config.set("case-destroy-disallow", destoryDonatCase);

        String noKey = config.getString("NoKey");
        config.set("NoKey", null);
        config.set("no-keys", noKey);

        String haveOpenCase = config.getString("HaveOpenCase");
        config.set("HaveOpenCase", null);
        config.set("case-opens", haveOpenCase);

        String reloadConfig = config.getString("ReloadConfig");
        config.set("ReloadConfig", null);
        config.set("config-reloaded", reloadConfig);

        String casesList = config.getString("CasesList");
        config.set("CasesList", null);
        config.set("list-of-cases", casesList);

        String NumberFormatException = config.getString("NumberFormatException");
        config.set("NumberFormatException", null);
        config.set("number-format-exception", NumberFormatException);

        List<String> help = config.getStringList("Help");
        config.set("Help", null);
        config.set("help", help);

        String helpAddonsFormatAddonName = config.getString("HelpAddons.Format.AddonName");
        config.set("help-addons.format.name", helpAddonsFormatAddonName);

        String helpAddonsFormatAddonDescription = config.getString("HelpAddons.Format.AddonDescription");
        config.set("help-addons.format.description", helpAddonsFormatAddonDescription);

        String helpAddonsFormatAddonCommand = config.getString("HelpAddons.Format.AddonCommand");
        config.set("help-addons.format.command", helpAddonsFormatAddonCommand);

        config.set("HelpAddons", null);


        List<String> helpPlayer = config.getStringList("HelpPlayer");
        config.set("HelpPlayer", null);
        config.set("help-player", helpPlayer);

        List<String> myKeys = config.getStringList("MyKeys");
        config.set("MyKeys", null);
        config.set("my-keys", myKeys);

        List<String> playerKeys = config.getStringList("PlayerKeys");
        config.set("PlayerKeys", null);
        config.set("player-keys", playerKeys);

        Case.getCustomConfig().saveLang();
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
