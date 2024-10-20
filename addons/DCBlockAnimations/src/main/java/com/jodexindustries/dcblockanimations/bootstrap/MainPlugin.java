package com.jodexindustries.dcblockanimations.bootstrap;

import com.jodexindustries.dcblockanimations.utils.Tools;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainPlugin extends JavaPlugin implements Main {
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

    @Override
    public Plugin getPlugin() {
        return this;
    }

}
