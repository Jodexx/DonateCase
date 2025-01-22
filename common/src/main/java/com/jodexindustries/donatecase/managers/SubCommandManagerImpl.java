package com.jodexindustries.donatecase.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import com.jodexindustries.donatecase.api.platform.Platform;
import com.jodexindustries.donatecase.command.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

public class SubCommandManagerImpl implements SubCommandManager {

    private static final List<? extends Class<? extends SubCommand.SubCommandBuilder>> defaultCommands = Arrays.asList(
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

    private static final Map<String, SubCommand> registeredSubCommands = new HashMap<>();

    private final DCAPI api;
    private final Platform platform;

    public SubCommandManagerImpl(DCAPI api) {
        this.api = api;
        this.platform = api.getPlatform();
    }

    @Override
    public boolean register(SubCommand subCommand) {
        String name = subCommand.getName();
        if (registeredSubCommands.get(name.toLowerCase()) == null) {
            registeredSubCommands.put(name.toLowerCase(), subCommand);
            return true;
        } else {
            platform.getLogger().warning("Sub command " + name + " already registered!");
        }
        return false;
    }

    @Override
    public void unregister(String commandName) {
        if (registeredSubCommands.get(commandName.toLowerCase()) != null) {
            registeredSubCommands.remove(commandName.toLowerCase());
        } else {
            platform.getLogger().warning("Sub command " + commandName + " already unregistered!");
        }
    }

    @Override
    public void unregister() {
        List<String> list = new ArrayList<>(registeredSubCommands.keySet());
        list.forEach(this::unregister);
    }

    @Override
    public @Nullable SubCommand get(String commandName) {
        return registeredSubCommands.get(commandName);
    }

    @Override
    public @NotNull Map<String, SubCommand> getMap() {
        return registeredSubCommands;
    }

    @Override
    public void registerDefault() {
        defaultCommands.forEach(commandClass -> {
            try {
                SubCommand.SubCommandBuilder command = commandClass.getDeclaredConstructor(DCAPI.class).newInstance(api);
                register(command.build());
            } catch (Exception e) {
                platform.getLogger().log(Level.WARNING, "Failed to register command: " + commandClass.getSimpleName(), e);
            }
        });
    }

}
