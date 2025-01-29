package com.jodexindustries.donatecase.spigot;

import org.bukkit.plugin.java.JavaPlugin;

public class BukkitDonateCase extends JavaPlugin {

    private BukkitBackend backend;

    @Override
    public void onLoad() {
        backend = new BukkitBackend(this);
    }

    @Override
    public void onEnable() {
        backend.load();
    }


    @Override
    public void onDisable() {
        backend.unload();
    }

}