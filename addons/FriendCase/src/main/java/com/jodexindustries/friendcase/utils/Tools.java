package com.jodexindustries.friendcase.utils;

import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.friendcase.FriendSubCommand;
import com.jodexindustries.friendcase.bootstrap.Main;
import com.jodexindustries.friendcase.config.Config;

import java.util.ArrayList;
import java.util.List;

import static com.jodexindustries.donatecase.tools.Tools.rc;

public class Tools {
    private final Main main;
    private final Config config;

    public Tools(Main main) {
        this.main = main;
        this.config = new Config(main);
    }

    public void load() {
        FriendSubCommand friendSubCommand = new FriendSubCommand(this);

        List<String> argsList = new ArrayList<>();

        for (String args : getConfig().getConfig().getStringList("Api.Args")) {
            argsList.add(rc(args));
        }

        String[] args = argsList.toArray(new String[0]);

        SubCommand subCommand = main.getCaseAPI().getSubCommandManager().builder("gift")
                .tabCompleter(friendSubCommand)
                .executor(friendSubCommand)
                .args(args)
                .permission(SubCommandType.PLAYER.permission)
                .description(rc(getConfig().getConfig().getString("Api.Description")))
                .build();

        main.getCaseAPI().getSubCommandManager().registerSubCommand(subCommand);
    }

    public void unload() {
        main.getCaseAPI().getSubCommandManager().unregisterSubCommand("gift");
    }

    public Config getConfig() {
        return config;
    }
}
