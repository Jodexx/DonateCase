package com.jodexindustries.dcwebhook.bootstrap;

import com.jodexindustries.dcwebhook.tools.Tools;
import com.jodexindustries.donatecase.api.CaseManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainPlugin extends JavaPlugin implements Main {
    private CaseManager api;
    private Tools t;

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
}
