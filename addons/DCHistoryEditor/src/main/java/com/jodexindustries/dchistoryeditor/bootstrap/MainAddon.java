package com.jodexindustries.dchistoryeditor.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;

public final class MainAddon extends InternalJavaAddon {

    private Bootstrap bootstrap;

    @Override
    public void onEnable() {
        DCAPIBukkit api = DCAPIBukkit.get(this);
        this.bootstrap = new Bootstrap(api);
        bootstrap.load();
    }

    @Override
    public void onDisable() {
        bootstrap.unload();
    }

}
