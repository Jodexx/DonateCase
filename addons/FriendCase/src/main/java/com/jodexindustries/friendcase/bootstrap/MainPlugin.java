package com.jodexindustries.friendcase.bootstrap;

import com.jodexindustries.donatecase.api.CaseManager;
import com.jodexindustries.friendcase.utils.Tools;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainPlugin extends JavaPlugin implements Main {
    private Tools t;
    private CaseManager api;

    @Override
    public void onEnable() {
        api = new CaseManager(this);
        t = new Tools(this);
    }

    @Override
    public void onDisable() {
        t.unload();
    }

    @Override
    public CaseManager getCaseAPI() {
        return api;
    }
}
