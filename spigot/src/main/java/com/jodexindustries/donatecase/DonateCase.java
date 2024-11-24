package com.jodexindustries.donatecase;

import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.jodexindustries.donatecase.animations.*;
import com.jodexindustries.donatecase.api.*;
import com.jodexindustries.donatecase.api.addon.PowerReason;
import com.jodexindustries.donatecase.api.addon.internal.InternalAddonClassLoader;
import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.HologramDriver;
import com.jodexindustries.donatecase.api.data.PermissionDriver;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.events.DonateCaseDisableEvent;
import com.jodexindustries.donatecase.api.events.DonateCaseEnableEvent;
import com.jodexindustries.donatecase.api.events.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import com.jodexindustries.donatecase.api.holograms.types.CMIHologramsSupport;
import com.jodexindustries.donatecase.api.holograms.types.DecentHologramsSupport;
import com.jodexindustries.donatecase.api.holograms.types.FancyHologramsSupport;
import com.jodexindustries.donatecase.api.holograms.types.HolographicDisplaysSupport;
import com.jodexindustries.donatecase.api.manager.*;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.command.impl.*;
import com.jodexindustries.donatecase.config.CaseLoader;
import com.jodexindustries.donatecase.config.ConfigImpl;
import com.jodexindustries.donatecase.database.CaseDatabaseImpl;
import com.jodexindustries.donatecase.gui.items.HISTORYItemHandlerImpl;
import com.jodexindustries.donatecase.gui.items.OPENItemClickHandlerImpl;
import com.jodexindustries.donatecase.impl.DCAPIBukkitImpl;
import com.jodexindustries.donatecase.impl.actions.*;
import com.jodexindustries.donatecase.impl.materials.*;
import com.jodexindustries.donatecase.listener.EventsListener;
import com.jodexindustries.donatecase.tools.*;
import com.jodexindustries.donatecase.tools.support.*;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Main DonateCase class for loading
 */
public class DonateCase extends JavaPlugin {
    public static DonateCase instance;

    public CaseDatabaseImpl<CaseDataBukkit, CaseDataMaterialBukkit, ItemStack> database;
    public HologramManager hologramManager = null;
    public PAPISupport papi = null;
    public LuckPerms luckPerms = null;
    public Permission permission = null;
    public PermissionDriver permissionDriver = null;
    public HologramDriver hologramDriver = null;
    public CaseLoader loader;
    public ItemsAdderSupport itemsAdderSupport = null;
    public OraxenSupport oraxenSupport = null;
    public HeadDatabaseSupport headDatabaseSupport = null;
    public CustomHeadsSupport customHeadsSupport = null;
    public PacketEventsSupport packetEventsSupport;

    public final DCAPIBukkit api = new DCAPIBukkitImpl(this);

    public ConfigImpl config;

    public boolean usePackets = false;

    private boolean spawnProtectionDisabled = false;

    static {
        DCAPIBukkit.register(DCAPIBukkitImpl.class);
    }

    @Override
    public void onLoad() {
        instance = this;

        api.getAddonManager().loadAddons();
    }

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();

        loadConfig();
        database = new CaseDatabaseImpl<>(api.getCaseManager(), getLogger());

        loadDatabase();

        loader = new CaseLoader(this);

        registerDefaultCommand();
        registerDefaultSubCommands();
        registerDefaultAnimations();
        registerDefaultActions();
        registerDefaultMaterials();
        registerDefaultGUITypedItems();

        loadPermissionDriver();
        loadHologramManager();

        loadPlaceholderAPI();

        loadPacketEventsAPI();

        loadItemsAdderAPI();
        loadOraxenAPI();
        loadHeadDatabaseAPI();
        loadCustomHeadsAPI();

        loadCases();
        loadHolograms();

        loadUpdater();
        loadMetrics();

        api.getAddonManager().enableAddons(PowerReason.DONATE_CASE);

        DonateCaseEnableEvent donateCaseEnableEvent = new DonateCaseEnableEvent(this);
        getServer().getPluginManager().callEvent(donateCaseEnableEvent);
        getServer().getPluginManager().registerEvents(new EventsListener(), this);

