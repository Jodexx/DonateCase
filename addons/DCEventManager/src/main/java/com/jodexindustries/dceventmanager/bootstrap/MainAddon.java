package com.jodexindustries.dceventmanager.bootstrap;

import com.jodexindustries.dceventmanager.utils.Tools;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddonBukkit;

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

}
