package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import com.jodexindustries.donatecase.api.events.CaseActionRegisteredEvent;
import com.jodexindustries.donatecase.api.events.CaseActionUnregisteredEvent;
import com.jodexindustries.donatecase.api.manager.ActionManager;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jodexindustries.donatecase.DonateCase.instance;

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
public class ActionManagerImpl implements ActionManager<Player> {
    /**
     * Map of all registered actions
     */
    public static final Map<String, CaseAction<Player>> registeredActions = new HashMap<>();
    private final Addon addon;

    /**
     * Default constructor
     *
     * @param addon An addon that will manage actions
     */
    public ActionManagerImpl(Addon addon) {
        this.addon = addon;
    }

    /**
     * Register action
     *
     * @param name           Action name, like: "[command]"
     * @param actionExecutor Action executor
     * @param description    Action description
     */
    @Override
    public void registerAction(String name, ActionExecutor<Player> actionExecutor, String description) {
        if (!isRegistered(name)) {
            CaseAction<Player> caseAction = new CaseAction<>(actionExecutor, addon, name, description);
            registeredActions.put(name, caseAction);
            CaseActionRegisteredEvent event = new CaseActionRegisteredEvent(caseAction);
            Bukkit.getPluginManager().callEvent(event);
        } else {
            addon.getLogger().warning("CaseAction with name " + name + " already registered!");
        }
    }

    /**
     * Unregister action
     *
     * @param name Action name
     */
    @Override
    public void unregisterAction(String name) {
        if (isRegistered(name)) {
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
    @Override
    public void unregisterActions() {
        List<String> list = new ArrayList<>(registeredActions.keySet());
        list.forEach(this::unregisterAction);
    }

    /**
     * Check for action registration
     *
     * @param name action name
     * @return boolean
     */
    @Override
    public boolean isRegistered(String name) {
        return registeredActions.containsKey(name);
    }

    /**
     * Get registered action
     *
     * @param action CaseAction name
     * @return CaseAction class instance
     */
    @Nullable
    @Override
    public CaseAction<Player> getRegisteredAction(@NotNull String action) {
        return registeredActions.get(action);
    }

    /**
     * Get registered action by string start
     *
     * @param string String to be parsed
     * @return Case action name
     */
    @Override
    public @Nullable String getByStart(@NotNull final String string) {
        return registeredActions.keySet().stream().filter(string::startsWith).findFirst().orElse(null);
    }

    @Override
    public void executeAction(Player player, String action, int cooldown) {
        String temp = instance.api.getActionManager().getByStart(action);
        if(temp == null) return;

        String context = action.replace(temp, "").trim();

        ActionExecutor<Player> actionExecutor = instance.api.getActionManager().getRegisteredAction(temp);
        if(actionExecutor == null) return;

        actionExecutor.execute(player.getPlayer(), context, cooldown);
    }

    @Override
    public void executeActions(Player player, List<String> actions) {
        for (String action : actions) {

            action = Tools.rc(Case.getInstance().papi.setPlaceholders(player, action));
            int cooldown = Tools.extractCooldown(action);
            action = action.replaceFirst("\\[cooldown:(.*?)]", "");

            executeAction(player, action, cooldown);
        }
    }
}