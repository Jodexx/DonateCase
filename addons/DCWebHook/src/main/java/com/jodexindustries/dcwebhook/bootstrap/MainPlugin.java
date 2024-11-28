package com.jodexindustries.dcwebhook.bootstrap;

import com.jodexindustries.dcwebhook.tools.Tools;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainPlugin extends JavaPlugin implements Main {
    private DCAPIBukkit api;
    private Tools t;

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

}
