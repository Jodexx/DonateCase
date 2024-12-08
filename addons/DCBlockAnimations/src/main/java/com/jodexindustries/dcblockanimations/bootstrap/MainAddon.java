package com.jodexindustries.dcblockanimations.bootstrap;

import com.jodexindustries.dcblockanimations.utils.Tools;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;

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
