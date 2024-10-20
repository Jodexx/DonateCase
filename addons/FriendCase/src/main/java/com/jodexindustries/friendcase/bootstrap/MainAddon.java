package com.jodexindustries.friendcase.bootstrap;

import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.friendcase.utils.Tools;

public final class MainAddon extends InternalJavaAddon implements Main {
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
