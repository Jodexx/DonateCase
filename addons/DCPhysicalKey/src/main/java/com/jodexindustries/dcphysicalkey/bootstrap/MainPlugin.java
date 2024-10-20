package com.jodexindustries.dcphysicalkey.bootstrap;

import com.jodexindustries.donatecase.api.CaseManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class MainPlugin extends JavaPlugin implements Main {
    private Bootstrap bootstrap;
    private CaseManager caseManager;

    @Override
    public void onEnable() {
        caseManager = new CaseManager(this);
        bootstrap = new Bootstrap(this);
        bootstrap.load();
    }

    @Override
    public void onDisable() {
        bootstrap.unload();
    }

    @Override
    public Plugin getPlugin() {
        return this;
    }

    @Override
    public CaseManager getCaseManager() {
        return caseManager;
    }
}
