package com.jodexindustries.donatecase;

import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.j256.ormlite.logger.Level;
import com.jodexindustries.donatecase.api.AddonManager;
import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.events.DonateCaseDisableEvent;
import com.jodexindustries.donatecase.api.events.DonateCaseEnableEvent;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import com.jodexindustries.donatecase.api.holograms.types.CMIHologramsSupport;
import com.jodexindustries.donatecase.api.holograms.types.DecentHologramsSupport;
import com.jodexindustries.donatecase.api.holograms.types.HolographicDisplaysSupport;
import com.jodexindustries.donatecase.commands.GlobalCommand;
import com.jodexindustries.donatecase.database.CaseDataBase;
import com.jodexindustries.donatecase.listener.EventsListener;
import com.jodexindustries.donatecase.tools.*;
import com.jodexindustries.donatecase.tools.animations.*;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
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

public class DonateCase extends JavaPlugin {
    public static DonateCase instance;
    public static AddonManager addonManager;
    public static Permission permission = null;
    public static boolean sql = true;

    public static Tools t;
    public static CaseDataBase mysql;
    public static HologramManager hologramManager = null;

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
        loadPlaceholderAPI();

        loadLibraries();

        setupConfigs();

        Bukkit.getPluginManager().registerEvents(new EventsListener(), this);

        loadUpdater();

        setupLangs();

        setupPermissions();


