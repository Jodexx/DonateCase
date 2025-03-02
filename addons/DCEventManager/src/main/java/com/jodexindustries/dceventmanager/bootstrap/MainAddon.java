package com.jodexindustries.dceventmanager.bootstrap;

import com.jodexindustries.dceventmanager.utils.Tools;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;

public final class MainAddon extends InternalJavaAddon {

    public final DCAPI api = DCAPI.getInstance();

    private Tools tools;

    @Override
    public void onLoad() {
        tools = new Tools(this);
    }

    @Override
    public void onEnable() {
        tools.load();
    }

    @Override
    public void onDisable() {
        tools.unload();
    }

}
