package com.jodexindustries.dceventmanager.bootstrap;

import com.jodexindustries.dceventmanager.utils.Tools;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;

public final class MainAddon extends InternalJavaAddon {

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
