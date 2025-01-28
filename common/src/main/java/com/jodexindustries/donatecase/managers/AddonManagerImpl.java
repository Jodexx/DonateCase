package com.jodexindustries.donatecase.managers;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.jodexindustries.donatecase.BuildConstants;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.PowerReason;
import com.jodexindustries.donatecase.api.addon.InternalAddonClassLoader;
import com.jodexindustries.donatecase.api.addon.InternalAddonDescription;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;
import com.jodexindustries.donatecase.api.addon.InvalidAddonException;
import com.jodexindustries.donatecase.api.event.addon.AddonDisableEvent;
import com.jodexindustries.donatecase.api.event.addon.AddonEnableEvent;
import com.jodexindustries.donatecase.api.manager.AddonManager;
import com.jodexindustries.donatecase.api.platform.Platform;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class AddonManagerImpl implements AddonManager {

    private static final Map<String, InternalJavaAddon> addons = new HashMap<>();
    private static final List<InternalAddonClassLoader> loaders = new CopyOnWriteArrayList<>();

    private MutableGraph<String> dependencyGraph = GraphBuilder.directed().build();

    private final DCAPI api;
    private final Platform platform;
    private final File folder;

    public AddonManagerImpl(DCAPI api) {
        this.api = api;
        this.platform = api.getPlatform();
        this.folder = new File(platform.getDataFolder(), "addons");
    }

    @Override
    public void load() {
        File addonsDir = folder;
        if (!addonsDir.exists()) {
            if(!addonsDir.mkdir()) return;
        }

        File[] files = addonsDir.listFiles();
        if (files == null) return;

        Map<String, InternalAddonDescription> descriptions = new HashMap<>();

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                try {
                    InternalAddonDescription description = new InternalAddonDescription(file);
                    descriptions.put(description.getName(), description);

                    Collection<String> depend = description.getDepend();
                    Collection<String> softDepend = description.getSoftDepend();

                    for (String dependency : depend) {
                        dependencyGraph.putEdge(description.getName(), dependency);
                    }
                    for (String softDependency : softDepend) {
                        dependencyGraph.putEdge(description.getName(), softDependency);
                    }
                } catch (IOException | InvalidAddonException e) {
                    platform.getLogger().log(Level.SEVERE, "Failed to parse addon: " + file.getName(), e);
                }
            }
        }

        List<String> loadOrder = resolveLoadOrder();
        if (loadOrder == null) {
            platform.getLogger().severe("Cyclic dependency detected! Aborting addon loading.");
            return;
        }

        for (String addonName : loadOrder) {
            InternalAddonDescription description = descriptions.get(addonName);
            if (description != null) {
                loadAddon(description);
            }
        }

        for (InternalAddonDescription description : descriptions.values()) {
            if(!addons.containsKey(description.getName())) loadAddon(description);
        }
    }

    /**
     * Resolves the load order of addons based on their dependencies using topological sort.
     *
     * @return A list of addon names in load order, or null if a cycle is detected.
     */
    private List<String> resolveLoadOrder() {
        List<String> sorted = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        for (String addon : dependencyGraph.nodes()) {
            if (!visited.contains(addon) && !topologicalSort(addon, sorted, visited, visiting)) {
                return null;
            }
        }

        return sorted;
    }

    /**
     * Performs a topological sort for a single addon.
     *
     * @param addon    The current addon.
     * @param sorted   The resulting sorted list.
     * @param visited  The set of fully visited addons.
     * @param visiting The set of currently visiting addons (for cycle detection).
     * @return True if the sort is successful, false if a cycle is detected.
     */
    private boolean topologicalSort(String addon, List<String> sorted, Set<String> visited, Set<String> visiting) {
        if (visiting.contains(addon)) {
            return false; // Cycle detected
        }
        if (visited.contains(addon)) {
            return true; // Already processed
        }

        visiting.add(addon);
        for (String dependency : dependencyGraph.successors(addon)) {
            if (!topologicalSort(dependency, sorted, visited, visiting)) {
                return false;
            }
        }
        visiting.remove(addon);
        visited.add(addon);
        sorted.add(addon);

        return true;
    }

    private boolean loadAddon(InternalAddonDescription description) {
        platform.getLogger().info("Loading " + description.getName() + " addon v" + description.getVersion());
        if (addons.get(description.getName()) != null) {
            if (description.getName().equalsIgnoreCase("DonateCase")) {
                platform.getLogger().warning("Addon " + description.getName() + " trying to load with DonateCase name! Abort.");
                return false;
            }
            platform.getLogger().warning("Addon with name " + description.getName() + " already loaded!");
            return false;
        }

        if (description.getApiVersion() != null) {
            int addonVersion = DCTools.getPluginVersion(description.getApiVersion());
            int pluginVersion = DCTools.getPluginVersion(BuildConstants.api);
            int supportedVersion = DCTools.getPluginVersion(BuildConstants.supported);

            if (pluginVersion < addonVersion || addonVersion < supportedVersion) {
                platform.getLogger().warning("Addon " + description.getName() + " API version (" + description.getApiVersion()
                        + ") incompatible with current API version (" + BuildConstants.api + ")! Abort.");
                return false;
            }
        }

        if (!description.isSupport(platform.getIdentifier())) {
            platform.getLogger().warning("Addon " + description.getName() + " does not support " + platform.getIdentifier() + " platform!");
            return false;
        }

        try {
            InternalAddonClassLoader loader = new InternalAddonClassLoader(platform.getClass().getClassLoader(), description, this, platform);
            InternalJavaAddon addon = loader.getAddon();
            addon.onLoad();
            addons.put(description.getName(), addon);
            loaders.add(loader);
            return true;
        } catch (Throwable e) {
            platform.getLogger().log(Level.SEVERE,
                    "Error occurred while loading addon " + description.getName() + " v" + description.getVersion(), e);
        }
        return false;
    }

    @Override
    public boolean load(File file) {
        if (file.isFile() && file.getName().endsWith(".jar")) {
            InternalAddonDescription description;
            try {
                description = new InternalAddonDescription(file);
            } catch (IOException | InvalidAddonException e) {
                throw new RuntimeException(e);
            }

            return loadAddon(description);
        }
        return false;
    }

    @Override
    public void enable(PowerReason reason) {
        Collection<InternalJavaAddon> list = addons.values();
        for (InternalJavaAddon internalJavaAddon : list) {
            enable(internalJavaAddon, reason);
        }
    }

    @Override
    public boolean enable(@NotNull InternalJavaAddon addon, PowerReason reason) {
        try {
            if (!addon.isEnabled()) {
                platform.getLogger().info("Enabling " + addon.getName() + " addon v" + addon.getVersion());
                addon.setEnabled(true);
                api.getEventBus().post(new AddonEnableEvent(addon, reason));

                return true;
            }
        } catch (Throwable t) {
            platform.getLogger().log(Level.SEVERE,
                    "Error occurred while enabling addon " + addon.getName() + " v" + addon.getVersion(), t);
        }
        return false;
    }

    @Override
    public boolean disable(@NotNull InternalJavaAddon addon, PowerReason reason) {
        try {
            if (addon.isEnabled()) {
                platform.getLogger().info("Disabling " + addon.getName() + " addon v" + addon.getVersion());
                addon.setEnabled(false);
                api.getActionManager().unregister(addon);
                api.getAnimationManager().unregister(addon);
                api.getGuiTypedItemManager().unregister(addon);
                api.getMaterialManager().unregister(addon);
                api.getSubCommandManager().unregister(addon);
                api.getEventBus().post(new AddonDisableEvent(addon, reason));

                return true;
            }
        } catch (Throwable t) {
            platform.getLogger().log(Level.SEVERE,
                    "Error occurred while disabling addon " + addon.getName() + " v" + addon.getVersion(), t);
        }
        return false;
    }

    @Override
    public void unload(PowerReason reason) {
        List<InternalJavaAddon> list = new ArrayList<>(addons.values());
        for (InternalJavaAddon internalJavaAddon : list) {
            unload(internalJavaAddon, reason);
        }
        dependencyGraph = GraphBuilder.directed().build();
        addons.clear();
        loaders.clear();
    }

    @Override
    public boolean unload(@NotNull InternalJavaAddon addon, PowerReason reason) {
        try {
            disable(addon, reason);
            addons.remove(addon.getName());
            loaders.remove(addon.getUrlClassLoader());
            addon.getUrlClassLoader().close();
            return true;
        } catch (Throwable e) {
            platform.getLogger().log(Level.SEVERE, e.getLocalizedMessage(), e.getCause());
        }
        return false;
    }

    @Nullable
    @Override
    public InternalJavaAddon get(String addon) {
        return addons.get(addon);
    }

    @Override
    public @NotNull Map<String, InternalJavaAddon> getMap() {
        return addons;
    }

    @Override
    public @NotNull File getFolder() {
        return folder;
    }

    /**
     * Gets InternalAddonClassLoader
     *
     * @param file Addon jar file
     * @return InternalAddon ClassLoader
     */
    @Nullable
    public static InternalAddonClassLoader getAddonClassLoader(File file) {
        return loaders.stream().filter(loader -> loader.getFile().equals(file)).findFirst().orElse(null);
    }


    @Nullable
    @Override
    public Class<?> getClassByName(String name, boolean resolve) {
        for (InternalAddonClassLoader loader : loaders) {
            try {
                return loader.loadClass0(name, resolve, false);
            } catch (ClassNotFoundException ignore) {
            }
        }
        return null;
    }

}