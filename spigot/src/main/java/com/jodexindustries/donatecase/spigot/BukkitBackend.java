package com.jodexindustries.donatecase.spigot;

import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.jodexindustries.donatecase.api.data.hologram.HologramDriver;
import com.jodexindustries.donatecase.api.data.storage.CaseWorld;
import com.jodexindustries.donatecase.api.event.player.ArmorStandCreatorInteractEvent;
import com.jodexindustries.donatecase.common.DonateCase;
import com.jodexindustries.donatecase.spigot.actions.CommandActionExecutorImpl;
import com.jodexindustries.donatecase.spigot.actions.SoundActionExecutorImpl;
import com.jodexindustries.donatecase.spigot.actions.TitleActionExecutorImpl;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.casedata.MetaUpdater;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.manager.*;
import com.jodexindustries.donatecase.spigot.animations.firework.FireworkAnimation;
import com.jodexindustries.donatecase.spigot.animations.futurewheel.FutureWheelAnimation;
import com.jodexindustries.donatecase.spigot.animations.pop.PopAnimation;
import com.jodexindustries.donatecase.spigot.animations.rainly.RainlyAnimation;
import com.jodexindustries.donatecase.spigot.animations.select.SelectAnimation;
import com.jodexindustries.donatecase.spigot.animations.select.SelectAnimationListener;
import com.jodexindustries.donatecase.spigot.animations.shape.ShapeAnimation;
import com.jodexindustries.donatecase.spigot.api.platform.BukkitOfflinePlayer;
import com.jodexindustries.donatecase.api.platform.DCOfflinePlayer;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.api.tools.PAPI;
import com.jodexindustries.donatecase.spigot.animations.wheel.WheelAnimation;
import com.jodexindustries.donatecase.common.gui.items.HISTORYItemHandlerImpl;
import com.jodexindustries.donatecase.common.gui.items.OPENItemClickHandlerImpl;
import com.jodexindustries.donatecase.spigot.holograms.CMIHologramsImpl;
import com.jodexindustries.donatecase.spigot.holograms.DecentHologramsImpl;
import com.jodexindustries.donatecase.spigot.holograms.FancyHologramsImpl;
import com.jodexindustries.donatecase.spigot.holograms.HolographicDisplaysImpl;
import com.jodexindustries.donatecase.spigot.listener.EventListener;
import com.jodexindustries.donatecase.common.platform.BackendPlatform;
import com.jodexindustries.donatecase.spigot.materials.*;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import com.jodexindustries.donatecase.spigot.tools.Metrics;
import com.jodexindustries.donatecase.spigot.tools.ToolsImpl;
import com.jodexindustries.donatecase.spigot.hook.packetevents.PacketEventsSupport;
import com.jodexindustries.donatecase.spigot.hook.papi.PAPISupport;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BukkitBackend extends BackendPlatform {

    @Getter
    private final BukkitDonateCase plugin;

    private final DonateCase api;
    private final DCTools tools;
    private final BukkitScheduler scheduler;

    private PAPI papi;
    @Getter
    private PacketEventsSupport packetEventsSupport;

    private MetaUpdater metaUpdater;

    private Metrics metrics;

    public BukkitBackend(BukkitDonateCase plugin) {
        this.plugin = plugin;
        this.api = new DonateCase(this);
        this.tools = new ToolsImpl(this);
        this.scheduler = new BukkitScheduler(this);
    }


    @Override
    public void load() {
        this.papi = new PAPISupport(this);
        papi.register();

        this.metaUpdater = new BukkitMetaUpdater();

        registerDefaultCommand();
        registerDefaultGUITypedItems();
        registerDefaultAnimations();
        registerDefaultActions();
        registerDefaultMaterials();

        loadHologramDrivers();

        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(this), plugin);

        api.load();

        // after config load
        loadPacketEventsAPI();
        loadLuckPerms();
        loadMetrics();
    }

    @Override
    public void unload() {
        api.unload();
        if (packetEventsSupport != null) packetEventsSupport.unload();
        if (metrics != null) metrics.shutdown();

        Bukkit.getWorlds().stream()
                .flatMap(world -> world.getEntitiesByClass(ArmorStand.class).stream())
                .filter(stand -> stand.hasMetadata("case"))
                .forEach(Entity::remove);
    }

    @Override
    public PAPI getPAPI() {
        return papi;
    }

    @Override
    public MetaUpdater getMetaUpdater() {
        return metaUpdater;
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
    public DonateCase getAPI() {
        return api;
    }

    @Override
    public @NotNull BukkitScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public DCPlayer getPlayer(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) return null;

        return BukkitUtils.fromBukkit(player);
    }

    @Override
    public DCPlayer[] getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(BukkitUtils::fromBukkit).toArray(DCPlayer[]::new);
    }

    @Override
    public DCOfflinePlayer[] getOfflinePlayers() {
        return Arrays.stream(Bukkit.getOfflinePlayers()).map(BukkitOfflinePlayer::new).toArray(DCOfflinePlayer[]::new);
    }

    @Override
    public @Nullable CaseWorld getWorld(String world) {
        return BukkitUtils.fromBukkit(Bukkit.getWorld(world));
    }

    @Override
    public boolean isWorldLoaded(String world) {
        return Bukkit.getWorld(world) != null;
    }

    @Override
    public int getSpawnRadius() {
        return Bukkit.getSpawnRadius();
    }

    private void registerDefaultCommand() {
        PluginCommand command = plugin.getCommand("donatecase");
        BukkitCommand bukkitCommand = new BukkitCommand(this);
        if (command != null) {
            command.setExecutor(bukkitCommand);
            command.setTabCompleter(bukkitCommand);
        }
    }

    private void registerDefaultGUITypedItems() {
        GUITypedItemManager manager = api.getGuiTypedItemManager();

        manager.register(
                TypedItem.builder()
                        .id("HISTORY")
                        .addon(this)
                        .description("Type for displaying the history of case openings")
                        .handler(new HISTORYItemHandlerImpl())
                        .build()
        );

        manager.register(
                TypedItem.builder()
                        .id("OPEN")
                        .addon(this)
                        .description("Type to open the case")
                        .click(new OPENItemClickHandlerImpl())
                        .updateMeta(true)
                        .loadOnCase(true)
                        .build()
        );

        getLogger().info("Registered " + manager.getMap().size() + " gui typed items");
    }


    private void registerDefaultAnimations() {
        AnimationManager manager = api.getAnimationManager();

        manager.register(
                CaseAnimation.builder()
                        .name("SHAPE")
                        .addon(this)
                        .animation(ShapeAnimation.class)
                        .description("Items flip through and a shape appears")
                        .requireSettings(true)
                        .requireBlock(true)
                        .build()
        );

        manager.register(
                CaseAnimation.builder()
                        .name("RAINLY")
                        .addon(this)
                        .animation(RainlyAnimation.class)
                        .description("Rain drips from the clouds")
                        .requireSettings(true)
                        .requireBlock(true)
                        .build()
        );

        manager.register(
                CaseAnimation.builder()
                        .name("FIREWORK")
                        .addon(this)
                        .animation(FireworkAnimation.class)
                        .description("Fireworks fly to the skies and a prize appears")
                        .requireSettings(true)
                        .requireBlock(true)
                        .build()
        );

        manager.register(
                CaseAnimation.builder()
                        .name("WHEEL")
                        .addon(this)
                        .animation(WheelAnimation.class)
                        .description("Items resolve around the case")
                        .requireSettings(true)
                        .requireBlock(true)
                        .build()
        );

        manager.register(
                CaseAnimation.builder()
                        .name("SELECT")
                        .addon(this)
                        .animation(SelectAnimation.class)
                        .description("Select your prize manually")
                        .requireSettings(true)
                        .requireBlock(true)
                        .build()
        );

        api.getEventBus().register(ArmorStandCreatorInteractEvent.class, new SelectAnimationListener());

        manager.register(
                CaseAnimation.builder()
                        .name("POP")
                        .addon(this)
                        .animation(PopAnimation.class)
                        .description("Items pop")
                        .requireSettings(true)
                        .requireBlock(true)
                        .build()
        );

        manager.register(
                CaseAnimation.builder()
                        .name("FUTURE_WHEEL")
                        .addon(this)
                        .animation(FutureWheelAnimation.class)
                        .description("Improved version of the WHEEL animation")
                        .requireSettings(true)
                        .requireBlock(true)
                        .build()
        );

        getLogger().info("Registered " + manager.getMap().size() + " animations");
    }

    private void registerDefaultActions() {
        ActionManager manager = api.getActionManager();

        manager.register(
                CaseAction.builder()
                        .name("[command]")
                        .addon(this)
                        .executor(new CommandActionExecutorImpl())
                        .description("Sends a command to the console")
                        .build()
        );

        manager.register(
                CaseAction.builder()
                        .name("[title]")
                        .addon(this)
                        .executor(new TitleActionExecutorImpl())
                        .description("Sends a title to the player")
                        .build()
        );

        manager.register(
                CaseAction.builder()
                        .name("[sound]")
                        .addon(this)
                        .executor(new SoundActionExecutorImpl())
                        .description("Sends a sound to the player")
                        .build()
        );

        getLogger().info("Registered " + manager.getMap().size() + " actions");
    }

    private void registerDefaultMaterials() {
        MaterialManager manager = api.getMaterialManager();

        manager.register(
                CaseMaterial.builder()
                        .id("BASE64")
                        .addon(this)
                        .handler(new BASE64MaterialHandlerImpl())
                        .description("Heads from Minecraft-heads by BASE64 value")
                        .build()
        );

        manager.register(
                CaseMaterial.builder()
                        .id("MCURL")
                        .addon(this)
                        .handler(new MCURLMaterialHandlerImpl())
                        .description("Heads from Minecraft-heads by Minecrat-URL")
                        .build()
        );

        manager.register(
                CaseMaterial.builder()
                        .id("HEAD")
                        .addon(this)
                        .handler(new HEADMaterialHandlerImpl())
                        .description("Default Minecraft heads by nickname")
                        .build()
        );

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
            manager.register(
                    CaseMaterial.builder()
                            .id("IA")
                            .addon(this)
                            .handler(new IAMaterialHandlerImpl())
                            .description("Items from ItemsAdder plugin")
                            .build()
            );
        }

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Oraxen")) {
            manager.register(
                    CaseMaterial.builder()
                            .id("ORAXEN")
                            .addon(this)
                            .handler(new OraxenMaterialHandlerImpl())
                            .description("Items from Oraxen plugin")
                            .build()
            );
        }

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
            manager.register(
                    CaseMaterial.builder()
                            .id("CH")
                            .addon(this)
                            .handler(new CHMaterialHandlerImpl())
                            .description("Heads from CustomHeads plugin")
                            .build()
            );
        }

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("HeadDatabase")) {
            manager.register(
                    CaseMaterial.builder()
                            .id("HDB")
                            .addon(this)
                            .handler(new HDBMaterialHandlerImpl())
                            .description("Heads from HeadDatabase plugin")
                            .build()
            );
        }

        getLogger().info("Registered " + manager.getMap().size() + " materials");
    }

    private void loadHologramDrivers() {
        HologramManager manager = api.getHologramManager();
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        Map<String, Supplier<Class<? extends HologramDriver>>> drivers = new HashMap<>();
        drivers.put("CMI", () -> CMIModule.holograms.isEnabled() ? CMIHologramsImpl.class : null);
        drivers.put("DecentHolograms", () -> DecentHologramsImpl.class);
        drivers.put("HolographicDisplays", () -> HolographicDisplaysImpl.class);
        drivers.put("FancyHolograms", () -> FancyHologramsImpl.class);

        drivers.forEach((plugin, provider) -> {
            if (pluginManager.isPluginEnabled(plugin)) {
                Class<? extends HologramDriver> driver = provider.get();
                if (driver != null) {
                    try {
                        manager.register(plugin.toLowerCase(), driver.newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        getLogger().log(Level.WARNING, "Error with loading " + plugin + " hologram driver: ", e);
                    }
                }
            }
        });
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
            getLuckPermsSupport().load();
        }
    }

    private void loadMetrics() {
        this.metrics = new Metrics(plugin, 18709);
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> api.getConfigManager().getConfig().language()));
        metrics.addCustomChart(new Metrics.SimplePie("use_packets", () -> packetEventsSupport != null ? packetEventsSupport.isUsePackets() ? "yes" : "no" : "no"));
        metrics.addCustomChart(new Metrics.SimplePie("hologram_driver", () -> api.getConfigManager().getConfig().hologramDriver()));
    }

}
