package com.jodexindustries.dcblockanimations.bootstrap;

import com.jodexindustries.dcblockanimations.utils.Tools;
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
