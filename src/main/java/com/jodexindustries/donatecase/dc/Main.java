package com.jodexindustries.donatecase.dc;

import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.listener.EventsListener;
import com.jodexindustries.donatecase.tools.*;
import com.jodexindustries.donatecase.tools.animations.FireworkShape;
import com.jodexindustries.donatecase.tools.animations.Rainly;
import com.jodexindustries.donatecase.tools.animations.Shape;
import com.jodexindustries.donatecase.tools.animations.Wheel;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
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
    public static boolean Tconfig = true;
    public static Tools t;
    public static FileConfiguration lang;
    public static MySQL mysql;

    File ConfigFile;
    File AnimationsFile;
    File langRu;
    File langEn;
    File langUa;
    public static CustomConfig customConfig;

    public void onEnable() {
        instance = this;
        customConfig = new CustomConfig();
        t = new Tools();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            (new Placeholder()).register();
            Bukkit.getServer().getConsoleSender().sendMessage(t.rc("[DonateCase] &aPlaceholders registered!"));
        }

        setupConfigs();

        customConfig.getConfig().addDefault("DonatCase.NoKeyWarningSound", "ENTITY_ENDERMAN_TELEPORT");

        if(customConfig.getCases().getString("config") == null || !customConfig.getCases().getString("config").equalsIgnoreCase("1.0")) {
            getLogger().info("Conversion of case locations to a new method of storage...");
            t.convertCasesLocation();
        }

        Bukkit.getPluginManager().registerEvents(new EventsListener(), this);
        if (customConfig.getConfig().getBoolean("DonatCase.UpdateChecker")) {
            new UpdateChecker(this, 106701).getVersion((version) -> {
                if (getDescription().getVersion().equals(version)) {
                    getLogger().info("There is not a new update available.");
                } else {
                    getLogger().info("There is a new update " + version +  " available.");
                    getLogger().info("Download - https://www.spigotmc.org/resources/donatecase.106701/");
                }

            });
        }

        setupLangs();
        Metrics metrics = new Metrics(this, 18709);
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> customConfig.getConfig().getString("DonatCase.Languages")));

        Tconfig = customConfig.getConfig().getString("DonatCase.MySql.Enabled").equalsIgnoreCase("false");
        instance.setupPermissions();
        if (!Tconfig) {
            String base = customConfig.getConfig().getString("DonatCase.MySql.DataBase");
            String port = customConfig.getConfig().getString("DonatCase.MySql.Port");
            String hostname = customConfig.getCases().getString("DonatCase.MySql.Host");
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
    }

    public void onDisable() {
        new Placeholder().unregister();

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
            Bukkit.getServer().getConsoleSender().sendMessage(t.rc("[DonateCase] &cOutdated Config.yml! Creating a new!"));
            ConfigFile = new File(this.getDataFolder(), "Config.yml");
            ConfigFile.renameTo(new File(this.getDataFolder(), "Config.yml.old"));
            this.saveResource("Config.yml", false);
            customConfig = new CustomConfig();

        }

        if (!customConfig.getConfig().getString("config").equals("2.5")) {
            Bukkit.getServer().getConsoleSender().sendMessage(t.rc("[DonateCase] &cOutdated Config.yml! Creating a new!"));
            ConfigFile = new File(this.getDataFolder(), "Config.yml");
            ConfigFile.renameTo(new File(this.getDataFolder(), "Config.yml.old"));
            this.saveResource("Config.yml", false);
            customConfig = new CustomConfig();
        }
        customConfig.getConfig().options().copyDefaults(true);
        customConfig.saveConfig();
        // Animations.yml ver check
        if (customConfig.getAnimations().getString("config") == null) {
            Bukkit.getServer().getConsoleSender().sendMessage(t.rc("[DonateCase] &cOutdated Animations.yml! Creating a new!"));
            AnimationsFile = new File(this.getDataFolder(), "Animations.yml");
            AnimationsFile.renameTo(new File(this.getDataFolder(), "Animations.yml.old"));
            this.saveResource("Animations.yml", false);
            customConfig = new CustomConfig();
        }

        if (!customConfig.getAnimations().getString("config").equals("1.1")) {
            Bukkit.getServer().getConsoleSender().sendMessage(t.rc("[DonateCase] &cOutdated Animations.yml! Creating a new!"));
            AnimationsFile = new File(this.getDataFolder(), "Animations.yml");
            AnimationsFile.renameTo(new File(this.getDataFolder(), "Animations.yml.old"));
            this.saveResource("Animations.yml", false);
            customConfig = new CustomConfig();
        }
    }

    public void setupLangs() {
        lang = (new Languages(customConfig.getConfig().getString("DonatCase.Languages"))).getLang();
        if (lang.getString("config") == null || !lang.getString("config").equals("2.4")) {
            Bukkit.getServer().getConsoleSender().sendMessage(t.rc("[DonateCase] &cOutdated lang config! Creating a new!"));
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
        AnimationManager.registerAnimation("SHAPE", Shape.class);
        AnimationManager.registerAnimation("WHEEL", Wheel.class);
        AnimationManager.registerAnimation("RAINLY", Rainly.class);
        AnimationManager.registerAnimation("FIREWORK", FireworkShape.class);
    }
    public static Permission getPermissions() {
        return permission;
    }
}