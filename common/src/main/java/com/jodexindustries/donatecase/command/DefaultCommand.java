package com.jodexindustries.donatecase.command;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;

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
}
