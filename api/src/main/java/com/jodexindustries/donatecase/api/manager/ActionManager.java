package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Interface for managing executable actions, allowing for registration, retrieval, and unregistration of actions.
 *
 * @param <Player> The type representing a player entity
 */
public interface ActionManager<Player> {

    /**
     * Registers an action with a specified name, executor, and description.
     *
     * @param name           the name of the action, e.g., "[command]"
     * @param actionExecutor the executor responsible for executing the action
     * @param description    a description of the action's functionality
     */
    void registerAction(String name, ActionExecutor<Player> actionExecutor, String description);

    /**
     * Unregisters a specific action by name.
     *
     * @param name the name of the action to unregister
     */
    void unregisterAction(String name);

    /**
     * Unregisters all registered actions.
     */
    void unregisterActions();

    /**
     * Checks if a specific action is registered.
     *
     * @param name the name of the action to check
     * @return true if the action is registered, false otherwise
     */
    boolean isRegistered(String name);

    /**
     * Retrieves a registered action by its name.
     *
     * @param action the name of the action to retrieve
     * @return the {@code CaseAction} instance if found, or null otherwise
     */
    @Nullable
    CaseAction<Player> getRegisteredAction(@NotNull String action);

    @NotNull
    Map<String, CaseAction<Player>> getRegisteredActions();

    /**
     * Retrieves a registered action by matching the beginning of a string.
     *
     * @param string the string used to match the start of an action name
     * @return the name of the matched action if found, or null otherwise
     */
    @Nullable
    String getByStart(@NotNull final String string);

    /**
     * Execute action with specific cooldown
     * @param player Player, who opened case (maybe another reason)
     * @param action Action to be executed
     * @param cooldown Cooldown in seconds
     */
    void executeAction(Player player, String action, int cooldown);

    /**
     * Execute actions
     * @param player Player, who opened case (maybe another reason)
     * @param actions List of actions
     */
    void executeActions(Player player, List<String> actions);
}
