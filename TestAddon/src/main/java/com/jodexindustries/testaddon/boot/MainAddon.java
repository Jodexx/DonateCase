package com.jodexindustries.testaddon.boot;

import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import org.bukkit.event.Listener;

public class MainAddon extends InternalJavaAddon implements Listener {
    private Loader loader;
    @Override
    public void onEnable() {
        loader = new Loader(this);
        loader.load();
    }

    @Override
    public void onDisable() {
        loader.unload();
    }
}