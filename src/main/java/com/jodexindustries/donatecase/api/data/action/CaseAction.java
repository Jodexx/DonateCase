package com.jodexindustries.donatecase.api.data.action;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class CaseAction implements ActionExecutor {
    private final ActionExecutor executor;
    private final Addon addon;
    private final String name;
    private final String description;

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

    public Addon getAddon() {
        return addon;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " (" + description + ")";
    }
}
