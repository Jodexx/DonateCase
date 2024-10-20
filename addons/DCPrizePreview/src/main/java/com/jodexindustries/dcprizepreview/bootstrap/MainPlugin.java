package com.jodexindustries.dcprizepreview.bootstrap;

import org.bukkit.plugin.java.JavaPlugin;

public final class MainPlugin extends JavaPlugin {
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
