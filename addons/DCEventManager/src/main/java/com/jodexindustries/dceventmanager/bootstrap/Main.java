package com.jodexindustries.dceventmanager.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;

public interface Main {
    Plugin getPlugin();
    DCAPIBukkit getDCAPI();
    Logger getLogger();
    File getDataFolder();
    void saveResource(String resource, boolean replace);
}
