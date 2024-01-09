package com.jodexindustries.donatecase.dc;

import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.HistoryData;
import com.jodexindustries.donatecase.api.events.DonateCaseDisableEvent;
import com.jodexindustries.donatecase.api.events.DonateCaseEnableEvent;
import com.jodexindustries.donatecase.listener.EventsListener;
import com.jodexindustries.donatecase.tools.*;
import com.jodexindustries.donatecase.tools.animations.*;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Objects;

public class Main extends JavaPlugin {
    public static Main instance;
    public static Permission permission = null;
    public static boolean sql = true;

    public static Tools t;
    public static FileConfiguration lang;
    public static MySQL mysql;

    File ConfigFile;
    File AnimationsFile;
    File langRu;
    File langEn;
    File langUa;
    public static CustomConfig customConfig;
    public static CasesConfig casesConfig;
    private boolean usePackets = true;

    public void onEnable() {
        long time = System.currentTimeMillis();
        instance = this;
        customConfig = new CustomConfig();
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
            String hostname = customConfig.getConfig().getString("DonatCase.MySql.Host");
            final String host = "jdbc:mysql://" + hostname + ":" + port + "/" + base;
            final String user = customConfig.getConfig().getString("DonatCase.MySql.User");
            final String password = customConfig.getConfig().getString("DonatCase.MySql.Password");
            (new BukkitRunnable() {
                public void run() {
                    Main.mysql = new MySQL(host, user, password);
                    if (!Main.mysql.hasTable("donate_cases")) {
                        Main.mysql.createTable();
                    }

                }
            }).runTaskTimer(instance, 0L, 12000L);
        }
        Objects.requireNonNull(getCommand("donatecase")).setExecutor(new CommandEx());
        Objects.requireNonNull(getCommand("donatecase")).setTabCompleter(new CommandEx());
        registerDefaultAnimations();

        Logger.log(ChatColor.GREEN + "Enabled in " + (System.currentTimeMillis() - time) + "ms");
        DonateCaseEnableEvent donateCaseEnableEvent = new DonateCaseEnableEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(donateCaseEnableEvent);
    }

    public void onDisable() {
        DonateCaseDisableEvent donateCaseDisableEvent = new DonateCaseDisableEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(donateCaseDisableEvent);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholder().unregister();
        }

        for (ArmorStand as : Case.listAR) {
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

    public void setupConfigs() {
        if (!(new File(this.getDataFolder(), "Config.yml")).exists()) {
            this.saveResource("Config.yml", false);
        }

        if (!(new File(this.getDataFolder(), "Cases.yml")).exists()) {
            this.saveResource("Cases.yml", false);
        }

        if (!(new File(this.getDataFolder(), "Keys.yml")).exists()) {
            this.saveResource("Keys.yml", false);
        }

        if (!(new File(this.getDataFolder(), "Animations.yml")).exists()) {
            this.saveResource("Animations.yml", false);
        }

        if (!(new File(this.getDataFolder(), "Data.yml")).exists()) {
            this.saveResource("Data.yml", false);
        }

        if (!(new File(this.getDataFolder(), "lang/ru_RU.yml")).exists()) {
            this.saveResource("lang/ru_RU.yml", false);
        }

        if (!(new File(this.getDataFolder(), "lang/en_US.yml")).exists()) {
            this.saveResource("lang/en_US.yml", false);
        }

        if (!(new File(this.getDataFolder(), "lang/ua_UA.yml")).exists()) {
            this.saveResource("lang/ua_UA.yml", false);
        }

        customConfig = new CustomConfig();
        // Config.yml ver check
        if (customConfig.getConfig().getString("config") == null) {
            Logger.log("&cOutdated Config.yml! Creating a new!");
            ConfigFile = new File(this.getDataFolder(), "Config.yml");
            ConfigFile.renameTo(new File(this.getDataFolder(), "Config.yml.old"));
            this.saveResource("Config.yml", false);
            customConfig = new CustomConfig();

        }

        if (!customConfig.getConfig().getString("config", "").equals("2.5")) {
            Logger.log("&cOutdated Config.yml! Creating a new!");
            ConfigFile = new File(this.getDataFolder(), "Config.yml");
            ConfigFile.renameTo(new File(this.getDataFolder(), "Config.yml.old"));
            this.saveResource("Config.yml", false);
            customConfig = new CustomConfig();
        }
        // Animations.yml ver check
        if (customConfig.getAnimations().getString("config") == null) {
            Logger.log("&cOutdated Animations.yml! Creating a new!");
            AnimationsFile = new File(this.getDataFolder(), "Animations.yml");
            AnimationsFile.renameTo(new File(this.getDataFolder(), "Animations.yml.old"));
            this.saveResource("Animations.yml", false);
            customConfig = new CustomConfig();
        }

        if (!customConfig.getAnimations().getString("config", "").equals("1.3")) {
            Logger.log("&cOutdated Animations.yml! Creating a new!");
            AnimationsFile = new File(this.getDataFolder(), "Animations.yml");
            AnimationsFile.renameTo(new File(this.getDataFolder(), "Animations.yml.old"));
            this.saveResource("Animations.yml", false);
            customConfig = new CustomConfig();
        }
        if(customConfig.getData().getConfigurationSection("Data") != null) {
            for (String c : customConfig.getData().getConfigurationSection("Data").getKeys(false)) {
                HistoryData[] historyData = new HistoryData[10];
                for (String i : customConfig.getData().getConfigurationSection("Data." + c).getKeys(false)) {
                    HistoryData data = new HistoryData(c, customConfig.getData().getString("Data." + c + "." + i + ".Player"),
                            customConfig.getData().getLong("Data." + c + "." + i + ".Time"),
                            customConfig.getData().getString("Data." + c + "." + i + ".Group"), customConfig.getData().getString("Data." + c + "." + i + ".Action"));
                    historyData[Integer.parseInt(i)] = data;
                }
                Case.historyData.put(c, historyData);
            }
        }
        Logger.log("&aHistory data loaded!");

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


    }

    public void setupLangs() {
        lang = (new Languages(customConfig.getConfig().getString("DonatCase.Languages"))).getLang();
        if (lang.getString("config") == null || !lang.getString("config", "").equals("2.5")) {
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
            lang = (new Languages(customConfig.getConfig().getString("DonatCase.Languages"))).getLang();
        }
    }

    private void registerDefaultAnimations() {
        AnimationManager.registerAnimation("SHAPE", ShapeAnimation.class);
        AnimationManager.registerAnimation("WHEEL", WheelAnimation.class);
        AnimationManager.registerAnimation("RAINLY", RainlyAnimation.class);
        AnimationManager.registerAnimation("FIREWORK", FireworkAnimation.class);
        AnimationManager.registerAnimation("FULLWHEEL", FullWheelAnimation.class);
        Logger.log("&aRegistered &c5 &adefault animations");
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
