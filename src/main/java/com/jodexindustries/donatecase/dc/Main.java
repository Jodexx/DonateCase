package com.jodexindustries.donatecase.dc;

import com.jodexindustries.donatecase.api.AddonManager;
import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.events.DonateCaseDisableEvent;
import com.jodexindustries.donatecase.api.events.DonateCaseEnableEvent;
import com.jodexindustries.donatecase.listener.EventsListener;
import com.jodexindustries.donatecase.tools.*;
import com.jodexindustries.donatecase.tools.animations.*;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Main extends JavaPlugin {
    public static Main instance;
    public static AddonManager addonManager;
    public static Permission permission = null;
    public static boolean sql = true;

    public static Tools t;
    public static MySQL mysql;

    File langRu;
    File langEn;
    File langUa;
    public static CustomConfig customConfig;
    public static CasesConfig casesConfig;
    private boolean usePackets = true;

    public void onEnable() {
        long time = System.currentTimeMillis();
        instance = this;
        t = new Tools();
//        loadLibraries();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            (new Placeholder()).register();
            Logger.log("&aPlaceholders registered!");
        }

        setupConfigs();

        customConfig.getConfig().addDefault("DonatCase.NoKeyWarningSound", "ENTITY_ENDERMAN_TELEPORT");

        if(customConfig.getCases().getString("config") == null || !customConfig.getCases().getString("config", "").equalsIgnoreCase("1.0")) {
            Logger.log("Conversion of case locations to a new method of storage...");
            t.convertCasesLocation();
        }

        Bukkit.getPluginManager().registerEvents(new EventsListener(), this);
        if (customConfig.getConfig().getBoolean("DonatCase.UpdateChecker")) {
            new UpdateChecker(this, 106701).getVersion((version) -> {
                if (t.getPluginVersion(getDescription().getVersion()) >= t.getPluginVersion(version)) {
                    Logger.log("There is not a new update available.");
                } else {
                    Logger.log(ChatColor.GREEN + "There is a new update " + version +  " available.");
                    Logger.log(ChatColor.GREEN + "Download - https://www.spigotmc.org/resources/donatecase.106701/");
                }

            });
        }

        setupLangs();
        Metrics metrics = new Metrics(this, 18709);
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> customConfig.getConfig().getString("DonatCase.Languages")));

        sql = customConfig.getConfig().getBoolean("DonatCase.MySql.Enabled");
        if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            instance.setupPermissions();
        }
        if (sql) {
            String base = customConfig.getConfig().getString("DonatCase.MySql.DataBase");
            String port = customConfig.getConfig().getString("DonatCase.MySql.Port");
            String host = customConfig.getConfig().getString("DonatCase.MySql.Host");
            String user = customConfig.getConfig().getString("DonatCase.MySql.User");
            String password = customConfig.getConfig().getString("DonatCase.MySql.Password");
            (new BukkitRunnable() {
                public void run() {
                    Main.mysql = new MySQL(base, port, host, user, password);
                    if (!Main.mysql.hasTable("donate_cases")) {
                        Main.mysql.createTable();
                    }

                }
            }).runTaskTimerAsynchronously(instance, 0L, 12000L);
        }
        Objects.requireNonNull(getCommand("donatecase")).setExecutor(new CommandEx());
        Objects.requireNonNull(getCommand("donatecase")).setTabCompleter(new CommandEx());
        registerDefaultAnimations();
        DonateCaseEnableEvent donateCaseEnableEvent = new DonateCaseEnableEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(donateCaseEnableEvent);

        addonManager = new AddonManager();
        addonManager.loadAddons();

        Logger.log(ChatColor.GREEN + "Enabled in " + (System.currentTimeMillis() - time) + "ms");
    }

    public void onDisable() {
        DonateCaseDisableEvent donateCaseDisableEvent = new DonateCaseDisableEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(donateCaseDisableEvent);

        addonManager.disableAddons();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholder().unregister();
        }

        for (ArmorStand as : Case.armorStandList) {
            if (as != null) {
                as.remove();
            }
        }

        if (mysql != null) {
            mysql.close();
        }

    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = this.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
    }
    public void cleanCache() {
        for (ArmorStand as : Case.armorStandList) {
            if (as != null) {
                as.remove();
            }
        }
        Case.playersCases.clear();
        Case.caseData.clear();
        Case.activeCases.clear();
        Case.armorStandList.clear();
    }

    public void setupConfigs() {
        String[] files = {
                "Config.yml",
                "Cases.yml",
                "Keys.yml",
                "Animations.yml",
                "Data.yml",
                "lang/ru_RU.yml",
                "lang/en_US.yml",
                "lang/ua_UA.yml"
        };

        for (String file : files) {
            checkAndCreateFile(file);
        }

        customConfig = new CustomConfig();
        checkAndUpdateConfig(customConfig.getConfig(),"Config.yml", "config", "2.5");
        checkAndUpdateConfig(customConfig.getAnimations(), "Animations.yml", "config", "1.3");

        if(customConfig.getConfig().getConfigurationSection("DonatCase.Cases") != null) {
            new File(getDataFolder(), "cases").mkdir();
            Logger.log("&cOutdated cases format!");
            t.convertCases();
        } else {
            if(!new File(getDataFolder(), "cases").exists()) {
                new File(getDataFolder(), "cases").mkdir();
            }
            casesConfig = new CasesConfig();
            if(casesConfig.getCases().isEmpty()) {
                saveResource("cases/case.yml", false);
            }
        }
        casesConfig = new CasesConfig();
        usePackets = customConfig.getConfig().getBoolean("DonatCase.UsePackets") && getServer().getPluginManager().isPluginEnabled("ProtocolLib");

        loadCases();
    }

    private void checkAndCreateFile(String fileName) {
        if (!(new File(this.getDataFolder(), fileName)).exists()) {
            this.saveResource(fileName, false);
        }
    }

    private void checkAndUpdateConfig(YamlConfiguration config, String fileName, String configKey, String expectedValue) {
        if (config.getString(configKey) == null || !config.getString(configKey, "").equals(expectedValue)) {
            Logger.log("&cOutdated " + fileName + "! Creating a new!");
            File configFile = new File(this.getDataFolder(), fileName);
            configFile.renameTo(new File(this.getDataFolder(), fileName + ".old"));
            this.saveResource(fileName, false);
            customConfig = new CustomConfig();
        }
    }


    public void setupLangs() {
        if (customConfig.getLang().getString("config") == null || !customConfig.getLang().getString("config", "").equals("2.5")) {
            Logger.log("&cOutdated lang config! Creating a new!");
            langRu = new File(this.getDataFolder(), "lang/ru_RU.yml");
            langRu.renameTo(new File(this.getDataFolder(), "lang/ru_RU.yml.old"));
            this.saveResource("lang/ru_RU.yml", false);
            langEn = new File(this.getDataFolder(), "lang/en_US.yml");
            langEn.renameTo(new File(this.getDataFolder(), "lang/en_US.yml.old"));
            this.saveResource("lang/en_US.yml", false);
            langUa = new File(this.getDataFolder(), "lang/ua_UA.yml");
            langUa.renameTo(new File(this.getDataFolder(), "lang/ua_UA.yml.old"));
            this.saveResource("lang/ua_UA.yml", false);
            customConfig = new CustomConfig();
        }
    }

    private void registerDefaultAnimations() {
        AnimationManager.registerAnimation("SHAPE", ShapeAnimation.class);
        AnimationManager.registerAnimation("WHEEL", WheelAnimation.class);
        AnimationManager.registerAnimation("RAINLY", RainlyAnimation.class);
        AnimationManager.registerAnimation("FIREWORK", FireworkAnimation.class);
        AnimationManager.registerAnimation("FULLWHEEL", FullWheelAnimation.class);
        Logger.log("&aRegistered &adefault animations");
    }

    private void loadCases() {
        Case.caseData.clear();
        for (String caseName : casesConfig.getCases().keySet()) {
            YamlConfiguration config = casesConfig.getCase(caseName);
            String caseTitle = t.rc(config.getString("case.Title"));
            String caseDisplayName = config.getString("case.DisplayName");
            caseDisplayName = caseDisplayName == null ? "" : t.rc(caseDisplayName);
            String animationName = config.getString("case.Animation");

            String animationSound = config.getString("case.AnimationSound", "NULL").toUpperCase();
            float volume = (float) config.getDouble("case.Sound.Volume");
            float pitch = (float) config.getDouble("case.Sound.Pitch");
            Sound bukkitSound = Sound.valueOf(animationSound);
            CaseData.AnimationSound sound = new CaseData.AnimationSound(bukkitSound,volume, pitch);

            Map<String, CaseData.Item> items = new HashMap<>();
            for (String item : config.getConfigurationSection("case.Items").getKeys(false)) {
                String group = config.getString("case.Items." + item + ".Group");
                int chance = config.getInt("case.Items." + item + ".Chance");
                String giveType = config.getString("case.Items." + item + ".GiveType", "ONE");

                // get actions
                List<String> actions = config.getStringList("case.Items." + item + ".Actions");

                // get random actions
                Map<String, CaseData.Item.RandomAction> randomActions = new HashMap<>();
                if(config.getConfigurationSection("case.Items." + item + ".RandomActions") != null) {
                    for (String randomAction : config.getConfigurationSection("case.Items." + item + ".RandomActions").getKeys(false)) {
                        int actionChance = config.getInt("case.Items." + item + ".RandomActions." + randomAction + ".Chance");
                        List<String> randomActionsList = config.getStringList("case.Items." + item + ".RandomActions." + randomAction + ".Actions");
                        CaseData.Item.RandomAction randomActionObject = new CaseData.Item.RandomAction(actionChance, randomActionsList);
                        randomActions.put(randomAction, randomActionObject);
                    }
                }

                // get rgb
                String[] rgb = null;
                String rgbString = config.getString("case.Items." + item + ".Item.Rgb");
                if(rgbString != null) {
                    rgb = rgbString.replaceAll(" ", "").split(",");
                }

                // get material
                String id = config.getString("case.Items." + item + ".Item.ID");
                String itemDisplayName = t.rc(config.getString("case.Items." + item + ".Item.DisplayName"));
                boolean enchanted = config.getBoolean("case.Items." + item + ".Item.Enchanted");
                ItemStack itemStack = t.getCaseItem(itemDisplayName, id, enchanted, rgb);
                CaseData.Item.Material material = new CaseData.Item.Material(id, itemStack, itemDisplayName, enchanted);

                CaseData.Item caseItem = new CaseData.Item(item, group, chance, material, giveType, actions, randomActions, rgb);
                items.put(item, caseItem);
            }
            CaseData.HistoryData[] historyData = new CaseData.HistoryData[10];
            if(customConfig.getData().getConfigurationSection("Data") != null &&
                    customConfig.getData().getConfigurationSection("Data." + caseName) != null) {
                for (String i : customConfig.getData().getConfigurationSection("Data." + caseName).getKeys(false)) {
                    CaseData.HistoryData data = new CaseData.HistoryData(
                            customConfig.getData().getString("Data." + caseName + "." + i + ".Item"),
                            caseName,
                            customConfig.getData().getString("Data." + caseName + "." + i + ".Player"),
                            customConfig.getData().getLong("Data." + caseName + "." + i + ".Time"),
                            customConfig.getData().getString("Data." + caseName + "." + i + ".Group"),
                            customConfig.getData().getString("Data." + caseName + "." + i + ".Action"));
                    historyData[Integer.parseInt(i)] = data;
                }
            }
            CaseData caseData = new CaseData(caseName, caseDisplayName, caseTitle,animationName, sound, items, historyData);
            Case.caseData.put(caseName, caseData);
        }
        Logger.log("&aCases loaded!");
    }
    public static Permission getPermissions() {
        return permission;
    }


    public boolean isUsePackets() {
        return usePackets;
    }
//    private void loadLibraries() {
//        BukkitLibraryManager libraryManager = new BukkitLibraryManager(this);
//        Library lib = Library.builder()
//                .groupId("com{}github{}justadeni{}standapi")
//                .artifactId("StandAPI")
//                .version("v1.8")
//                .isolatedLoad(true)
//                .build();
//        libraryManager.addRepository("https://repo.jodexindustries.space/releases");
//        libraryManager.addMavenCentral();
//        libraryManager.loadLibrary(lib);
//
//    }
}
