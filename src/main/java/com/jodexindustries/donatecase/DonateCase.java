package com.jodexindustries.donatecase;

import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.alessiodp.libby.logging.LogLevel;
import com.jodexindustries.donatecase.animations.*;
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
import com.jodexindustries.donatecase.tools.support.ItemsAdderSupport;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import com.jodexindustries.donatecase.tools.support.PacketEventsSupport;
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
    public ItemsAdderSupport itemsAdderSupport;
    public PacketEventsSupport packetEventsSupport;

    public Config config;

    public boolean usePackets = false;
    public boolean sql = false;

    @Override
    public void onLoad() {
        loadLibraries();
    }

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        instance = this;
        api = new CaseManager(this);
        loader = new CaseLoader(this);

        loadConfig();

        loadUpdater();

        loadPermissionDriver();
        loadHologramManager();

        loadMetrics();

        registerDefaultCommand();
        registerDefaultSubCommands();
        registerDefaultAnimations();
        registerDefaultActions();

        loadHolograms();

        loadPlaceholderAPI();

        loadPacketEventsAPI();

        loadItemsAdderAPI();

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

        api.getAddonManager().unloadAddons();
        api.getAnimationManager().unregisterAnimations();
        api.getSubCommandManager().unregisterSubCommands();
        api.getActionManager().unregisterActions();

        if(papi != null) papi.unregister();
        if(mysql != null) mysql.close();
        if(hologramManager != null) hologramManager.removeAllHolograms();
        if(packetEventsSupport != null) packetEventsSupport.unload();

        Case.cleanCache();
    }

    private void loadPlaceholderAPI() {
        papi = new PAPISupport();
        papi.register();
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

        loader.load();

        DonateCaseReloadEvent reloadEvent = new DonateCaseReloadEvent(this);
        Bukkit.getPluginManager().callEvent(reloadEvent);
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
        Logger.log("&aRegistered &cdefault &acommands");
    }

    private void registerDefaultAnimations() {
        api.getAnimationManager().registerAnimation("SHAPE", ShapeAnimation.class);
        api.getAnimationManager().registerAnimation("WHEEL", WheelAnimation.class);
        api.getAnimationManager().registerAnimation("RAINLY", RainlyAnimation.class);
        api.getAnimationManager().registerAnimation("FIREWORK", FireworkAnimation.class);
        api.getAnimationManager().registerAnimation("FULLWHEEL", FullWheelAnimation.class);
        Logger.log("&aRegistered &cdefault &aanimations");
    }

    private void registerDefaultActions() {
        api.getActionManager().registerAction("[command]", new CommandActionImpl());
        api.getActionManager().registerAction("[message]", new MessageActionImpl());
        api.getActionManager().registerAction("[title]", new TitleActionImpl());
        api.getActionManager().registerAction("[broadcast]", new BroadcastActionImpl());
        api.getActionManager().registerAction("[sound]", new SoundActionImpl());
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
        if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Permission> permissionProvider = this.getServer().getServicesManager().getRegistration(Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
            }
        }
    }

    private void loadLuckPerms() {
        if(Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                luckPerms = provider.getProvider();
            }
        }
    }

    private void loadHologramManager() {
        String driverName = config.getConfig().getString("DonatCase.HologramDriver", "decentholograms");
        hologramDriver = HologramDriver.getDriver(driverName);

        if (tryInitializeHologramManager(hologramDriver)) {
            return;
        }

        for (HologramDriver fallbackDriver : HologramDriver.values()) {
            if (tryInitializeHologramManager(fallbackDriver)) {
                return;
            }
        }
        if(hologramManager != null) Logger.log("&aUsing &b" + hologramDriver + " &aas hologram driver");
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
        ConfigurationSection section = config.getCases().getConfigurationSection("DonatCase.Cases");
        if (section == null || section.getKeys(false).isEmpty()) return;
        for (String caseName : section.getKeys(false)) {
            String caseType = Case.getCaseTypeByCustomName(caseName);
            CaseData caseData = Case.getCase(caseType);
            Location location = Case.getCaseLocationByCustomName(caseName);
            if (caseData != null && caseData.getHologram().isEnabled() && location != null && location.getWorld() != null && hologramManager != null && !Case.activeCasesByLocation.containsKey(location)) {
                hologramManager.createHologram(location.getBlock(), caseData);
            }
        }
    }

    private void loadLibraries() {
        Library orm = Library.builder()
                .groupId("com{}j256{}ormlite")
                .artifactId("ormlite-jdbc")
                .version("6.1")
                .build();
        Library entityLibSpigot = Library.builder()
                .groupId("com{}github{}tofaa2{}entitylib")
                .artifactId("spigot")
                .version("2.4.7-SNAPSHOT")
                .url("https://jitpack.io/com/github/Tofaa2/EntityLib/spigot/2.4.7-SNAPSHOT/spigot-2.4.7-SNAPSHOT.jar")
                .build();
        Library entityLibAPI = Library.builder()
                .groupId("com{}github{}tofaa2{}entitylib")
                .artifactId("api")
                .version("2.4.7-SNAPSHOT")
                .url("https://jitpack.io/com/github/Tofaa2/EntityLib/api/2.4.7-SNAPSHOT/api-2.4.7-SNAPSHOT.jar")
                .build();
        Library entityLibCommon = Library.builder()
                .groupId("com{}github{}tofaa2{}entitylib")
                .artifactId("common")
                .version("2.4.7-SNAPSHOT")
                .url("https://jitpack.io/com/github/Tofaa2/EntityLib/common/2.4.7-SNAPSHOT/common-2.4.7-SNAPSHOT.jar")
                .build();
        libraryManager = new BukkitLibraryManager(this);
        libraryManager.setLogLevel(LogLevel.WARN);
        libraryManager.addJitPack();
        libraryManager.addMavenCentral();
        loadLibrary(orm, entityLibCommon, entityLibSpigot, entityLibAPI);
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
