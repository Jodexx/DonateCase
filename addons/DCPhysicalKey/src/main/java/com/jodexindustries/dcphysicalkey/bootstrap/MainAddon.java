package com.jodexindustries.dcphysicalkey.bootstrap;

import com.jodexindustries.donatecase.api.addon.external.ExternalAddon;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddonBukkit;
import org.bukkit.plugin.Plugin;

public class MainAddon extends InternalJavaAddonBukkit implements Main {
    private Bootstrap bootstrap;

    @Override
    public void onEnable() {
        bootstrap = new Bootstrap(this);
        bootstrap.load();
    }

    @Override
    public void onDisable() {
        bootstrap.unload();
    }

    @Override
    public Plugin getPlugin() {
        return ((ExternalAddon) getDonateCase()).getPlugin();
    }

}
