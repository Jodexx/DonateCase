package com.jodexindustries.donatecase;

import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.j256.ormlite.logger.Level;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.CaseManager;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.HologramDriver;
import com.jodexindustries.donatecase.api.data.PermissionDriver;
import com.jodexindustries.donatecase.api.events.DonateCaseDisableEvent;
import com.jodexindustries.donatecase.api.events.DonateCaseEnableEvent;
import com.jodexindustries.donatecase.api.events.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import com.jodexindustries.donatecase.api.holograms.types.CMIHologramsSupport;
import com.jodexindustries.donatecase.api.holograms.types.DecentHologramsSupport;
import com.jodexindustries.donatecase.api.holograms.types.HolographicDisplaysSupport;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.command.subcommands.*;
import com.jodexindustries.donatecase.database.sql.MySQLDataBase;
import com.jodexindustries.donatecase.listener.EventsListener;
import com.jodexindustries.donatecase.tools.*;
import com.jodexindustries.donatecase.tools.animations.*;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

/**
 * Main DonateCase class for loading
 */
public class DonateCase extends JavaPlugin {
    public static DonateCase instance;

    public MySQLDataBase mysql;
    public HologramManager hologramManager = null;
    public PAPISupport papi = null;
    public LuckPerms luckPerms = null;
    public Permission permission = null;
    public PermissionDriver permissionDriver = null;
    public HologramDriver hologramDriver = null;
    public CaseManager api;
    public CaseLoader loader;
    public BukkitLibraryManager libraryManager;

    public CustomConfig customConfig;
    public CasesConfig casesConfig;

    private boolean usePackets = true;
    public boolean sql = false;

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        instance = this;
        api = new CaseManager(this);
        loader = new CaseLoader(this);

        loadLibraries();

        setupConfigs();

        loadUpdater();

        loadPermissionDriver();

        loadMetrics();

        registerDefaultCommand();
        registerDefaultSubCommands();
        registerDefaultAnimations();

        loadHologramManager();

        loadHolograms();

        loadPlaceholderAPI();

        api.getAddonManager().loadAddons();

        DonateCaseEnableEvent donateCaseEnableEvent = new DonateCaseEnableEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(donateCaseEnableEvent);
        Bukkit.getPluginManager().registerEvents(new EventsListener(), this);

