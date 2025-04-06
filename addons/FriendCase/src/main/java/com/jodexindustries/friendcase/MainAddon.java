package com.jodexindustries.friendcase;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.event.Subscriber;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;
import net.kyori.event.method.annotation.Subscribe;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class MainAddon extends InternalJavaAddon implements Subscriber {

    public final DCAPI api = DCAPI.getInstance();

    public Config config;

    @Override
    public void onLoad() {
        this.config = new Config(new File(getDataFolder(), "config.yml"), this);
    }

    @Override
    public void onEnable() {
        load(false);

        api.getEventBus().register(this);

        FriendSubCommand friendSubCommand = new FriendSubCommand(this);

        List<String> argsList = new ArrayList<>(config.getList("Api", "Args"));

        String[] args = argsList.toArray(new String[0]);

        SubCommand subCommand = SubCommand.builder()
                .name("gift")
                .addon(this)
                .tabCompleter(friendSubCommand)
                .executor(friendSubCommand)
                .args(args)
                .permission(SubCommandType.PLAYER.permission)
                .description(config.getString("Api", "Description"))
                .build();

        api.getSubCommandManager().register(subCommand);
    }

    @Override
    public void onDisable() {
        api.getEventBus().unregister(this);
    }

    public void load(boolean log) {
        try {
            config.load();
            if (log) getLogger().info("Config reloaded!");
        } catch (ConfigurateException e) {
            getLogger().log(Level.WARNING, "Error with loading configuration:", e);
        }
    }

    @Subscribe
    public void onReload(DonateCaseReloadEvent event) {
        if (event.type() == DonateCaseReloadEvent.Type.CONFIG) load(true);
    }
}
