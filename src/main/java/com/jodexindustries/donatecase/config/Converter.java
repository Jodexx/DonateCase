package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.Pair;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {
    private final Config config;

    public Converter(Config config) {
        this.config = config;
    }

    public void convertConfig() {
        ConfigurationSection configSection = config.getConfig().getConfigurationSection("DonatCase");
        if (configSection != null) {
            config.getConfig().set("DonateCase", configSection);
            config.getConfig().set("DonatCase", null);
            config.saveConfig();
        }

        ConfigurationSection keysSection = config.getKeys().getConfigurationSection("DonatCase");
        if (keysSection != null) {
            config.getKeys().set("DonateCase", keysSection);
            config.getKeys().set("DonatCase", null);
            config.saveKeys();
        }

        ConfigurationSection casesSection = config.getCases().getConfigurationSection("DonatCase");
        if (casesSection != null) {
            config.getCases().set("DonateCase", casesSection);
            config.getCases().set("DonatCase", null);
            config.saveCases();
        }
    }

    public void convertAnimations() {
        if(!config.getAnimations().getString("config", "1.3").equals("1.4")) {
            for (String animation : config.getAnimations().getKeys(false)) {
                ConfigurationSection section = config.getAnimations().getConfigurationSection(animation);
                config.getAnimations().set(animation.toUpperCase(), section);
                config.getAnimations().set(animation, null);
            }
            config.getAnimations().set("config", "1.4");
            config.saveAnimations();
        }
    }

    public void convertOverall() {
        for (String caseType : config.getCasesConfig().getCases().keySet()) {
            Pair<File, YamlConfiguration> pair = config.getCasesConfig().getCases().get(caseType);
            YamlConfiguration caseConfig = pair.getSecond();
            String version = caseConfig.getString("config");

            ConfigurationSection caseSection = caseConfig.getConfigurationSection("case");
            if (caseSection == null) continue;

            if (version != null) {
                if (version.equals("1.2")) continue;

                // Convert materials in the GUI section
                convertMaterialsInSection(caseSection.getConfigurationSection("Gui.Items"), "Material");

                // Convert materials in the win items section
                convertMaterialsInSection(caseSection.getConfigurationSection("Items"), "Item.ID");

                config.getPlugin().getLogger().info("BASE64 converted successfully for case type: " + caseType);
            }

            convertSound(caseSection);


            // Convert old actions
            ConfigurationSection itemsSection = caseConfig.getConfigurationSection("case.Items");
            if (itemsSection == null) continue;

            boolean hasOldActions = false;
            for (String item : itemsSection.getKeys(false)) {
                List<String> actions = new ArrayList<>();
                String giveCommand = caseConfig.getString("case.Items." + item + ".GiveCommand");
                List<String> giveCommands = caseConfig.getStringList("case.Items." + item + ".Commands");
                ConfigurationSection giveSection = caseConfig.getConfigurationSection("case.Items." + item + ".GiveCommands");

                if (giveCommand != null || !giveCommands.isEmpty() || giveSection != null) {
                    if (!hasOldActions) {
                        hasOldActions = true;
                        Case.getInstance().getLogger().warning("Case " + caseType + " has outdated actions! Converting...");
                    }
                    actions.addAll(collectActions(caseConfig, item, giveCommand, giveCommands));
                    actions.addAll(collectRandomActions(caseConfig, item, giveSection));

                    caseConfig.set("case.Items." + item + ".Actions", actions);
                    clearOldConfig(caseConfig, item);
                }
            }

            // Convert NoKeyActions
            List<String> noKeyActions = caseSection.getStringList("NoKeyActions");
            String sound = config.getConfig().getString("DonateCase.NoKeyWarningSound");
            String noKeys = config.getLang().getString("no-keys");

            if (sound != null) {
                noKeyActions.add("[sound] " + sound);
                config.getConfig().set("DonateCase.NoKeyWarningSound", null);
            }

            if (noKeys != null) noKeyActions.add("[message] " + noKeys);

            caseSection.set("NoKeyActions", noKeyActions);

            caseSection.set("OpenType", "GUI");
            caseConfig.set("config", "1.2");

            try {
                caseConfig.save(pair.getFirst());
                config.getPlugin().getLogger().info("NoKeyActions | OpenType converted successfully for case type: " + caseType);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save config for case type: " + caseType, e);
            }
        }
    }

    private static void convertSound(ConfigurationSection section) {
        String animationSound = section.getString("AnimationSound");
        double pitch = section.getDouble("Sound.Pitch");
        double volume = section.getDouble("Sound.Volume");

        if(animationSound != null) {
            section.set("AnimationSound", null);
            section.set("Sound", null);

            ConfigurationSection itemsSection = section.getConfigurationSection("Items");
            if (itemsSection == null) return;

            for (String item : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(item);
                if (itemSection == null) continue;

                // GiveType: ONE
                List<String> actions = itemSection.getStringList("Actions");
                if(!actions.isEmpty()) {
                    actions.add("[sound] " + animationSound + " " + volume + " " + pitch);
                    itemSection.set("Actions", actions);
                }

                // GiveType: RANDOM

                ConfigurationSection randomActionsSection = itemSection.getConfigurationSection("RandomActions");
                if (randomActionsSection == null) continue;

                for (String action : randomActionsSection.getKeys(false)) {
                    ConfigurationSection randomActionSection = randomActionsSection.getConfigurationSection(action);
                    if(randomActionSection == null) continue;

                    List<String> randomActions = randomActionSection.getStringList("Actions");
                    if(!randomActions.isEmpty()) {
                        randomActions.add("[sound] " + animationSound + " " + volume + " " + pitch);
                        randomActionSection.set("Actions", randomActions);
                    }

                }
            }
        }

    }

    private static List<String> collectActions(YamlConfiguration caseConfig, String item, String giveCommand, List<String> giveCommands) {
        List<String> actions = new ArrayList<>();
        if (giveCommand != null && !giveCommand.isEmpty()) actions.add("[command] " + giveCommand);
        for (String command : giveCommands) {
            if(command == null || command.isEmpty()) continue;
            String s = "[command] " + command;
            actions.add(s);
        }
        String title = caseConfig.getString("case.Items." + item + ".Title");
        String subTitle = caseConfig.getString("case.Items." + item + ".SubTitle");
        List<String> broadcast = caseConfig.getStringList("case.Items." + item + ".Broadcast");

        StringBuilder titleBuilder = new StringBuilder();
        if(title != null || subTitle != null) {
            titleBuilder.append("[title] ");
            if(title != null) titleBuilder.append(title);
            titleBuilder.append(";");
            if(subTitle != null) titleBuilder.append(subTitle);
        }

        actions.add(titleBuilder.toString());
        broadcast.stream().map(line -> "[broadcast] " + line).forEach(actions::add);
        return actions;
    }

    private static List<String> collectRandomActions(YamlConfiguration caseConfig, String item, ConfigurationSection giveSection) {
        List<String> randomActions = new ArrayList<>();
        if (giveSection != null) {
            for (String choice : giveSection.getKeys(false)) {
                int chance = caseConfig.getInt("case.Items." + item + ".GiveCommands." + choice + ".Chance");
                List<String> choiceActions;
                List<String> choiceCommands = caseConfig.getStringList("case.Items." + item + ".GiveCommands." + choice + ".Commands");
                choiceActions = choiceCommands.stream().map(choiceCommand -> "[command] " + choiceCommand).collect(Collectors.toList());
                List<String> choiceBroadcasts = caseConfig.getStringList("case.Items." + item + ".GiveCommands." + choice + ".Broadcast");
                choiceBroadcasts.stream().map(choiceBroadcast -> "[broadcast] " + choiceBroadcast).forEach(choiceActions::add);
                caseConfig.set("case.Items." + item + ".RandomActions." + choice + ".Chance", chance);
                caseConfig.set("case.Items." + item + ".RandomActions." + choice + ".Actions", choiceActions);
            }
        }
        return randomActions;
    }

    private static void clearOldConfig(YamlConfiguration caseConfig, String item) {
        caseConfig.set("case.Items." + item + ".Title", null);
        caseConfig.set("case.Items." + item + ".SubTitle", null);
        caseConfig.set("case.Items." + item + ".Commands", null);
        caseConfig.set("case.Items." + item + ".GiveCommand", null);
        caseConfig.set("case.Items." + item + ".Broadcast", null);
        caseConfig.set("case.Items." + item + ".GiveCommands", null);
    }

    /**
     * Converts materials in the specified section by replacing BASE64 materials with MCURL.
     *
     * @param section     the configuration section to process
     * @param materialKey the key used to identify material strings within the section
     */
    private void convertMaterialsInSection(ConfigurationSection section, String materialKey) {
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection itemSection = section.getConfigurationSection(key);
            if (itemSection == null) continue;

            String material = itemSection.getString(materialKey);
            if (material == null) continue;

            String[] materialParts = material.split(":");
            if (!materialParts[0].equalsIgnoreCase("BASE64")) continue;

            material = material.replace(materialParts[0], "MCURL");
            itemSection.set(materialKey, material);
        }
    }


    public void convertCasesLocation() {
        ConfigurationSection cases = config.getCases().getConfigurationSection("DonateCase.Cases");
        if (cases != null) {
            for (String name : cases.getValues(false).keySet()) {
                if (cases.getString(name + ".location") == null) {
                    return;
                } else {
                    Location lv = cases.getLocation(name + ".location");
                    String world = "Undefined";
                    if (lv != null) {
                        if (lv.getWorld() != null) {
                            world = lv.getWorld().getName();
                        }
                        String location = world + ";" + lv.getX() + ";" + lv.getY() + ";" + lv.getZ() + ";" + lv.getPitch() + ";" + lv.getYaw();
                        config.getCases().set("DonateCase.Cases." + name + ".location", location);
                    }
                }
            }
        }
        config.getCases().set("config", "1.0");
        config.saveCases();
        Logger.log("&aConversion successful!");
    }

    public void convertOldCasesFormat() {
        ConfigurationSection cases = config.getConfig().getConfigurationSection("DonateCase.Cases");
        if (cases != null) {
            for (String caseName : cases.getKeys(false)) {
                File folder = new File(config.getPlugin().getDataFolder(), "cases");
                File caseFile;
                try {
                    caseFile = new File(folder, caseName + ".yml");
                    caseFile.createNewFile();
                    YamlConfiguration caseConfig = YamlConfiguration.loadConfiguration(caseFile);
                    caseConfig.set("case", config.getConfig().getConfigurationSection("DonateCase.Cases." + caseName));
                    String defaultMaterial = config.getConfig().getString("DonateCase.Cases." + caseName + ".Gui.GuiMaterial");
                    String defaultDisplayName = config.getConfig().getString("DonateCase.Cases." + caseName + ".Gui.GuiMaterialName");
                    boolean defaultEnchanted = config.getConfig().getBoolean("DonateCase.Cases." + caseName + ".Gui.GuiMaterialEnchant");
                    List<String> defaultLore = config.getConfig().getStringList("DonateCase.Cases." + caseName + ".Gui.GuiMaterialLore");
                    List<Integer> defaultSlots = new ArrayList<>();
                    defaultSlots.add(0);
                    defaultSlots.add(8);

                    String openMaterial = config.getConfig().getString("DonateCase.Cases." + caseName + ".Gui.GuiOpenCaseMaterial");
                    String openDisplayName = config.getConfig().getString("DonateCase.Cases." + caseName + ".Gui.DisplayName");
                    boolean openEnchanted = config.getConfig().getBoolean("DonateCase.Cases." + caseName + ".Gui.GuiOpenCaseMaterialEnchant");
                    List<String> openLore = config.getConfig().getStringList("DonateCase.Cases." + caseName + ".Gui.Lore");
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
        config.getConfig().set("DonateCase.Cases", null);
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

        String hasDonateCase = lang.getString("HasDonateCase");
        lang.set("HasDonateCase", null);
        lang.set("case-already-created", hasDonateCase);

        String addDonateCase = lang.getString("AddDonateCase");
        lang.set("AddDonateCase", null);
        lang.set("case-added", addDonateCase);

        String RemoveDonateCase = lang.getString("RemoveDonateCase");
        lang.set("RemoveDonateCase", null);
        lang.set("case-removed", RemoveDonateCase);

        String blockDontDonateCase = lang.getString("BlockDontDonateCase");
        lang.set("BlockDontDonateCase", null);
        lang.set("block-is-not-case", blockDontDonateCase);

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

        String destoryDonateCase = lang.getString("DestoryDonateCase");
        lang.set("DestoryDonateCase", null);
        lang.set("case-destroy-disallow", destoryDonateCase);

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

        String helpAddonsFormatAddonDescription = lang.getString("HelpAddons.Format.AddonDescription");
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
}