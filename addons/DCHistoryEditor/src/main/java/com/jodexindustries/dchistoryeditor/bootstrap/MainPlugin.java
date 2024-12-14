package com.jodexindustries.dchistoryeditor.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainPlugin extends JavaPlugin {

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
