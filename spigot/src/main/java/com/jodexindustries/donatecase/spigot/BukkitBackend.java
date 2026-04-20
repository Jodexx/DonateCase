package com.jodexindustries.donatecase.spigot;

import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.casedata.MetaUpdater;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import com.jodexindustries.donatecase.api.data.hologram.HologramDriver;
import com.jodexindustries.donatecase.api.data.hologram.HologramFactory;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.MaterialFactory;
import com.jodexindustries.donatecase.api.data.storage.CaseWorld;
import com.jodexindustries.donatecase.api.event.player.ArmorStandCreatorInteractEvent;
import com.jodexindustries.donatecase.api.manager.*;
import com.jodexindustries.donatecase.api.platform.DCOfflinePlayer;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.scheduler.DCFuture;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.api.tools.PAPI;
import com.jodexindustries.donatecase.common.DonateCase;
import com.jodexindustries.donatecase.common.gui.items.HISTORYItemHandlerImpl;
import com.jodexindustries.donatecase.common.gui.items.OPENItemClickHandlerImpl;
import com.jodexindustries.donatecase.common.platform.BackendPlatform;
import com.jodexindustries.donatecase.common.tools.ReflectionUtils;
import com.jodexindustries.donatecase.spigot.animations.firework.FireworkAnimation;
import com.jodexindustries.donatecase.spigot.animations.futurewheel.FutureWheelAnimation;
import com.jodexindustries.donatecase.spigot.animations.pop.PopAnimation;
import com.jodexindustries.donatecase.spigot.animations.rainly.RainlyAnimation;
import com.jodexindustries.donatecase.spigot.animations.select.SelectAnimation;
import com.jodexindustries.donatecase.spigot.animations.select.SelectAnimationListener;
import com.jodexindustries.donatecase.spigot.animations.shape.ShapeAnimation;
import com.jodexindustries.donatecase.spigot.animations.wheel.WheelAnimation;
import com.jodexindustries.donatecase.spigot.api.platform.BukkitOfflinePlayer;
import com.jodexindustries.donatecase.spigot.hook.packetevents.PacketEventsSupport;
import com.jodexindustries.donatecase.spigot.hook.papi.PAPISupport;
import com.jodexindustries.donatecase.spigot.listener.EventListener;
import com.jodexindustries.donatecase.spigot.materials.BASE64MaterialHandlerImpl;
import com.jodexindustries.donatecase.spigot.materials.HEADMaterialHandlerImpl;
import com.jodexindustries.donatecase.spigot.materials.MCURLMaterialHandlerImpl;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import com.jodexindustries.donatecase.spigot.tools.Metrics;
import com.jodexindustries.donatecase.spigot.tools.ToolsImpl;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.List;
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
        registerMaterials();

        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(this), plugin);

        api.load();

        // after config load
        loadHologramDrivers().thenRunSync(() -> api.getHologramManager().load());

        loadPacketEventsAPI();
        loadLuckPerms();

        scheduler.async(this, this::loadMetrics, 0L);
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
    public void dispatchConsoleCommand(@NotNull String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
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
    }

    private void registerMaterials() {
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

        // load external hooks
        scheduler.async(this, () -> {
            try {
                for (Class<?> clazz : ReflectionUtils.getClasses(getClass().getClassLoader(), "com.jodexindustries.donatecase.spigot.materials.factory")) {
                    if (!MaterialFactory.class.isAssignableFrom(clazz)) continue;

                    MaterialFactory factory = (MaterialFactory) clazz.getDeclaredField("INSTANCE").get(null);
                    CaseMaterial material = factory.create(this);
                    if (material != null) {
                        manager.register(material);
                    }
                }
            } catch (ReflectiveOperationException ignored) {
            }

            getLogger().info("Registered " + manager.getMap().size() + " materials");
        }, 0L);
    }

    private DCFuture<Void> loadHologramDrivers() {
        HologramManager manager = api.getHologramManager();

        return DCFuture.supplyAsync(() -> {
            try {
                List<Class<?>> classes = ReflectionUtils.getClasses(getClass().getClassLoader(), "com.jodexindustries.donatecase.spigot.holograms.factory");

                for (Class<?> clazz : classes) {
                    if (!HologramFactory.class.isAssignableFrom(clazz)) continue;

                    HologramFactory factory = (HologramFactory) clazz.getDeclaredField("INSTANCE").get(null);
                    HologramDriver driver = factory.create(this);

                    if (driver != null) {
                        try {
                            manager.register(factory.name().toLowerCase(), driver);
                        } catch (Throwable e) {
                            getLogger().log(Level.WARNING, "Error with loading " + factory.name() + " hologram driver: ", e);
                        }
                    }
                }
            } catch (ReflectiveOperationException ignored) {
            }

            getLogger().info("Registered " + manager.get().size() + " hologram drivers");

            return null;
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
