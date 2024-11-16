package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.config.Config;
import com.jodexindustries.donatecase.database.CaseDatabaseImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.jodexindustries.donatecase.DonateCase.*;
import static com.jodexindustries.donatecase.database.CaseDatabaseImpl.historyCache;


/**
 * The main class for API interaction with DonateCase, this is where most of the functions are located.
 */ 
public class Case {

    /**
     * Default constructor, but actually not used. All methods are static.
     */
    public Case() {}

    /**
     * Save case location
     * @param caseName Case name (custom)
     * @param type Case type (config)
     * @param location Case location
     */
    public static void saveLocation(String caseName, String type, Location location) {
        CaseDataBukkit caseData = instance.api.getCaseManager().getCase(type);
        if(location.getWorld() == null) {
            instance.getLogger().warning("Error with saving location: world not found!");
            return;
        }
        if(instance.hologramManager != null && (caseData != null && caseData.getHologram().isEnabled())) instance.hologramManager.createHologram(location.getBlock(), caseData);
        String tempLocation = location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getPitch() + ";" + location.getYaw();
        getConfig().getCases().set("DonateCase.Cases." + caseName + ".location", tempLocation);
        getConfig().getCases().set("DonateCase.Cases." + caseName + ".type", type);
        getConfig().saveCases();
    }

    /**
     * Delete case by name in Cases.yml
     * @param name Case name
     */
    public static void deleteCaseByName(String name) {
        getConfig().getCases().set("DonateCase.Cases." + name, null);
        getConfig().saveCases();
    }

    /**
     * Check if case has by location
     * @param loc Case location
     * @return Boolean
     */
    public static boolean hasCaseByLocation(Location loc) {
        return getCaseTypeByLocation(loc) != null;
    }

    /**
     * Get case information by location
     * @param loc Case location
     * @param infoType Information type ("type", "name" or "location")
     * @return Case information
     */
    private static <T> T getCaseInfoByLocation(Location loc, String infoType, Class<T> clazz) {
        T object = null;
        ConfigurationSection casesSection = getConfig().getCases().getConfigurationSection("DonateCase.Cases");
        if (casesSection == null) return null;

        for (String name : casesSection.getValues(false).keySet()) {
            ConfigurationSection caseSection = casesSection.getConfigurationSection(name);
            if (caseSection == null) continue;

            String location = caseSection.getString("location");
            if (location == null) continue;

            String[] worldLocation = location.split(";");
            World world = Bukkit.getWorld(worldLocation[0]);
            try {
                Location temp = new Location(world, Double.parseDouble(worldLocation[1]), Double.parseDouble(worldLocation[2]), Double.parseDouble(worldLocation[3]));

                if (temp.equals(loc)) {
                    switch (infoType) {
                        case "type":
                            object = clazz.cast(caseSection.getString("type"));
                            break;
                        case "name":
                            object = clazz.cast(name);
                            break;
                        case "location": {
                            Location result = temp.clone();
                            result.setPitch(Float.parseFloat(worldLocation[4]));
                            result.setYaw(Float.parseFloat(worldLocation[5]));
                            object = clazz.cast(result);
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        return object;
    }

    /**
     * Get case type by location
     * @param loc Case location
     * @return Case type
     */
    public static String getCaseTypeByLocation(Location loc) {
        return getCaseInfoByLocation(loc, "type", String.class);
    }

    /**
     * Get case name by location
     * @param loc Case location
     * @return Case name
     */
    public static String getCaseCustomNameByLocation(Location loc) {
        return getCaseInfoByLocation(loc, "name", String.class);
    }

    /**
     * Get case location (in Cases.yml) by block location
     * @param loc Block location
     * @return case location in Cases.yml (with yaw and pitch)
     */
    public static Location getCaseLocationByBlockLocation(Location loc) {
        return getCaseInfoByLocation(loc, "location", Location.class);
    }

    /**
     * Is there a case with a specific custom name?
     * <p>
     * In other words, whether a case has been created
     * @param name Case name
     * @return true - if case created on the server
     */
    public static boolean hasCaseByCustomName(String name) {
        ConfigurationSection section = getConfig().getCases().getConfigurationSection("DonateCase.Cases");
        if(section == null) return false;

        return getConfig().getCases().getConfigurationSection("DonateCase.Cases") != null
                && section.contains(name);
    }

    /**
     * Get plugin instance
     * @return DonateCase instance
     */
    public static DonateCase getInstance() {
        return instance;
    }

    /** Get plugin configuration manager
     * @return configuration manager instance
     * @since 2.2.3.8
     */
    @NotNull
    public static Config getConfig() {
        return getInstance().config;
    }

    /**
     * Get plugin database manager
     *
     * @return database manager
     * @since 2.2.6.5
     */
    public static CaseDatabaseImpl<CaseDataBukkit, CaseDataMaterialBukkit, ItemStack> getDatabase() {
        return getInstance().database;
    }

    /**
     * Get case location by custom name (/dc create (type) (customname)
     * @param name Case custom name
     * @return Case name
     */
    @Nullable
    public static Location getCaseLocationByCustomName(String name) {
        String location = getConfig().getCases().getString("DonateCase.Cases." + name + ".location");
        if (location == null) return null;
        String[] worldLocation = location.split(";");
        World world = Bukkit.getWorld(worldLocation[0]);
        return new Location(world, Double.parseDouble(worldLocation[1]), Double.parseDouble(worldLocation[2]), Double.parseDouble(worldLocation[3]));
    }

    /**
     * Trying to clean all entities with "case" metadata value,
     * all loaded cases in runtime,
     * all active cases, keys and open caches
     * @since 2.2.3.8
     */
    public static void cleanCache() {
        instance.api.getGUIManager().getPlayersGUI().values().parallelStream().forEach(gui -> gui.getPlayer().closeInventory());

        Bukkit.getWorlds().stream()
                .flatMap(world -> world.getEntitiesByClass(ArmorStand.class).stream())
                .filter(stand -> stand.hasMetadata("case"))
                .forEach(Entity::remove);

        instance.api.getGUIManager().getPlayersGUI().clear();
        instance.api.getCaseManager().getMap().clear();
        instance.api.getAnimationManager().getActiveCases().clear();
        instance.api.getAnimationManager().getActiveCasesByBlock().clear();
        instance.api.getCaseOpenManager().getCache().clear();
        instance.api.getCaseKeyManager().getCache().clear();
        historyCache.clear();
    }

}