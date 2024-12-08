package com.jodexindustries.dcphysicalkey.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;

public class MainAddon extends InternalJavaAddon implements Main {
    private Bootstrap bootstrap;
    private DCAPIBukkit api;

    @Override
    public void onEnable() {
        api = DCAPIBukkit.get(this);
        bootstrap = new Bootstrap(this);
        bootstrap.load();
    }

    @Override
    public void onDisable() {
        bootstrap.unload();
    }

    @Override
    public DCAPIBukkit getDCAPI() {
        return api;
    }
}
