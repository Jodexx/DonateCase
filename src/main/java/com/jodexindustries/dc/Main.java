package com.jodexindustries.dc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.jodexindustries.listener.EventsListener;
import com.jodexindustries.tools.*;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin {
    public static Main instance;
    public static Permission permission = null;
    public static boolean Tconfig = true;
    public static List<ArmorStand> listAR = new ArrayList<>();
    public static HashMap<Player, Location> openCase = new HashMap<>();
    public static HashMap<Location, String> ActiveCase = new HashMap<>();
    public static Tools t;
    public static FileConfiguration lang;
    public static MySQL mysql;

    public Main() {
        instance = this;
    }
    File ConfigFile;
    File AnimationsFile;
    File langRu;
    File langEn;
    File langUa;

    public void onEnable() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            (new Placeholder()).register();
            Bukkit.getServer().getConsoleSender().sendMessage("[DonateCase] §aPlaceholders registered!");
        }

        t = new Tools();
        setupConfigs();


        Bukkit.getPluginManager().registerEvents(new EventsListener(), this);
        if (CustomConfig.getConfig().getBoolean("DonatCase.UpdateChecker")) {
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


        Tconfig = CustomConfig.getConfig().getString("DonatCase.MySql.Enabled").equalsIgnoreCase("false");
        instance.setupPermissions();
        if (!Tconfig) {
            String base = CustomConfig.getConfig().getString("DonatCase.MySql.DataBase");
            String port = CustomConfig.getConfig().getString("DonatCase.MySql.Port");
            String hostname = CustomConfig.getCases().getString("DonatCase.MySql.Host");
            final String host = "jdbc:mysql://" + hostname + ":" + port + "/" + base;
            final String user = CustomConfig.getConfig().getString("DonatCase.MySql.User");
            final String password = CustomConfig.getConfig().getString("DonatCase.MySql.Password");
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
    }

    public void onDisable() {
        new Placeholder().unregister();

        for (ArmorStand as : listAR) {
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

        CustomConfig.setup();
        // Config.yml ver check
        if (CustomConfig.getConfig().getString("config") == null) {
            Bukkit.getServer().getConsoleSender().sendMessage("[DonateCase] §cOutdated Config.yml! Creating a new!");
            ConfigFile = new File(this.getDataFolder(), "Config.yml");
            ConfigFile.renameTo(new File(this.getDataFolder(), "Config.yml.old"));
            this.saveResource("Config.yml", false);
            CustomConfig.setup();
        }

        if (!CustomConfig.getConfig().getString("config").equals("2.2")) {
            Bukkit.getServer().getConsoleSender().sendMessage("[DonateCase] §cOutdated Config.yml! Creating a new!");
            ConfigFile = new File(this.getDataFolder(), "Config.yml");
            ConfigFile.renameTo(new File(this.getDataFolder(), "Config.yml.old"));
            this.saveResource("Config.yml", false);
            CustomConfig.setup();
        }
        // Animations.yml ver check
        if (CustomConfig.getAnimations().getString("config") == null) {
            Bukkit.getServer().getConsoleSender().sendMessage("[DonateCase] §cOutdated Animations.yml! Creating a new!");
            AnimationsFile = new File(this.getDataFolder(), "Animations.yml");
            AnimationsFile.renameTo(new File(this.getDataFolder(), "Animations.yml.old"));
            this.saveResource("Animations.yml", false);
            CustomConfig.setup();
        }

        if (!CustomConfig.getAnimations().getString("config").equals("1.0")) {
            Bukkit.getServer().getConsoleSender().sendMessage("[DonateCase] §cOutdated Animations.yml! Creating a new!");
            AnimationsFile = new File(this.getDataFolder(), "Animations.yml");
            AnimationsFile.renameTo(new File(this.getDataFolder(), "Animations.yml.old"));
            this.saveResource("Animations.yml", false);
            CustomConfig.setup();
        }
    }

    public void setupLangs() {
        lang = (new Languages(CustomConfig.getConfig().getString("DonatCase.Languages"))).getLang();
        if (lang.getString("config") == null || !lang.getString("config").equals("2.4")) {
            Bukkit.getServer().getConsoleSender().sendMessage("[DonateCase] §cOutdated lang config! Creating a new!");
            langRu = new File(this.getDataFolder(), "lang/ru_RU.yml");
            langRu.renameTo(new File(this.getDataFolder(), "lang/ru_RU.yml.old"));
            this.saveResource("lang/ru_RU.yml", false);
            langEn = new File(this.getDataFolder(), "lang/en_US.yml");
            langEn.renameTo(new File(this.getDataFolder(), "lang/en_US.yml.old"));
            this.saveResource("lang/en_US.yml", false);
            langUa = new File(this.getDataFolder(), "lang/ua_UA.yml");
            langUa.renameTo(new File(this.getDataFolder(), "lang/ua_UA.yml.old"));
            this.saveResource("lang/ua_UA.yml", false);
            CustomConfig.setup();
            lang = (new Languages(CustomConfig.getConfig().getString("DonatCase.Languages"))).getLang();
        }
    }
    public static Permission getPermissions() {
        return permission;
    }
}
