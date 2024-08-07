package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import com.jodexindustries.donatecase.api.events.CaseActionRegisteredEvent;
import com.jodexindustries.donatecase.api.events.CaseActionUnregisteredEvent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for managing executable actions. <br>
 * Used in case configuration like this:
 * <pre>{@code
 *       Actions: # GiveType: ONE
 *         - '[command] lp user %player% parent set %group%'
 *         - '[title] &aCongratulations!;&5you won %groupdisplayname%'
 *         - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
 *       AlternativeActions: # GiveType: any, it doesn't matter; is performed if the group is lower in rank than the player's group in LevelGroups
 *         - "[message] &cI'm sorry %player%, but you have group a stronger group than you won:("
 *       RandomActions: # GiveType: RANDOM
 *         first:
 *           Chance: 50
 *           DisplayName: "something" # displayname for historydata displaying
 *           Actions:
 *             - '[command] say something'
 *             - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
 *         second:
 *           Chance: 50
 *           Actions:
 *             - '[command] say something'
 *             - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
 * }</pre>
 *
 * Default actions like: {@code [command], [broadcast], [message], [title], etc.} loading here.
 */
public class ActionManager {
    private static final Map<String, CaseAction> registeredActions = new HashMap<>();
    private final Addon addon;

    /**
     * Default constructor
     * @param addon An addon that will manage actions
     */
    public ActionManager(Addon addon) {
        this.addon = addon;
    }

    /**
     *
     * Register action
     * @param name Action name, like: "[command]"
     * @param actionExecutor Action executor
     * @param description Action description
     */
    public void registerAction(String name, ActionExecutor actionExecutor, String description) {
        if(!isRegistered(name)) {
            CaseAction caseAction = new CaseAction(actionExecutor, addon, name, description);
            registeredActions.put(name, caseAction);
            String actionAddonName = addon.getName();
            boolean isDefault = actionAddonName.equalsIgnoreCase("DonateCase");
            CaseActionRegisteredEvent event = new CaseActionRegisteredEvent(name, caseAction, actionAddonName, isDefault);
            Bukkit.getPluginManager().callEvent(event);
        } else {
            addon.getLogger().warning("CaseAction with name " + name + " already registered!");
        }
    }

    /**
     * Register action
     * @param name Action name, like: "[command]"
     * @param actionExecutor Action executor
     */
    @Deprecated
    public void registerAction(String name, ActionExecutor actionExecutor) {
        registerAction(name, actionExecutor, null);
    }

    /**
     * Unregister action
     * @param name Action name
     */
    public void unregisterAction(String name) {
        if(isRegistered(name)) {
            registeredActions.remove(name);
            CaseActionUnregisteredEvent event = new CaseActionUnregisteredEvent(name);
            Bukkit.getServer().getPluginManager().callEvent(event);
        } else {
            addon.getLogger().warning("CaseAction with name " + name + " already unregistered!");
        }
    }

    /**
     * Unregister all actions
     */
    public void unregisterAction() {
        List<String> list = new ArrayList<>(getRegisteredActions().keySet());
        for (String s : list) {
            unregisterAction(s);
        }
    }

    /**
     * Check for action registration
     * @param name action name
     * @return boolean
     */
    public static boolean isRegistered(String name) {
        return getRegisteredActions().containsKey(name);
    }

    /**
     * Get all registered animations
     * @return map with registered animations
     */
    public static Map<String, CaseAction> getRegisteredActions() {
        return registeredActions;
    }

    /**
     * Get registered action
     * @param action CaseAction name
     * @return CaseAction class instance
     */
    @Nullable
    public static CaseAction getRegisteredAction(@NotNull String action) {
        if (isRegistered(action)) {
            return getRegisteredActions().get(action);
        }
        return null;
    }

    /**
     * Get registered action by string start
     * @param string String to be parsed
     * @return Case action name
     */
    public static @Nullable String getByStart(@NotNull final String string) {
        return registeredActions.keySet().stream().filter(string::startsWith).findFirst().orElse(null);
    }
}