        Logger.log(ChatColor.GREEN + "Enabled in " + (System.currentTimeMillis() - time) + "ms");
    }


    @Override
    public void onDisable() {
        DonateCaseDisableEvent donateCaseDisableEvent = new DonateCaseDisableEvent(this);
        getServer().getPluginManager().callEvent(donateCaseDisableEvent);

        api.getAddonManager().unloadAddons(PowerReason.DONATE_CASE);
        api.getAnimationManager().unregisterAnimations();
        api.getSubCommandManager().unregisterSubCommands();
        api.getActionManager().unregisterActions();
        api.getMaterialManager().unregisterMaterials();
        api.getGuiTypedItemManager().unregisterItems();

        if (papi != null) papi.unregister();
        if (database != null) database.close();
        if (hologramManager != null) hologramManager.removeAllHolograms();
        if (packetEventsSupport != null) packetEventsSupport.unload();

        Case.cleanCache();
        DCAPIBukkit.unregister();
    }

    private void loadPlaceholderAPI() {
        try {
            papi = new PAPISupport();
            papi.register();
        } catch (Throwable e) {
            Logger.log("&cError hooking to &bPlaceholderAPI&c: " + e.getMessage());
        }
    }

    private void loadPacketEventsAPI() {
        if (getServer().getPluginManager().isPluginEnabled("packetevents")) {
            try {
                packetEventsSupport = new PacketEventsSupport(this);
            } catch (Throwable e) {
                Logger.log("&cError hooking to &bpacketevents&c: " + e.getMessage());
            }
        }
    }

    private void loadItemsAdderAPI() {
        if (getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
            try {
                itemsAdderSupport = new ItemsAdderSupport(this);
            } catch (Throwable e) {
                Logger.log("&cError hooking to &bItemsAdder&c: " + e.getMessage());
            }
        }
    }

    private void loadOraxenAPI() {
        if (getServer().getPluginManager().isPluginEnabled("Oraxen")) {
            try {
                oraxenSupport = new OraxenSupport(this);
            } catch (Throwable e) {
                Logger.log("&cError hooking to &bOraxen&c: " + e.getMessage());
            }
        }
    }

    private void loadHeadDatabaseAPI() {
        if (getServer().getPluginManager().isPluginEnabled("HeadDatabase")) {
            try {
                headDatabaseSupport = new HeadDatabaseSupport(this);
            } catch (Throwable e) {
                Logger.log("&cError hooking to &bHeadDatabase&c: " + e.getMessage());
            }
        }
    }

    private void loadCustomHeadsAPI() {
        if (getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
            try {
                customHeadsSupport = new CustomHeadsSupport();
            } catch (Throwable e) {
                Logger.log("&cError hooking to &bCustomHeads&c: " + e.getMessage());
            }
        }
    }

    private void loadUpdater() {
        if (config.getConfig().getBoolean("DonateCase.UpdateChecker")) {
            new UpdateChecker(this, 106701).getVersion((version) -> {
                if (DCTools.getPluginVersion(getDescription().getVersion()) < DCTools.getPluginVersion(version)) {
                    Logger.log(ChatColor.GREEN + "There is a new update " + version + " available.");
                    Logger.log(ChatColor.GREEN + "Download - https://www.spigotmc.org/resources/donatecase.106701/");
                }
            });
        }
    }

    public void loadConfig() {
        config = new ConfigImpl(this);

        config.getConverter().convertData();

        disableSpawnProtection();

        DonateCaseReloadEvent reloadEvent = new DonateCaseReloadEvent(this, DonateCaseReloadEvent.Type.CONFIG);
        getServer().getPluginManager().callEvent(reloadEvent);
    }

    public void loadDatabase() {
        ConfigurationSection section = config.getConfig().getConfigurationSection("DonateCase.MySql");
        if(section == null || !section.getBoolean("Enabled")) {
            database.connect(getDataFolder().getAbsolutePath());
            return;
        }

        String databaseName = section.getString("DataBase");
        String port = section.getString("Port");
        String host = section.getString("Host");
        String user = section.getString("User");
        String password = section.getString("Password");

        database.connect(
                databaseName,
                port,
                host,
                user,
                password
        );
    }

    public void loadCases() {
        loader.load();
    }

    private void registerDefaultCommand() {
        PluginCommand command = getCommand("donatecase");
        if (command != null) {
            command.setExecutor(new GlobalCommand());
            command.setTabCompleter(new GlobalCommand());
        }
    }

    private void registerDefaultSubCommands() {
        SubCommandManager<CommandSender> manager = api.getSubCommandManager();

        ReloadCommand.register(manager);
        GiveKeyCommand.register(manager);
        DelKeyCommand.register(manager);
        SetKeyCommand.register(manager);
        KeysCommand.register(manager);
        CasesCommand.register(manager);
        OpenCaseCommand.register(manager);
        HelpCommand.register(manager);
        CreateCommand.register(manager);
        DeleteCommand.register(manager);
        AddonsCommand.register(manager);
        AddonCommand.register(manager);
        RegistryCommand.register(manager);

        Logger.log("&aRegistered &c" + manager.getRegisteredSubCommands().size() + " &acommands");
    }

    private void registerDefaultAnimations() {
        AnimationManager<JavaAnimationBukkit, CaseDataMaterialBukkit, ItemStack, Player, Location, Block, CaseDataBukkit> manager = api.getAnimationManager();
        ShapeAnimation.register(manager);
        RainlyAnimation.register(manager);
        FireworkAnimation.register(manager);
        WheelAnimation.register(manager);
        Logger.log("&aRegistered &c" + manager.getRegisteredAnimations().size() + " &aanimations");
    }

    private void registerDefaultActions() {
        ActionManager<Player> manager = api.getActionManager();
        manager.registerAction("[command]", new CommandActionExecutorImpl(),
                "Sends a command to the console");
        manager.registerAction("[message]", new MessageActionExecutorImpl(),
                "Sends a message in the player's chat");
        manager.registerAction("[title]", new TitleActionExecutorImpl(),
                "Sends a title to the player");
        manager.registerAction("[broadcast]", new BroadcastActionExecutorImpl(),
                "Sends a broadcast to the players");
        manager.registerAction("[sound]", new SoundActionExecutorImpl(),
                "Sends a sound to the player");
        Logger.log("&aRegistered &c" + manager.getRegisteredActions().size() + " &aactions");
    }

    private void registerDefaultMaterials() {
        MaterialManager<ItemStack> manager = api.getMaterialManager();
        manager.registerMaterial("BASE64", new BASE64MaterialHandlerImpl(),
                "Heads from Minecraft-heads by BASE64 value");
        manager.registerMaterial("MCURL", new MCURLMaterialHandlerImpl(),
                "Heads from Minecraft-heads by Minecrat-URL");
        manager.registerMaterial("HEAD", new HEADMaterialHandlerImpl(),
                "Default Minecraft heads by nickname");
        manager.registerMaterial("IA", new IAMaterialHandlerImpl(),
                "Items from ItemsAdder plugin");
        manager.registerMaterial("ORAXEN", new OraxenMaterialHandlerImpl(),
                "Items from Oraxen plugin");
        manager.registerMaterial("CH", new CHMaterialHandlerImpl(),
                "Heads from CustomHeads plugin");
        manager.registerMaterial("HDB", new HDBMaterialHandlerImpl(),
                "Heads from HeadDatabase plugin");
        Logger.log("&aRegistered &c" + manager.getRegisteredMaterials().size() + " &amaterials");
    }

    private void registerDefaultGUITypedItems() {
        GUITypedItemManager<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent> manager = api.getGuiTypedItemManager();

        OPENItemClickHandlerImpl.register(manager);
        HISTORYItemHandlerImpl.register(manager);

        Logger.log("&aRegistered &c" + manager.getRegisteredItems().size() + " &agui typed items");
    }

    private void loadPermissionDriver() {
        loadLuckPerms();
        loadVault();
        PermissionDriver temp = PermissionDriver.getDriver(config.getConfig().getString("DonateCase.PermissionDriver", "vault"));
        if ((temp == PermissionDriver.vault && permission != null) ||
                (temp == PermissionDriver.luckperms && luckPerms != null)) {
            permissionDriver = temp;
        }
        if (permissionDriver != null) Logger.log("&aUsing &b" + permissionDriver + " &aas permission driver");
    }

    private void loadVault() {
        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
            }
        }
    }

    private void loadLuckPerms() {
        if (getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            RegisteredServiceProvider<LuckPerms> provider = getServer().getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                luckPerms = provider.getProvider();
            }
        }
    }

    private void loadHologramManager() {
        String driverName = config.getConfig().getString("DonateCase.HologramDriver", "decentholograms");
        hologramDriver = HologramDriver.getDriver(driverName);

        tryInitializeHologramManager(hologramDriver);

        if (hologramManager == null) {
            for (HologramDriver fallbackDriver : HologramDriver.values()) {
                if (tryInitializeHologramManager(fallbackDriver)) {
                    break;
                }
            }
        }

        if (hologramManager != null) Logger.log("&aUsing &b" + hologramDriver + " &aas hologram driver");
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
            case fancyholograms:
                if(getServer().getPluginManager().isPluginEnabled("FancyHolograms")) {
                    hologramManager = new FancyHologramsSupport();
                    return true;
                }
        }
        return false;
    }

    public void loadHolograms() {
        ConfigurationSection section = config.getCases().getConfigurationSection("DonateCase.Cases");
        if (section == null || section.getKeys(false).isEmpty()) return;
        for (String caseName : section.getKeys(false)) {
            ConfigurationSection caseSection = section.getConfigurationSection(caseName);
            if(caseSection == null) continue;

            String caseType = caseSection.getString("type");
            if(caseType == null) continue;

            CaseDataBukkit caseData = api.getCaseManager().getCase(caseType);
            Location location = Case.getCaseLocationByCustomName(caseName);
            if (caseData != null && caseData.getHologram().isEnabled() && location != null
                    && location.getWorld() != null && hologramManager != null
                    && !api.getAnimationManager().getActiveCasesByBlock().containsKey(location.getBlock())) {
                hologramManager.createHologram(location.getBlock(), caseData);
            }
        }
    }

    private void loadMetrics() {
        Metrics metrics = new Metrics(this, 18709);
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> config.getConfig().getString("DonateCase.Languages")));
    }

    private void disableSpawnProtection() {
        if (config.getConfig().getBoolean("DonateCase.DisableSpawnProtection", false)) {
            if (getServer().getSpawnRadius() > 0 && !spawnProtectionDisabled) {
                getServer().setSpawnRadius(0);
                spawnProtectionDisabled = true;
                Logger.log("&aSpawn protection disabled!");
            }
        }
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
            if (replace) {
                InternalAddonClassLoader.saveFromInputStream(in, outFile);
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
            URL url = getClassLoader().getResource("resources/" + filename);

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
