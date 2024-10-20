package com.jodexindustries.freecases.bootstrap;

import com.jodexindustries.donatecase.api.CaseManager;
import com.jodexindustries.freecases.utils.Tools;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainPlugin extends JavaPlugin implements Main {
    private Tools t;
    private CaseManager api;

    @Override
    public void onEnable() {
        api = new CaseManager(this);
        t = new Tools(this);
        t.load();
    }
    @Override
    public void onDisable() {
        t.unload();
    }


    @Override
    public Plugin getPlugin() {
        return this;
    }

    @Override
    public CaseManager getCaseAPI() {
        return api;
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

}
