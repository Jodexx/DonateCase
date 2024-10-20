package com.jodexindustries.dcphysicalkey.bootstrap;

import com.jodexindustries.donatecase.api.CaseManager;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;

public interface Main {
    Plugin getPlugin();
    CaseManager getCaseManager();
    File getDataFolder();
    Logger getLogger();
    void saveResource(String resource, boolean replace);
}
