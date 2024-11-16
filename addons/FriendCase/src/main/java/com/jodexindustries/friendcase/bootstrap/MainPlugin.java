package com.jodexindustries.friendcase.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.friendcase.utils.Tools;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainPlugin extends JavaPlugin implements Main {
    private Tools t;
    private DCAPIBukkit api;

    @Override
    public void onEnable() {
        api = DCAPIBukkit.get(this);
        t = new Tools(this);
    }

    @Override
    public void onDisable() {
        t.unload();
    }

    @Override
    public DCAPIBukkit getDCAPI() {
        return api;
    }
}
