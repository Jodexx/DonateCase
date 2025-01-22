package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.action.ActionException;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Interface for managing executable actions within the Donate Case system.
 * Provides methods for registering, unregistering, retrieving, and executing actions.
 *
 */
public interface ActionManager {

    void register(CaseAction action) throws ActionException;

    /**
     * Unregisters an action by its name.
     *
     * @param name the unique name of the action to unregister
     */
    void unregister(@NotNull String name);

    default void unregister(Addon addon) {
        List<CaseAction> list = new ArrayList<>(get(addon));
        list.stream().map(CaseAction::getName).forEach(this::unregister);
    }

    /**
     * Unregisters all currently registered actions.
     */
    void unregister();

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
    CaseAction get(@NotNull String name);

    /**
     * Retrieves all registered actions by addon.
     * @param addon The addon instance
     * @return List of actions
     */
    default List<CaseAction> get(Addon addon) {
        return getMap().values().stream().filter(action ->
                action.getAddon().equals(addon)).collect(Collectors.toList());
    }

    /**
     * Retrieves all currently registered actions.
     *
     * @return a map of action names to their respective {@code CaseAction} instances
     */
    @NotNull
    Map<String, CaseAction> getMap();

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
    void execute(@NotNull DCPlayer player, @NotNull String action, int cooldown);

    /**
     * Executes a list of actions for a player.
     *
     * @param player  the player for whom the actions are executed
     * @param actions the list of action names to execute
     */
    void execute(@NotNull DCPlayer player, @NotNull List<String> actions);
}
