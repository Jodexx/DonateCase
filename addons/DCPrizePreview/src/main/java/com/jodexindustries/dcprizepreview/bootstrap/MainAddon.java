package com.jodexindustries.dcprizepreview.bootstrap;

import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;

public class MainAddon extends InternalJavaAddon {
    private Loader loader;

    @Override
    public void onLoad() {
        loader = new Loader(this);
    }

    @Override
    public void onEnable() {
        loader.enable();
    }

    @Override
    public void onDisable() {
        loader.disable();
    }
}
