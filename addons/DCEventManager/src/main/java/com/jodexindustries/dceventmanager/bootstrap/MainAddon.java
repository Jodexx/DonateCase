package com.jodexindustries.dceventmanager.bootstrap;

import com.jodexindustries.dceventmanager.utils.Tools;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import org.bukkit.plugin.Plugin;

public final class MainAddon extends InternalJavaAddon implements Main {
    private Tools t;

    @Override
    public void onEnable() {
        t = new Tools(this);
        t.load();
    }

    @Override
    public void onDisable() {
        t.unload();
    }

    @Override
    public Plugin getPlugin() {
        return getDonateCase();
    }
}
