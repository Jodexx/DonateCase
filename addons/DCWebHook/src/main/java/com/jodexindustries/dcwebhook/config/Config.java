package com.jodexindustries.dcwebhook.config;


import com.jodexindustries.dcwebhook.bootstrap.MainAddon;
import com.jodexindustries.donatecase.common.config.ConfigImpl;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.util.logging.Level;

public class Config extends ConfigImpl {

    private final MainAddon addon;

    @Getter
    private DiscordWebhook webhook;

    public Config(File file, MainAddon addon) {
        super(file);
        this.addon = addon;
        if(!file.exists()) addon.saveResource("config.yml", false);
    }

    @Override
    public void load() {
        try {
            node(loader().load());
            this.webhook = node().get(DiscordWebhook.class);
        } catch (ConfigurateException e) {
            addon.getLogger().log(Level.WARNING, "Error with loading configuration", e);
        }
    }

}