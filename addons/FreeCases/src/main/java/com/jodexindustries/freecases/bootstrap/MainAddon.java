package com.jodexindustries.freecases.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.freecases.utils.Tools;

public final class MainAddon extends InternalJavaAddon implements Main {
    private Tools t;
    private DCAPIBukkit api;

    @Override
    public void onEnable() {
        api = DCAPIBukkit.get(this);
        t = new Tools(this);
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
