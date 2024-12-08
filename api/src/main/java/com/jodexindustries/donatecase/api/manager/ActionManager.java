package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Interface for managing executable actions within the Donate Case system.
 * Provides methods for registering, unregistering, retrieving, and executing actions.
 *
 * @param <P> The type representing a player entity
 */
public interface ActionManager<P> {

    /**
     * Registers an action with the specified name, executor, and description.
     *
     * @param name           the unique name of the action (e.g., "[command]")
     * @param actionExecutor the executor responsible for the logic of the action
     * @param description    a textual description of the action's functionality
     * @return True if the registration was successful, false otherwise.
     */
    boolean registerAction(@NotNull String name, @NotNull ActionExecutor<P> actionExecutor, @NotNull String description);

    /**
     * Unregisters an action by its name.
     *
     * @param name the unique name of the action to unregister
     */
    void unregisterAction(@NotNull String name);

    /**
     * Unregisters all currently registered actions.
     */
    void unregisterActions();

    /**
     * Checks whether an action with the specified name is registered.
     *
     * @param name the name of the action to check
     * @return {@code true} if the action is registered, {@code false} otherwise
     */
    boolean isRegistered(@NotNull String name);

    /**
     * Retrieves a registered action by its name.
     *
     * @param name the name of the action to retrieve
     * @return the {@code CaseAction} instance if found, or {@code null} if not found
     */
    @Nullable
    CaseAction<P> getRegisteredAction(@NotNull String name);

    /**
     * Retrieves all currently registered actions.
     *
     * @return a map of action names to their respective {@code CaseAction} instances
     */
    @NotNull
    Map<String, CaseAction<P>> getRegisteredActions();

    /**
     * Retrieves the name of a registered action that matches the beginning of a given string.
     *
     * @param prefix the prefix string to match against action names
     * @return the name of the matching action, or {@code null} if no match is found
     */
    @Nullable
    String getByStart(@NotNull String prefix);

    /**
     * Executes a specific action for a player, applying a cooldown.
     *
     * @param player   the player for whom the action is executed
     * @param action   the name of the action to execute
     * @param cooldown the cooldown duration in seconds
     */
    void executeAction(@NotNull P player, @NotNull String action, int cooldown);

    /**
     * Executes a list of actions for a player.
     *
     * @param player  the player for whom the actions are executed
     * @param actions the list of action names to execute
     */
    void executeActions(@NotNull P player, @NotNull List<String> actions);
}
