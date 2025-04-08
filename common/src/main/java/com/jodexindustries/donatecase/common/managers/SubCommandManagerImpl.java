package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandException;
import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import com.jodexindustries.donatecase.api.platform.Platform;
import com.jodexindustries.donatecase.common.command.DefaultCommand;
import com.jodexindustries.donatecase.common.command.sub.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class SubCommandManagerImpl implements SubCommandManager {

    private static final List<? extends Class<? extends DefaultCommand>> defaultCommands = Arrays.asList(
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

    private static final Map<String, SubCommand> registeredSubCommands = new ConcurrentHashMap<>();

    private final DCAPI api;
    private final Platform platform;

    public SubCommandManagerImpl(DCAPI api) {
        this.api = api;
        this.platform = api.getPlatform();

        registerDefault();
    }

    @Override
    public void register(SubCommand subCommand) {
        String name = subCommand.name().toLowerCase();
        if(isRegistered(name)) throw new SubCommandException("Sub command with name " + name + " already registered!");

        registeredSubCommands.put(name, subCommand);
    }

    @Override
    public void unregister(String name) {
        if(!isRegistered(name)) throw new SubCommandException("Sub command with name " + name + " already unregistered!");

        registeredSubCommands.remove(name.toLowerCase());
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

    private void registerDefault() {
        defaultCommands.forEach(commandClass -> {
            try {
                DefaultCommand command = commandClass.getDeclaredConstructor(DCAPI.class).newInstance(api);
                register(command.build());
            } catch (Exception e) {
                platform.getLogger().log(Level.WARNING, "Failed to register command: " + commandClass.getSimpleName(), e);
            }
        });
    }

}
