package com.jodexindustries.dceventmanager.bootstrap;

import com.jodexindustries.dceventmanager.utils.Tools;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;

public final class MainAddon extends InternalJavaAddon implements Main {
    private Tools t;
    private DCAPIBukkit api;

    @Override
    public void onEnable() {
        t = new Tools(this);
        api = DCAPIBukkit.get(this);
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
