package com.jodexindustries.freecases.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;

public interface Main {
    Plugin getPlugin();
    DCAPIBukkit getDCAPI();
    Logger getLogger();
    File getDataFolder();
    String getVersion();
    void saveResource(String resourcePath, boolean replace);
}
