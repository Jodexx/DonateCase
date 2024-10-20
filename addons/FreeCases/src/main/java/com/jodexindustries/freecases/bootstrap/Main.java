package com.jodexindustries.freecases.bootstrap;

import com.jodexindustries.donatecase.api.CaseManager;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;

public interface Main {
    Plugin getPlugin();
    CaseManager getCaseAPI();
    Logger getLogger();
    File getDataFolder();
    String getVersion();
    void saveResource(String resourcePath, boolean replace);
}
