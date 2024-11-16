package com.jodexindustries.dcwebhook.bootstrap;

import com.jodexindustries.dcwebhook.tools.Tools;
import com.jodexindustries.donatecase.api.addon.external.ExternalAddon;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddonBukkit;
import org.bukkit.plugin.Plugin;

public final class MainAddon extends InternalJavaAddonBukkit implements Main {
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
        return ((ExternalAddon) getDonateCase()).getPlugin();
    }

}