        Metrics metrics = new Metrics(this, 18709);
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> customConfig.getConfig().getString("DonatCase.Languages")));

        setupMySQL();

        PluginCommand command = getCommand("donatecase");
        if(command != null) {
            command.setExecutor(new GlobalCommand());
            command.setTabCompleter(new GlobalCommand());
        }

        registerDefaultAnimations();

        addonManager = new AddonManager();
        addonManager.loadAddons();

        loadHologramManager();

        loadHolograms();

        DonateCaseEnableEvent donateCaseEnableEvent = new DonateCaseEnableEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(donateCaseEnableEvent);

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
        if(hologramManager != null) hologramManager.removeAllHolograms();

    }
    private void setupMySQL() {
        sql = customConfig.getConfig().getBoolean("DonatCase.MySql.Enabled");
        if (sql) {
            String base = customConfig.getConfig().getString("DonatCase.MySql.DataBase");
            String port = customConfig.getConfig().getString("DonatCase.MySql.Port");
            String host = customConfig.getConfig().getString("DonatCase.MySql.Host");
            String user = customConfig.getConfig().getString("DonatCase.MySql.User");
            String password = customConfig.getConfig().getString("DonatCase.MySql.Password");
            (new BukkitRunnable() {
                public void run() {
                    DonateCase.mysql = new CaseDataBase(instance, base, port, host, user, password);
                }
            }).runTaskTimerAsynchronously(instance, 0L, 12000L);
            com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.WARNING);
        }
    }

    private void setupPermissions() {
        if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Permission> permissionProvider = this.getServer().getServicesManager().getRegistration(Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
            }
        }
    }
    private void loadPlaceholderAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            (new Placeholder()).register();
            Logger.log("&aPlaceholders registered!");
        }
    }
    private void loadUpdater() {
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

        customConfig.getConfig().addDefault("DonatCase.NoKeyWarningSound", "ENTITY_ENDERMAN_TELEPORT");

        if(customConfig.getCases().getString("config") == null || !customConfig.getCases().getString("config", "").equalsIgnoreCase("1.0")) {
            Logger.log("Conversion of case locations to a new method of storage...");
            t.convertCasesLocation();
        }
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
        AnimationManager.registerAnimation("SHAPE", new ShapeAnimation());
        AnimationManager.registerAnimation("WHEEL", new WheelAnimation());
        AnimationManager.registerAnimation("RAINLY", new RainlyAnimation());
        AnimationManager.registerAnimation("FIREWORK", new FireworkAnimation());
        AnimationManager.registerAnimation("FULLWHEEL", new FullWheelAnimation());
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

            boolean hologramEnabled = config.getBoolean("case.Hologram.Toggle");
            double hologramHeight = config.getDouble("case.Hologram.Height");
            int range = config.getInt("case.Hologram.Range");
            List<String> hologramMessage = config.getStringList("case.Hologram.Message");
            CaseData.Hologram hologram = hologramEnabled ? new CaseData.Hologram(true, hologramHeight, range, hologramMessage) : new CaseData.Hologram();

            Map<String, CaseData.Item> items = new HashMap<>();
            for (String item : config.getConfigurationSection("case.Items").getKeys(false)) {
                String group = config.getString("case.Items." + item + ".Group");
                int chance = config.getInt("case.Items." + item + ".Chance");
                String giveType = config.getString("case.Items." + item + ".GiveType", "ONE");

                // get actions
                List<String> actions = config.getStringList("case.Items." + item + ".Actions");

                // get alternative actions
                List<String> alternativeActions = config.getStringList("case.Items." + item + ".AlternativeActions");

                // get random actions
                Map<String, CaseData.Item.RandomAction> randomActions = new HashMap<>();
                if(config.getConfigurationSection("case.Items." + item + ".RandomActions") != null) {
                    for (String randomAction : config.getConfigurationSection("case.Items." + item + ".RandomActions").getKeys(false)) {
                        int actionChance = config.getInt("case.Items." + item + ".RandomActions." + randomAction + ".Chance");
                        List<String> randomActionsList = config.getStringList("case.Items." + item + ".RandomActions." + randomAction + ".Actions");
                        String displayName = config.getString("case.Items." + item + ".RandomActions." + randomAction + ".DisplayName");
                        CaseData.Item.RandomAction randomActionObject = new CaseData.Item.RandomAction(actionChance, randomActionsList, displayName);
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

                CaseData.Item caseItem = new CaseData.Item(item, group, chance, material, giveType, actions, randomActions, rgb, alternativeActions);
                items.put(item, caseItem);
            }
            CaseData.HistoryData[] historyData = new CaseData.HistoryData[10];
            if(!sql) {
                if (customConfig.getData().getConfigurationSection("Data") != null &&
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
            }
            CaseData caseData = new CaseData(caseName, caseDisplayName, caseTitle,animationName, sound, items, historyData, hologram);
            Case.caseData.put(caseName, caseData);
        }
        Logger.log("&aCases loaded!");
    }
    public static Permission getPermissions() {
        return permission;
    }

    private void loadHologramManager() {
        if(Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
            hologramManager = new DecentHologramsSupport();
        } else if(Bukkit.getPluginManager().isPluginEnabled("CMI") && CMIModule.holograms.isEnabled()) {
            hologramManager = new CMIHologramsSupport();
        } else if(Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")){
            hologramManager = new HolographicDisplaysSupport();
        }
    }
    public void loadHolograms() {
        ConfigurationSection section = customConfig.getCases().getConfigurationSection("DonatCase.Cases");
        if(section == null || section.getKeys(false).isEmpty()) return;
        for (String caseName : section.getKeys(false)) {
            String caseType = Case.getCaseTypeByCustomName(caseName);
            CaseData caseData = Case.getCase(caseType);
            Location location = Case.getCaseLocationByCustomName(caseName);
            if (caseData != null && caseData.getHologram().isEnabled() && location != null && hologramManager != null) {
                hologramManager.createHologram(location.getBlock(), caseData);
            }
        }
    }


    public boolean isUsePackets() {
        return usePackets;
    }

    private void loadLibraries() {
        Library lib = Library.builder()
                .groupId("com{}j256{}ormlite")
                .artifactId("ormlite-jdbc")
                .version("6.1")
                .id("ormlite")
                .build();
        BukkitLibraryManager bukkitLibraryManager = new BukkitLibraryManager(this);
        bukkitLibraryManager.addMavenCentral();
        bukkitLibraryManager.loadLibrary(lib);

    }
}