        Logger.log(ChatColor.GREEN + "Enabled in " + (System.currentTimeMillis() - time) + "ms");
    }


    @Override
    public void onDisable() {
        DonateCaseDisableEvent donateCaseDisableEvent = new DonateCaseDisableEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(donateCaseDisableEvent);
        if(api.getAddonManager() != null) api.getAddonManager().unloadAddons();

        api.getAnimationManager().unregisterAnimations();
        api.getSubCommandManager().unregisterSubCommands();

        papi.unregister();

        if (mysql != null) {
            mysql.close();
        }
        if(hologramManager != null) hologramManager.removeAllHolograms();
        cleanCache();
    }
    private void setupMySQL() {
        ConfigurationSection mysqlSection = customConfig.getConfig().getConfigurationSection("DonatCase.MySql");
        if(mysqlSection == null) return;
        sql = mysqlSection.getBoolean("Enabled");
        if (sql) {
            String base = mysqlSection.getString("DataBase");
            String port = mysqlSection.getString("Port");
            String host = mysqlSection.getString("Host");
            String user = mysqlSection.getString("User");
            String password = mysqlSection.getString("Password");
            (new BukkitRunnable() {
                public void run() {
                    mysql = new MySQLDataBase(instance, base, port, host, user, password);
                }
            }).runTaskTimerAsynchronously(instance, 0L, 12000L);
            com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.WARNING);
        }
    }
    private void loadPlaceholderAPI() {
        papi = new PAPISupport();
        papi.register();
    }
    private void loadUpdater() {
        if (customConfig.getConfig().getBoolean("DonatCase.UpdateChecker")) {
            new UpdateChecker(this, 106701).getVersion((version) -> {
                if (Tools.getPluginVersion(getDescription().getVersion()) < Tools.getPluginVersion(version)) {
                    Logger.log(ChatColor.GREEN + "There is a new update " + version +  " available.");
                    Logger.log(ChatColor.GREEN + "Download - https://www.spigotmc.org/resources/donatecase.106701/");
                }
            });
        }
    }
    public void cleanCache() {
        Bukkit.getWorlds().forEach(world -> world.getEntitiesByClass(ArmorStand.class).stream().filter(stand -> stand.hasMetadata("case")).forEachOrdered(Entity::remove));
        Case.playersGui.clear();
        Case.caseData.clear();
        Case.activeCases.clear();
        Case.activeCasesByLocation.clear();
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
        checkAndUpdateConfig(customConfig.getConfig(), "Config.yml", "2.5");
        checkAndUpdateConfig(customConfig.getAnimations(), "Animations.yml", "1.3");

        if(customConfig.getConfig().getConfigurationSection("DonatCase.Cases") != null) {
            new File(getDataFolder(), "cases").mkdir();
            Logger.log("&cOutdated cases format!");
            Converter.convertCases();
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

        Converter.convertBASE64(this);

        setupMySQL();

        loader.load();

        customConfig.getConfig().addDefault("DonatCase.NoKeyWarningSound", "ENTITY_ENDERMAN_TELEPORT");

        if(customConfig.getCases().getString("config") == null || !customConfig.getCases().getString("config", "").equalsIgnoreCase("1.0")) {
            Logger.log("Conversion of case locations to a new method of storage...");
            Converter.convertCasesLocation();
        }

        checkLanguageVersion();

        DonateCaseReloadEvent reloadEvent = new DonateCaseReloadEvent(this);
        Bukkit.getPluginManager().callEvent(reloadEvent);
    }

    private void checkAndCreateFile(String fileName) {
        if (!(new File(getDataFolder(), fileName)).exists()) {
            saveResource(fileName, false);
        }
    }

    private void checkAndUpdateConfig(YamlConfiguration config, String fileName, String expectedValue) {
        if (config.getString("config") == null || !config.getString("config", "").equals(expectedValue)) {
            Logger.log("&cOutdated " + fileName + "! Creating a new!");
            File configFile = new File(getDataFolder(), fileName);
            configFile.renameTo(new File(getDataFolder(), fileName + ".old"));
            saveResource(fileName, false);
            customConfig = new CustomConfig();
        }
    }


    private void checkLanguageVersion() {
        String version = customConfig.getLang().getString("config");
        if (version == null || !version.equalsIgnoreCase("2.6")) {
            Logger.log("&cOutdated language config! Creating a new!");
            if(version != null && version.equalsIgnoreCase("2.5")) {
                Converter.convertLanguage(customConfig.getLang());
            } else {
                File langRu = new File(getDataFolder(), "lang/ru_RU.yml");
                langRu.renameTo(new File(getDataFolder(), "lang/ru_RU.yml.old"));
                saveResource("lang/ru_RU.yml", false);
                File langEn = new File(getDataFolder(), "lang/en_US.yml");
                langEn.renameTo(new File(getDataFolder(), "lang/en_US.yml.old"));
                saveResource("lang/en_US.yml", false);
                File langUa = new File(getDataFolder(), "lang/ua_UA.yml");
                langUa.renameTo(new File(getDataFolder(), "lang/ua_UA.yml.old"));
                saveResource("lang/ua_UA.yml", false);
            }
        }
    }
    private void registerDefaultCommand() {
        PluginCommand command = getCommand("donatecase");
        if(command != null) {
            command.setExecutor(new GlobalCommand());
            command.setTabCompleter(new GlobalCommand());
        }
    }

    private void registerDefaultSubCommands() {
        api.getSubCommandManager().registerSubCommand("reload", new ReloadCommand());
        api.getSubCommandManager().registerSubCommand("givekey", new GiveKeyCommand());
        api.getSubCommandManager().registerSubCommand("delkey", new DelKeyCommand());
        api.getSubCommandManager().registerSubCommand("setkey", new SetKeyCommand());
        api.getSubCommandManager().registerSubCommand("keys", new KeysCommand());
        api.getSubCommandManager().registerSubCommand("cases", new CasesCommand());
        api.getSubCommandManager().registerSubCommand("opencase", new OpenCaseCommand());
        api.getSubCommandManager().registerSubCommand("help", new HelpCommand());
        api.getSubCommandManager().registerSubCommand("create", new CreateCommand());
        api.getSubCommandManager().registerSubCommand("delete", new DeleteCommand());
        api.getSubCommandManager().registerSubCommand("addons", new AddonsCommand());
        api.getSubCommandManager().registerSubCommand("addon", new AddonCommand());
        api.getSubCommandManager().registerSubCommand("animations", new AnimationsCommand());
    }

    private void registerDefaultAnimations() {
        api.getAnimationManager().registerAnimation("SHAPE", new ShapeAnimation());
        api.getAnimationManager().registerAnimation("WHEEL", new WheelAnimation());
        api.getAnimationManager().registerAnimation("RAINLY", new RainlyAnimation());
        api.getAnimationManager().registerAnimation("FIREWORK", new FireworkAnimation());
        api.getAnimationManager().registerAnimation("FULLWHEEL", new FullWheelAnimation());
//        api.getAnimationManager().registerAnimation("TEST_WHEEL", new TestWheelAnimation());
        Logger.log("&aRegistered &cdefault &aanimations");
    }

    private void loadPermissionDriver() {
        setupLuckPerms();
        setupVault();
        PermissionDriver temp = PermissionDriver.getDriver(customConfig.getConfig().getString("DonatCase.PermissionDriver", "vault"));
        if (temp == PermissionDriver.vault && permission != null) {
            permissionDriver = temp;
        }
        if (temp == PermissionDriver.luckperms && luckPerms != null) {
            permissionDriver = temp;
        }
    }

    private void setupVault() {
        if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Permission> permissionProvider = this.getServer().getServicesManager().getRegistration(Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
            }
        }
    }

    private void setupLuckPerms() {
        if(Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                luckPerms = provider.getProvider();
            }
        }
    }

    private void loadHologramManager() {
        String driverName = customConfig.getConfig().getString("DonatCase.HologramDriver", "decentholograms");
        hologramDriver = HologramDriver.getDriver(driverName);

        if (tryInitializeHologramManager(hologramDriver)) {
            return;
        }

        for (HologramDriver fallbackDriver : HologramDriver.values()) {
            if (tryInitializeHologramManager(fallbackDriver)) {
                return;
            }
        }
    }

    private boolean tryInitializeHologramManager(HologramDriver driver) {
        switch (driver) {
            case cmi:
                if (Bukkit.getPluginManager().isPluginEnabled("CMI") && CMIModule.holograms.isEnabled()) {
                    hologramManager = new CMIHologramsSupport();
                    return true;
                }
                break;
            case decentholograms:
                if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
                    hologramManager = new DecentHologramsSupport();
                    return true;
                }
                break;
            case holographicdisplays:
                if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
                    hologramManager = new HolographicDisplaysSupport();
                    return true;
                }
                break;
        }
        return false;
    }

    public void loadHolograms() {
        ConfigurationSection section = customConfig.getCases().getConfigurationSection("DonatCase.Cases");
        if(section == null || section.getKeys(false).isEmpty()) return;
        for (String caseName : section.getKeys(false)) {
            String caseType = Case.getCaseTypeByCustomName(caseName);
            CaseData caseData = Case.getCase(caseType);
            Location location = Case.getCaseLocationByCustomName(caseName);
            if (caseData != null && caseData.getHologram().isEnabled() && location != null && location.getWorld() != null && hologramManager != null && !Case.activeCasesByLocation.containsKey(location)) {
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
        libraryManager = new BukkitLibraryManager(this);
        libraryManager.addMavenCentral();
        libraryManager.loadLibrary(lib);
    }

    private void loadMetrics() {
        Metrics metrics = new Metrics(this, 18709);
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> customConfig.getConfig().getString("DonatCase.Languages")));
    }

    public void saveResource(@NotNull String resourcePath, boolean replace) {
        if (resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = Files.newOutputStream(outFile.toPath());
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                getLogger().log(java.util.logging.Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            getLogger().log(java.util.logging.Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    @Nullable
    public InputStream getResource(@NotNull String filename) {

        try {
            URL url = getClassLoader().getResource("resources/" +  filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
}
