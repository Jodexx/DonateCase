package com.jodexindustries.dcphysicalkey.bootstrap;

import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddonBukkit;

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

}
