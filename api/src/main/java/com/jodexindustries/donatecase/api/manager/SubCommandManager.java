package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Interface for managing subcommands, providing methods to register, unregister, and retrieve
 * subcommands as well as handle tab completions.
 *
 */
public interface SubCommandManager {

    /**
     * Registers a subcommand to the manager.
     *
     * @param subCommand the SubCommand object to register
     * @return true if the registration was successful, false otherwise
     */
    boolean register(SubCommand subCommand);

    /**
     * Unregisters a subcommand from the manager by its name.
     *
     * @param commandName the name of the subcommand to unregister
     */
    void unregister(String commandName);

    default void unregister(Addon addon) {
        List<SubCommand> list = new ArrayList<>(get(addon));
        list.stream().map(SubCommand::getName).forEach(this::unregister);
    }

    /**
     * Unregisters all subcommands currently managed by this instance.
     */
    void unregister();

    @Nullable
    SubCommand get(String commandName);

    default List<SubCommand> get(Addon addon) {
        return getMap().values().stream().filter(subCommand ->
                subCommand.getAddon().equals(addon)).collect(Collectors.toList());
    }

    @NotNull
    Map<String, SubCommand> getMap();

    void registerDefault();
}
