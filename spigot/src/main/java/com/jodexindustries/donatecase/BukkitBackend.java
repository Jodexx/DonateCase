package com.jodexindustries.donatecase;

import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.jodexindustries.donatecase.animations.*;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.HologramDriver;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.manager.*;
import com.jodexindustries.donatecase.api.platform.BukkitOfflinePlayer;
import com.jodexindustries.donatecase.api.platform.BukkitPlayer;
import com.jodexindustries.donatecase.api.platform.DCOfflinePlayer;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.api.tools.PAPI;
import com.jodexindustries.donatecase.gui.CaseGuiWrapperBukkit;
import com.jodexindustries.donatecase.gui.items.HISTORYItemHandlerImpl;
import com.jodexindustries.donatecase.gui.items.OPENItemClickHandlerImpl;
import com.jodexindustries.donatecase.impl.actions.*;
import com.jodexindustries.donatecase.impl.holograms.CMIHologramsImpl;
import com.jodexindustries.donatecase.impl.holograms.DecentHologramsImpl;
import com.jodexindustries.donatecase.impl.holograms.FancyHologramsImpl;
import com.jodexindustries.donatecase.impl.holograms.HolographicDisplaysImpl;
import com.jodexindustries.donatecase.impl.materials.*;
import com.jodexindustries.donatecase.listener.EventsListener;
import com.jodexindustries.donatecase.platform.BackendPlatform;
import com.jodexindustries.donatecase.tools.Metrics;
import com.jodexindustries.donatecase.tools.ToolsImpl;
import com.jodexindustries.donatecase.tools.support.PacketEventsSupport;
import com.jodexindustries.donatecase.tools.support.papi.PAPISupport;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BukkitBackend extends BackendPlatform {

    @Getter
    private final BukkitDonateCase plugin;

    private final DonateCase api;
    private final DCTools tools;

    private PAPI papi;
    @Getter
    private PacketEventsSupport packetEventsSupport;
    private LuckPerms luckPerms;
    public HologramDriver hologramDriver = null;
    public HologramManager hologramManager = null;

    public BukkitBackend(BukkitDonateCase plugin) {
        this.plugin = plugin;
        this.api = new DonateCase(this);
        this.tools = new ToolsImpl(this);

        DCAPI.setInstance(api);
    }


    @Override
    public void load() {
        this.papi = new PAPISupport(this);
        papi.register();
        registerDefaultCommand();
        registerDefaultSubCommands();
        registerDefaultGUITypedItems();
        registerDefaultAnimations();
        registerDefaultActions();
        registerDefaultMaterials();

        loadPacketEventsAPI();
        loadLuckPerms();

        Bukkit.getServer().getPluginManager().registerEvents(new EventsListener(), plugin);

        api.load();
        // after config load
        loadMetrics();
        loadHologramManager();
        loadHolograms();
        disableSpawnProtection();
    }

    @Override
    public void unload() {
        api.unload();
        if (hologramManager != null) hologramManager.removeAllHolograms();
        if (packetEventsSupport != null) packetEventsSupport.unload();

        Bukkit.getWorlds().stream()
                .flatMap(world -> world.getEntitiesByClass(ArmorStand.class).stream())
                .filter(stand -> stand.hasMetadata("case"))
                .forEach(Entity::remove);
    }

    @Override
    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    @Override
    public PAPI getPAPI() {
        return papi;
    }

    @Override
    public HologramManager getHologramManager() {
        return hologramManager;
    }

    @Override
    public CaseGuiWrapper createGui(DCPlayer player, CaseData caseData, CaseLocation location) {
        return new CaseGuiWrapperBukkit(this, player, caseData, location);
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public String getIdentifier() {
        return "Bukkit";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @NotNull File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public DCTools getTools() {
        return tools;
    }

    @Override
    public DCAPI getAPI() {
        return api;
    }

    @Override
    public void runSync(@NotNull Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

    @Override
    public DCPlayer[] getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(BukkitPlayer::new).toArray(DCPlayer[]::new);
    }

    @Override
    public DCOfflinePlayer[] getOfflinePlayers() {
        return Arrays.stream(Bukkit.getOfflinePlayers()).map(BukkitOfflinePlayer::new).toArray(DCOfflinePlayer[]::new);
    }

    private void registerDefaultCommand() {
        PluginCommand command = plugin.getCommand("donatecase");
        BukkitCommand bukkitCommand = new BukkitCommand(this);
        if (command != null) {
            command.setExecutor(bukkitCommand);
            command.setTabCompleter(bukkitCommand);
        }
    }

    private void registerDefaultSubCommands() {
        SubCommandManager manager = api.getSubCommandManager();
        manager.registerDefaultSubCommands();

        getLogger().info("Registered " + manager.getRegisteredSubCommands().size() + " commands");
    }

    private void registerDefaultGUITypedItems() {
        GUITypedItemManager manager = api.getGuiTypedItemManager();

        manager.registerItem(
                manager.builder("HISTORY", this)
                        .description("Type for displaying the history of case openings")
                        .handler(new HISTORYItemHandlerImpl())
                        .build()
        );

        manager.registerItem(
                manager.builder("OPEN", this)
                        .description("Type to open the case")
                        .click(new OPENItemClickHandlerImpl())
                        .setUpdateMeta(true)
                        .setLoadOnCase(true)
                        .build()
        );

        getLogger().info("Registered " + manager.getRegisteredItems().size() + " gui typed items");
    }


    private void registerDefaultAnimations() {
        AnimationManager manager = api.getAnimationManager();

        manager.registerAnimation(
                CaseAnimation.builder()
                        .name("SHAPE")
                        .addon(this)
                        .animation(ShapeAnimation.class)
                        .description("Items flip through and a shape appears")
                        .requireSettings(true)
                        .requireBlock(true)
                        .build()
        );

        manager.registerAnimation(
                CaseAnimation.builder()
                        .name("RAINLY")
                        .addon(this)
                        .animation(RainlyAnimation.class)
                        .description("Rain drips from the clouds")
                        .requireSettings(true)
                        .requireBlock(true)
                        .build()
        );

        manager.registerAnimation(
                CaseAnimation.builder()
                        .name("FIREWORK")
                        .addon(this)
                        .animation(FireworkAnimation.class)
                        .description("Fireworks fly to the skies and a prize appears")
                        .requireSettings(true)
                        .requireBlock(true)
                        .build()
        );

        manager.registerAnimation(
                CaseAnimation.builder()
                        .name("WHEEL")
                        .addon(this)
                        .animation(WheelAnimation.class)
                        .description("Items resolve around the case")
                        .requireSettings(true)
                        .requireBlock(true)
                        .build()
        );

        manager.registerAnimation(
                CaseAnimation.builder()
                        .name("RANDOM")
                        .addon(this)
                        .animation(RandomAnimation.class)
                        .description("Selects the random animation from config")
                        .requireSettings(true)
                        .requireBlock(true)
                        .build()
        );

        getLogger().info("Registered " + manager.getRegisteredAnimations().size() + " animations");
    }

    private void registerDefaultActions() {
        ActionManager manager = api.getActionManager();
        manager.registerAction("[command]", new CommandActionExecutorImpl(),
                "Sends a command to the console", this);
        manager.registerAction("[message]", new MessageActionExecutorImpl(),
                "Sends a message in the player's chat", this);
        manager.registerAction("[title]", new TitleActionExecutorImpl(),
                "Sends a title to the player", this);
        manager.registerAction("[broadcast]", new BroadcastActionExecutorImpl(),
                "Sends a broadcast to the players", this);
        manager.registerAction("[sound]", new SoundActionExecutorImpl(),
                "Sends a sound to the player", this);
        getLogger().info("Registered " + manager.getRegisteredActions().size() + " actions");
    }

    private void registerDefaultMaterials() {
        MaterialManager manager = api.getMaterialManager();
        manager.registerMaterial("BASE64", new BASE64MaterialHandlerImpl(),
                "Heads from Minecraft-heads by BASE64 value", this);
        manager.registerMaterial("MCURL", new MCURLMaterialHandlerImpl(),
                "Heads from Minecraft-heads by Minecrat-URL", this);
        manager.registerMaterial("HEAD", new HEADMaterialHandlerImpl(),
                "Default Minecraft heads by nickname", this);

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
            manager.registerMaterial("IA", new IAMaterialHandlerImpl(),
                    "Items from ItemsAdder plugin", this);
        }

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Oraxen")) {
            manager.registerMaterial("ORAXEN", new OraxenMaterialHandlerImpl(),
                    "Items from Oraxen plugin", this);
        }

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
            manager.registerMaterial("CH", new CHMaterialHandlerImpl(),
                    "Heads from CustomHeads plugin", this);
        }

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("HeadDatabase")) {
            manager.registerMaterial("HDB", new HDBMaterialHandlerImpl(),
                    "Heads from HeadDatabase plugin", this);
        }
    }

    private void loadHologramManager() {
        String driverName = api.getConfig().getConfig().node("DonateCase", "HologramDriver").getString("decentholograms");
        hologramDriver = HologramDriver.getDriver(driverName);

        tryInitializeHologramManager(hologramDriver);

        if (hologramManager == null) {
            for (HologramDriver fallbackDriver : HologramDriver.values()) {
                if (tryInitializeHologramManager(fallbackDriver)) {
                    hologramDriver = fallbackDriver;
                    break;
                }
            }
        }

        if (hologramManager != null) getLogger().info("Using " + hologramDriver + " as hologram driver");
    }

    private boolean tryInitializeHologramManager(HologramDriver driver) {
        switch (driver) {
            case cmi:
                if (Bukkit.getServer().getPluginManager().isPluginEnabled("CMI") && CMIModule.holograms.isEnabled()) {
                    hologramManager = new CMIHologramsImpl();
                    return true;
                }
                break;
            case decentholograms:
                if (Bukkit.getServer().getPluginManager().isPluginEnabled("DecentHolograms")) {
                    hologramManager = new DecentHologramsImpl();
                    return true;
                }
                break;
            case holographicdisplays:
                if (Bukkit.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
                    hologramManager = new HolographicDisplaysImpl();
                    return true;
                }
                break;
            case fancyholograms:
                if(Bukkit.getServer().getPluginManager().isPluginEnabled("FancyHolograms")) {
                    hologramManager = new FancyHologramsImpl();
                    return true;
                }
        }
        return false;
    }

    public void loadHolograms() {
        if(hologramManager == null) return;

        hologramManager.removeAllHolograms();
        for (Map.Entry<String, CaseInfo> entry : api.getConfig().getCaseStorage().get().entrySet()) {
            CaseInfo info = entry.getValue();

            String caseType = info.getType();

            CaseData caseData = api.getCaseManager().getCase(caseType);

            if(caseData == null || !caseData.getHologram().isEnabled()) continue;

            CaseLocation location = info.getLocation();

            World world = Bukkit.getWorld(location.getWorld());

            if(world == null) {
                getLogger().warning("Hologram creation error. World " + location.getWorld() + " is null for case name: " + entry.getKey());
                continue;
            }

            hologramManager.createHologram(location, caseData);
        }
    }

    private void loadPacketEventsAPI() {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("packetevents")) {
            try {
                packetEventsSupport = new PacketEventsSupport(this);
            } catch (Throwable e) {
                getLogger().log(Level.WARNING, "Error hooking to packetevents: ", e);
            }
        }
    }

    private void loadLuckPerms() {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServer().getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                luckPerms = provider.getProvider();
            }
        }
    }

    private void loadMetrics() {
        Metrics metrics = new Metrics(plugin, 18709);
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> api.getConfig().getConfig().node("DonateCase", "Languages").getString()));
    }

    private void disableSpawnProtection() {
        if (api.getConfig().getConfig().node("DonateCase", "DisableSpawnProtection").getBoolean()) {
            if (Bukkit.getServer().getSpawnRadius() > 0) {
                Bukkit.getServer().setSpawnRadius(0);
                getLogger().info("Spawn protection disabled!");
            }
        }
    }

}
