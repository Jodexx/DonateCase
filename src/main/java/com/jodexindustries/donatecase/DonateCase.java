package com.jodexindustries.donatecase;

import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.alessiodp.libby.logging.LogLevel;
import com.jodexindustries.donatecase.animations.*;
import com.jodexindustries.donatecase.api.AddonManager;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.CaseManager;
import com.jodexindustries.donatecase.api.actions.*;
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
import com.jodexindustries.donatecase.config.Config;
import com.jodexindustries.donatecase.database.sql.MySQLDataBase;
import com.jodexindustries.donatecase.listener.EventsListener;
import com.jodexindustries.donatecase.tools.*;
import com.jodexindustries.donatecase.tools.support.*;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.logging.Level;

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
    public ItemsAdderSupport itemsAdderSupport = null;
    public OraxenSupport oraxenSupport = null;
    public HeadDatabaseSupport headDatabaseSupport = null;
    public PacketEventsSupport packetEventsSupport;

    public Config config;

    public boolean usePackets = false;
    public boolean sql = false;

    @Override
    public void onLoad() {
        instance = this;

        loadLibraries();

        api = new CaseManager(this);
        api.getAddonManager().loadAddons();
    }

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();

        loadConfig();

        loader = new CaseLoader(this);

        registerDefaultCommand();
        registerDefaultSubCommands();
        registerDefaultAnimations();
        registerDefaultActions();

        loadPermissionDriver();
        loadHologramManager();

        loadPlaceholderAPI();

        loadPacketEventsAPI();

        loadItemsAdderAPI();
        loadOraxenAPI();
        loadHeadDatabaseAPI();

        loadCases();
        loadHolograms();

        loadUpdater();
        loadMetrics();

        api.getAddonManager().enableAddons(AddonManager.PowerReason.DONATE_CASE);

        DonateCaseEnableEvent donateCaseEnableEvent = new DonateCaseEnableEvent(this);
        getServer().getPluginManager().callEvent(donateCaseEnableEvent);
        getServer().getPluginManager().registerEvents(new EventsListener(), this);

        Logger.log(ChatColor.GREEN + "Enabled in " + (System.currentTimeMillis() - time) + "ms");
    }


    @Override
    public void onDisable() {
        DonateCaseDisableEvent donateCaseDisableEvent = new DonateCaseDisableEvent(this);
        getServer().getPluginManager().callEvent(donateCaseDisableEvent);

        api.getAddonManager().unloadAddons(AddonManager.PowerReason.DONATE_CASE);
        api.getAnimationManager().unregisterAnimations();
        api.getSubCommandManager().unregisterSubCommands();
        api.getActionManager().unregisterAction();

        if(papi != null) papi.unregister();
        if(mysql != null) mysql.close();
        if(hologramManager != null) hologramManager.removeAllHolograms();
        if(packetEventsSupport != null) packetEventsSupport.unload();

        Case.cleanCache();
    }

    private void loadPlaceholderAPI() {
        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            papi = new PAPISupport();
            papi.register();
        }
    }

    private void loadPacketEventsAPI() {
        if(getServer().getPluginManager().isPluginEnabled("packetevents")) {
            try {
                packetEventsSupport = new PacketEventsSupport(this);
                packetEventsSupport.load();
            } catch (NoClassDefFoundError e) {
                getLogger().log(Level.WARNING, "packetevents hooking canceled!", e);
            }
        }
    }

    private void loadItemsAdderAPI() {
        if (getServer().getPluginManager().isPluginEnabled("ItemsAdder")) itemsAdderSupport = new ItemsAdderSupport(this);
    }

    private void loadOraxenAPI() {
        if(getServer().getPluginManager().isPluginEnabled("Oraxen")) oraxenSupport = new OraxenSupport(this);
    }

    private void loadHeadDatabaseAPI() {
        if (getServer().getPluginManager().isPluginEnabled("HeadDatabase")) headDatabaseSupport = new HeadDatabaseSupport(this);
    }

    private void loadUpdater() {
        if (config.getConfig().getBoolean("DonatCase.UpdateChecker")) {
            new UpdateChecker(this, 106701).getVersion((version) -> {
                if (Tools.getPluginVersion(getDescription().getVersion()) < Tools.getPluginVersion(version)) {
                    Logger.log(ChatColor.GREEN + "There is a new update " + version +  " available.");
                    Logger.log(ChatColor.GREEN + "Download - https://www.spigotmc.org/resources/donatecase.106701/");
                }
            });
        }
    }

    public void loadConfig() {
        config = new Config(this);

        mysql = new MySQLDataBase(this);
        mysql.connect();

        DonateCaseReloadEvent reloadEvent = new DonateCaseReloadEvent(this, DonateCaseReloadEvent.Type.CONFIG);
        getServer().getPluginManager().callEvent(reloadEvent);
    }

    public void loadCases() {
        loader.load();
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
        api.getSubCommandManager().registerSubCommand("actions", new ActionsCommand());
        Logger.log("&aRegistered &cdefault &acommands");
    }

    private void registerDefaultAnimations() {
        api.getAnimationManager().registerAnimation("SHAPE", ShapeAnimation.class,
                "Items flip through and a shape appears");
        api.getAnimationManager().registerAnimation("WHEEL", WheelAnimation.class,
                "Random items revolve around the case");
        api.getAnimationManager().registerAnimation("RAINLY", RainlyAnimation.class,
                "Rain drips from the clouds");
        api.getAnimationManager().registerAnimation("FIREWORK", FireworkAnimation.class,
                "Fireworks fly to the skies and a prize appears");
        api.getAnimationManager().registerAnimation("FULLWHEEL", FullWheelAnimation.class,
                "All items from the case revolve around it");
        Logger.log("&aRegistered &cdefault &aanimations");
    }

    private void registerDefaultActions() {
        api.getActionManager().registerAction("[command]", new CommandActionExecutorImpl(),
                "Sends a command to the console");
        api.getActionManager().registerAction("[message]", new MessageActionExecutorImpl(),
                "Sends a message in the player's chat");
        api.getActionManager().registerAction("[title]", new TitleActionExecutorImpl(),
                "Sends a title to the player");
        api.getActionManager().registerAction("[broadcast]", new BroadcastActionExecutorImpl(),
                "Sends a broadcast to the players");
        api.getActionManager().registerAction("[sound]", new SoundActionExecutorImpl(),
                "Sends a sound to the player");
        Logger.log("&aRegistered &cdefault &aactions");
    }

    private void loadPermissionDriver() {
        loadLuckPerms();
        loadVault();
        PermissionDriver temp = PermissionDriver.getDriver(config.getConfig().getString("DonatCase.PermissionDriver", "vault"));
        if (temp == PermissionDriver.vault && permission != null) {
            permissionDriver = temp;
        }
        if (temp == PermissionDriver.luckperms && luckPerms != null) {
            permissionDriver = temp;
        }
        if(permissionDriver != null) Logger.log("&aUsing &b" + permissionDriver + " &aas permission driver");
    }

    private void loadVault() {
        if(getServer().getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
            }
        }
    }

    private void loadLuckPerms() {
        if(getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            RegisteredServiceProvider<LuckPerms> provider = getServer().getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                luckPerms = provider.getProvider();
            }
        }
    }

    private void loadHologramManager() {
        String driverName = config.getConfig().getString("DonatCase.HologramDriver", "decentholograms");
        hologramDriver = HologramDriver.getDriver(driverName);

        tryInitializeHologramManager(hologramDriver);

        if(hologramManager == null) {
            for (HologramDriver fallbackDriver : HologramDriver.values()) {
                if (tryInitializeHologramManager(fallbackDriver)) {
                    break;
                }
            }
        }

        if(hologramManager != null) Logger.log("&aUsing &b" + hologramDriver + " &aas hologram driver");
    }

    private boolean tryInitializeHologramManager(HologramDriver driver) {
        switch (driver) {
            case cmi:
                if (getServer().getPluginManager().isPluginEnabled("CMI") && CMIModule.holograms.isEnabled()) {
                    hologramManager = new CMIHologramsSupport();
                    return true;
                }
                break;
            case decentholograms:
                if (getServer().getPluginManager().isPluginEnabled("DecentHolograms")) {
                    hologramManager = new DecentHologramsSupport();
                    return true;
                }
                break;
            case holographicdisplays:
                if (getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
                    hologramManager = new HolographicDisplaysSupport();
                    return true;
                }
                break;
        }
        return false;
    }

    public void loadHolograms() {
        ConfigurationSection section = config.getCases().getConfigurationSection("DonatCase.Cases");
        if (section == null || section.getKeys(false).isEmpty()) return;
        for (String caseName : section.getKeys(false)) {
            String caseType = Case.getCaseTypeByCustomName(caseName);
            CaseData caseData = Case.getCase(caseType);
            Location location = Case.getCaseLocationByCustomName(caseName);
            if (caseData != null && caseData.getHologram().isEnabled() && location != null
                    && location.getWorld() != null && hologramManager != null
                    && !Case.activeCasesByLocation.containsKey(location)) {
                hologramManager.createHologram(location.getBlock(), caseData);
            }
        }
    }

    private void loadLibraries() {
        getLogger().info("Loading libraries...");
        Library orm = Library.builder()
                .groupId("com{}j256{}ormlite")
                .artifactId("ormlite-jdbc")
                .version("6.1")
                .build();
        Library entityLibSpigot = Library.builder()
                .groupId("me{}tofaa{}entitylib")
                .artifactId("spigot")
                .version("2.4.7-SNAPSHOT")
                .resolveTransitiveDependencies(true)
                .build();
        libraryManager = new BukkitLibraryManager(this);
        libraryManager.setLogLevel(LogLevel.WARN);
        libraryManager.addRepository("https://maven.evokegames.gg/snapshots");
        libraryManager.addMavenCentral();
        loadLibrary(orm, entityLibSpigot);
        getLogger().info("Libraries loaded!");
    }

    private void loadLibrary(Library... libraries) {
        try {
            libraryManager.loadLibraries(libraries);
        } catch (RuntimeException e) {
            getLogger().log(Level.WARNING, e.getMessage());
        }
    }

    private void loadMetrics() {
        Metrics metrics = new Metrics(this, 18709);
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> config.getConfig().getString("DonatCase.Languages")));
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
                saveFromInputStream(in, outFile);
            } else {
                getLogger().log(java.util.logging.Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            getLogger().log(java.util.logging.Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    public static void saveFromInputStream(InputStream in, File outFile) throws IOException {
        OutputStream out = Files.newOutputStream(outFile.toPath());
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
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
