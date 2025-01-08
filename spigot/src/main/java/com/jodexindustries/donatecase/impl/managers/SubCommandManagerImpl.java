package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import com.jodexindustries.donatecase.command.impl.*;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

public class SubCommandManagerImpl implements SubCommandManager<CommandSender> {

    private static final List<? extends Class<? extends SubCommand<CommandSender>>> defaultCommands = Arrays.asList(
            AddonCommand.class,
            AddonsCommand.class,
            CasesCommand.class,
            CreateCommand.class,
            DeleteCommand.class,
            DelKeyCommand.class,
            GiveKeyCommand.class,
            HelpCommand.class,
            KeysCommand.class,
            OpenCaseCommand.class,
            RegistryCommand.class,
            ReloadCommand.class,
            SetKeyCommand.class
    );

    private static final Map<String, SubCommand<CommandSender>> registeredSubCommands = new HashMap<>();
    private final DCAPIBukkit api;
    private final Addon addon;

    public SubCommandManagerImpl(DCAPIBukkit api) {
        this.api = api;
        this.addon = api.getAddon();
    }

    @NotNull
    @Override
    public SubCommand.Builder<CommandSender> builder(@NotNull String name) {
        return new SubCommand.Builder<>(name, addon);
    }

    @Override
    public boolean registerSubCommand(SubCommand<CommandSender> subCommand) {
        String name = subCommand.getName();
        if (registeredSubCommands.get(name.toLowerCase()) == null) {
            registeredSubCommands.put(name.toLowerCase(), subCommand);
            return true;
        } else {
            addon.getLogger().warning("Sub command " + name + " already registered!");
        }
        return false;
    }

    @Override
    public void unregisterSubCommand(String commandName) {
        if(registeredSubCommands.get(commandName.toLowerCase()) != null) {
            registeredSubCommands.remove(commandName.toLowerCase());
        } else {
            addon.getLogger().warning("Sub command " + commandName + " already unregistered!");
        }
    }

    @Override
    public void unregisterSubCommands() {
        List<String> list = new ArrayList<>(registeredSubCommands.keySet());
        list.forEach(this::unregisterSubCommand);
    }

    @Override
    public @Nullable SubCommand<CommandSender> getRegisteredSubCommand(String commandName) {
        return registeredSubCommands.get(commandName);
    }

    @Override
    public @NotNull Map<String, SubCommand<CommandSender>> getRegisteredSubCommands() {
        return registeredSubCommands;
    }

    @Override
    public void registerDefaultSubCommands() {
        defaultCommands.forEach(commandClass -> {
            try {
                SubCommand<CommandSender> command = commandClass.getDeclaredConstructor(DCAPIBukkit.class).newInstance(api);
                registerSubCommand(command);
            } catch (Exception e) {
                addon.getLogger().log(Level.WARNING, "Failed to register command: " + commandClass.getSimpleName(), e);
            }
        });
    }

}
