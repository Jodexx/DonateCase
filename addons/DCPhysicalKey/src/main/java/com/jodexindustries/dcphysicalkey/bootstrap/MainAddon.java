package com.jodexindustries.dcphysicalkey.bootstrap;

import com.jodexindustries.donatecase.api.CaseManager;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import org.bukkit.plugin.Plugin;

public class MainAddon extends InternalJavaAddon implements Main {
    private Bootstrap bootstrap;
    private CaseManager caseManager;

    @Override
    public void onEnable() {
        caseManager = getCaseAPI();
        bootstrap = new Bootstrap(this);
        bootstrap.load();
    }

    @Override
    public void onDisable() {
        bootstrap.unload();
    }

    @Override
    public Plugin getPlugin() {
        return getDonateCase();
    }

    @Override
    public CaseManager getCaseManager() {
        return caseManager;
    }
}
