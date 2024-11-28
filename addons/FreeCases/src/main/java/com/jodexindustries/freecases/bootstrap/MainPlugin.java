package com.jodexindustries.freecases.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.freecases.utils.Tools;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainPlugin extends JavaPlugin implements Main {
    private Tools t;
    private DCAPIBukkit api;

    @Override
    public void onEnable() {
        api = DCAPIBukkit.get(this);
        t = new Tools(this);
        t.load();
    }
    @Override
    public void onDisable() {
        t.unload();
    }

    @Override
    public DCAPIBukkit getDCAPI() {
        return api;
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

}
