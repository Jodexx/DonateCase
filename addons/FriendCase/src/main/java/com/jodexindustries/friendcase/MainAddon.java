package com.jodexindustries.friendcase;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;

import java.util.ArrayList;
import java.util.List;

public final class MainAddon extends InternalJavaAddon {

    public final DCAPI api = DCAPI.getInstance();

    public Config config;

    @Override
    public void onLoad() {
        this.config = new Config(this);
    }

    @Override
    public void onEnable() {
        api.getEventBus().register(DonateCaseReloadEvent.class, event -> {
            if(event.getType() == DonateCaseReloadEvent.Type.CONFIG) config.load();
            getLogger().info("Config reloaded");
        });

        FriendSubCommand friendSubCommand = new FriendSubCommand(this);

        List<String> argsList = new ArrayList<>(config.getList("Api", "Args"));

        String[] args = argsList.toArray(new String[0]);

        SubCommand subCommand = SubCommand.builder()
                .name("gift")
                .tabCompleter(friendSubCommand)
                .executor(friendSubCommand)
                .args(args)
                .permission(SubCommandType.PLAYER.permission)
                .description(config.getString("Api", "Description"))
                .build();

        api.getSubCommandManager().register(subCommand);

    }

}
