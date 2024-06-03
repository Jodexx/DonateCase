package com.jodexindustries.donatecase;

import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.j256.ormlite.logger.Level;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.CaseManager;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.PermissionDriver;
import com.jodexindustries.donatecase.api.events.DonateCaseDisableEvent;
import com.jodexindustries.donatecase.api.events.DonateCaseEnableEvent;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import com.jodexindustries.donatecase.api.holograms.types.CMIHologramsSupport;
import com.jodexindustries.donatecase.api.holograms.types.DecentHologramsSupport;
import com.jodexindustries.donatecase.api.holograms.types.HolographicDisplaysSupport;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.command.subcommands.ReloadCommand;
import com.jodexindustries.donatecase.database.CaseDataBase;
import com.jodexindustries.donatecase.listener.EventsListener;
import com.jodexindustries.donatecase.tools.*;
import com.jodexindustries.donatecase.tools.animations.*;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonateCase extends JavaPlugin {
    public static DonateCase instance;
    public static Permission permission = null;
    public static boolean sql = false;

    public static CaseDataBase mysql;
    public static HologramManager hologramManager = null;
    public static LuckPerms luckPerms = null;
    public static PermissionDriver permissionDriver = null;
    public static CaseManager api;

    public static CustomConfig customConfig;
    public static CasesConfig casesConfig;
    private boolean usePackets = true;

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        instance = this;
        api = new CaseManager(this);
        loadPlaceholderAPI();

        loadLibraries();

        setupConfigs();

        Bukkit.getPluginManager().registerEvents(new EventsListener(), this);

        loadUpdater();

        setupLangs();

        loadPermissionDriver();


        Metrics metrics = new Metrics(this, 18709);
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> customConfig.getConfig().getString("DonatCase.Languages")));

        PluginCommand command = getCommand("donatecase");
        if(command != null) {
            command.setExecutor(new GlobalCommand());
            command.setTabCompleter(new GlobalCommand());
        }

        registerDefaultSubCommands();
        registerDefaultAnimations();

        api.getAddonManager().loadAddons();

        loadHologramManager();

        loadHolograms();

        DonateCaseEnableEvent donateCaseEnableEvent = new DonateCaseEnableEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(donateCaseEnableEvent);

        Logger.log(ChatColor.GREEN + "Enabled in " + (System.currentTimeMillis() - time) + "ms");
    }


    @Override
    public void onDisable() {
        DonateCaseDisableEvent donateCaseDisableEvent = new DonateCaseDisableEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(donateCaseDisableEvent);
        api.getAnimationManager().unregisterAnimations();
        if(api.getAddonManager() != null) api.getAddonManager().unloadAddons();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholder().unregister();
        }

        if (mysql != null) {
            mysql.close();
        }
        if(hologramManager != null) hologramManager.removeAllHolograms();
        cleanCache();
    }
    private void loadPermissionDriver() {
        setupLuckPerms();
        setupVault();
        PermissionDriver temp;
        try {
            temp = PermissionDriver.valueOf(customConfig.getConfig().getString("DonatCase.PermissionDriver", "vault").toLowerCase());
        }catch (IllegalArgumentException ignored) {
            temp = PermissionDriver.luckperms;
        }
        if (temp == PermissionDriver.vault && permission != null) {
            permissionDriver = temp;
        }
        if (temp == PermissionDriver.luckperms && luckPerms != null) {
            permissionDriver = temp;
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
                    DonateCase.mysql = new CaseDataBase(instance, base, port, host, user, password);
                }
            }).runTaskTimerAsynchronously(instance, 0L, 12000L);
            com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.WARNING);
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
    private void loadPlaceholderAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            (new Placeholder()).register();
            Logger.log("&aPlaceholders registered!");
        }
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
            Tools.convertCases();
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

        setupMySQL();

        loadCases();

        customConfig.getConfig().addDefault("DonatCase.NoKeyWarningSound", "ENTITY_ENDERMAN_TELEPORT");

        if(customConfig.getCases().getString("config") == null || !customConfig.getCases().getString("config", "").equalsIgnoreCase("1.0")) {
            Logger.log("Conversion of case locations to a new method of storage...");
            Tools.convertCasesLocation();
        }
    }

    private void checkAndCreateFile(String fileName) {
        if (!(new File(this.getDataFolder(), fileName)).exists()) {
            this.saveResource(fileName, false);
        }
    }

    private void checkAndUpdateConfig(YamlConfiguration config, String fileName, String expectedValue) {
        if (config.getString("config") == null || !config.getString("config", "").equals(expectedValue)) {
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
            File langRu = new File(this.getDataFolder(), "lang/ru_RU.yml");
            langRu.renameTo(new File(this.getDataFolder(), "lang/ru_RU.yml.old"));
            this.saveResource("lang/ru_RU.yml", false);
            File langEn = new File(this.getDataFolder(), "lang/en_US.yml");
            langEn.renameTo(new File(this.getDataFolder(), "lang/en_US.yml.old"));
            this.saveResource("lang/en_US.yml", false);
            File langUa = new File(this.getDataFolder(), "lang/ua_UA.yml");
            langUa.renameTo(new File(this.getDataFolder(), "lang/ua_UA.yml.old"));
            this.saveResource("lang/ua_UA.yml", false);
            customConfig = new CustomConfig();
        }
    }
    private void registerDefaultSubCommands() {
        api.getSubCommandManager().registerSubCommand("reload", new ReloadCommand());
    }

    private void registerDefaultAnimations() {
        api.getAnimationManager().registerAnimation("SHAPE", new ShapeAnimation());
        api.getAnimationManager().registerAnimation("WHEEL", new WheelAnimation());
        api.getAnimationManager().registerAnimation("RAINLY", new RainlyAnimation());
        api.getAnimationManager().registerAnimation("FIREWORK", new FireworkAnimation());
        api.getAnimationManager().registerAnimation("FULLWHEEL", new FullWheelAnimation());
        Logger.log("&aRegistered &adefault animations");
    }

    private void loadCases() {
        Case.caseData.clear();
        for (String caseName : casesConfig.getCases().keySet()) {
            YamlConfiguration config = casesConfig.getCase(caseName);
            ConfigurationSection caseSection = config.getConfigurationSection("case");
            if(caseSection == null) {
                getLogger().warning("Case " + caseName + " has a broken case section, skipped.");
                continue;
            }
            String caseTitle = Tools.rc(caseSection.getString("Title"));
            String caseDisplayName = caseSection.getString("DisplayName");
            caseDisplayName = caseDisplayName == null ? "" : Tools.rc(caseDisplayName);
            String animationName = caseSection.getString("Animation");

            String animationSound = caseSection.getString("AnimationSound", "NULL").toUpperCase();
            float volume = (float) caseSection.getDouble("Sound.Volume");
            float pitch = (float) caseSection.getDouble("Sound.Pitch");
            Sound bukkitSound = Sound.valueOf(animationSound);
            CaseData.AnimationSound sound = new CaseData.AnimationSound(bukkitSound,volume, pitch);

            boolean hologramEnabled = caseSection.getBoolean("Hologram.Toggle");
            double hologramHeight = caseSection.getDouble("Hologram.Height");
            int range = caseSection.getInt("Hologram.Range");
            List<String> hologramMessage = caseSection.getStringList("Hologram.Message");
            CaseData.Hologram hologram = hologramEnabled ? new CaseData.Hologram(true, hologramHeight, range, hologramMessage) : new CaseData.Hologram();

            Map<String, CaseData.Item> items = new HashMap<>();
            ConfigurationSection itemsSection = caseSection.getConfigurationSection("Items");
            if(itemsSection != null) {
                for (String item : itemsSection.getKeys(false)) {
                    ConfigurationSection itemSection = itemsSection.getConfigurationSection(item);
                    if(itemSection == null) {
                        getLogger().warning("Case " + caseName + " has a broken item " + item + " section, skipped.");
                        continue;
                    }
                    String group = itemSection.getString("Group", "");
                    int chance = itemSection.getInt("Chance");
                    String giveType = itemSection.getString("GiveType", "ONE");

                    // get actions
                    List<String> actions = itemSection.getStringList("Actions");

                    // get alternative actions
                    List<String> alternativeActions = itemSection.getStringList("AlternativeActions");

                    // get random actions
                    Map<String, CaseData.Item.RandomAction> randomActions = new HashMap<>();
                    ConfigurationSection randomActionsSection = itemSection.getConfigurationSection("RandomActions");
                    if (randomActionsSection != null) {
                        for (String randomAction : randomActionsSection.getKeys(false)) {
                            ConfigurationSection randomActionSection = randomActionsSection.getConfigurationSection(randomAction);
                            if(randomActionSection != null) {
                                int actionChance = randomActionSection.getInt("Chance");
                                List<String> randomActionsList = randomActionSection.getStringList("Actions");
                                String displayName = randomActionSection.getString("DisplayName");
                                CaseData.Item.RandomAction randomActionObject = new CaseData.Item.RandomAction(actionChance, randomActionsList, displayName);
                                randomActions.put(randomAction, randomActionObject);
                            }
                        }
                    }

                    // get rgb
                    String[] rgb = null;
                    String rgbString = itemSection.getString("Item.Rgb");
                    if (rgbString != null) {
                        rgb = rgbString.replaceAll(" ", "").split(",");
                    }

                    // get material
                    String id = itemSection.getString("Item.ID");
                    String itemDisplayName = Tools.rc(itemSection.getString("Item.DisplayName"));
                    boolean enchanted = itemSection.getBoolean("Item.Enchanted");
                    ItemStack itemStack = Tools.getCaseItem(itemDisplayName, id, enchanted, rgb);
                    CaseData.Item.Material material = new CaseData.Item.Material(id, itemStack, itemDisplayName, enchanted);

                    CaseData.Item caseItem = new CaseData.Item(item, group, chance, material, giveType, actions, randomActions, rgb, alternativeActions);
                    items.put(item, caseItem);
                }
            } else {
                getLogger().warning("Case " + caseName + " has a broken case.Items section");
            }
            CaseData.HistoryData[] historyData = new CaseData.HistoryData[10];
            if(!sql) {
                ConfigurationSection dataSection = customConfig.getData().getConfigurationSection("Data");
                if (dataSection != null) {
                    ConfigurationSection caseDatasSection = dataSection.getConfigurationSection(caseName);
                    if (caseDatasSection != null) {
                        for (String i : caseDatasSection.getKeys(false)) {
                            ConfigurationSection caseDataSection = caseDatasSection.getConfigurationSection(i);
                            if (caseDataSection == null) continue;
                            CaseData.HistoryData data = new CaseData.HistoryData(
                                    caseDataSection.getString("Item"),
                                    caseName,
                                    caseDataSection.getString("Player"),
                                    caseDataSection.getLong("Time"),
                                    caseDataSection.getString("Group"),
                                    caseDataSection.getString("Action"));
                            historyData[Integer.parseInt(i)] = data;
                        }
                    }
                }
            }

            Map<String, Integer> levelGroups = new HashMap<>();
            ConfigurationSection lgSection = caseSection.getConfigurationSection("LevelGroups");
            if(lgSection != null) {
                for (String group : lgSection.getKeys(false)) {
                    int level = lgSection.getInt(group);
                    levelGroups.put(group, level);
                }
            }

            CaseData caseData = new CaseData(caseName, caseDisplayName, caseTitle,animationName, sound, items, historyData, hologram, levelGroups);
            Case.caseData.put(caseName, caseData);
        }
        Logger.log("&aCases loaded!");
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
        BukkitLibraryManager bukkitLibraryManager = new BukkitLibraryManager(this);
        bukkitLibraryManager.addMavenCentral();
        bukkitLibraryManager.loadLibrary(lib);
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
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

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
