package com.jodexindustries.donatecase.api.data.action;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Class for custom actions storage
 */
public class CaseAction implements ActionExecutor {
    private final ActionExecutor executor;
    private final Addon addon;
    private final String name;
    private final String description;

    /**
     * Default constructor
     * @param executor Action executor
     * @param addon Action addon
     * @param name Action name
     * @param description Action description
     */
    public CaseAction(ActionExecutor executor, Addon addon, String name, String description) {
        this.executor = executor;
        this.addon = addon;
        this.name = name;
        this.description = description;

    }

    @Override
    public void execute(@NotNull OfflinePlayer player, @NotNull String context, int cooldown) {
        executor.execute(player, context, cooldown);
    }

    /**
     * Gets addon which registered this action
     * @return addon action
     */
    public Addon getAddon() {
        return addon;
    }

    /**
     * Gets action description
     * @return action description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets action name
     * @return action name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " (" + description + ")";
    }
}
