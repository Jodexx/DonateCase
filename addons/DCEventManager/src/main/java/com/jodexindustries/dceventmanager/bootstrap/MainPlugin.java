package com.jodexindustries.dceventmanager.bootstrap;

import com.jodexindustries.dceventmanager.utils.Tools;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainPlugin extends JavaPlugin implements Main {
    private Tools t;
    private DCAPIBukkit api;


    @Override
    public void onEnable() {
        t = new Tools(this);
        api = DCAPIBukkit.get(this);
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
    public DCAPIBukkit getDCAPI() {
        return api;
    }
}
