package com.jodexindustries.dcwebhook.tools;

import com.jodexindustries.dcwebhook.bootstrap.Main;
import com.jodexindustries.dcwebhook.config.Config;
import com.jodexindustries.dcwebhook.events.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class Tools {
    private final Main main;
    private final Config config;

    private final EventListener eventListener;

    public Tools(Main main) {
        this.main = main;
        this.config = new Config(main);

        this.eventListener = new EventListener(this);
    }

    public void load() {
        Bukkit.getServer().getPluginManager().registerEvents(eventListener, main.getDCAPI().getDonateCase());
    }

    public void unload() {
        HandlerList.unregisterAll(eventListener);
    }

    public Main getMain() {
        return main;
    }

    public Config getConfig() {
        return config;
    }
}
