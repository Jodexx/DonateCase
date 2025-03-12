package com.jodexindustries.donatecase.common.command;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.subcommand.*;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultCommand implements SubCommandExecutor, SubCommandTabCompleter {

    private final DCAPI api;
    private final String name;
    private final SubCommandType type;

    public DefaultCommand(DCAPI api, String name, SubCommandType type) {
        this.api = api;
        this.name = name;
        this.type = type;
    }

    public final SubCommand build() {
        return SubCommand.builder()
                .addon(api.getPlatform())
                .name(name)
                .permission(type.permission)
                .tabCompleter(this)
                .executor(this)
                .build();
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, @NotNull String[] args) throws SubCommandException {
        return new ArrayList<>();
    }
}
