package com.jodexindustries.freecases.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;

import java.io.File;
import java.util.logging.Logger;

public interface Main {
    DCAPIBukkit getDCAPI();
    Logger getLogger();
    File getDataFolder();
    String getVersion();
    void saveResource(String resourcePath, boolean replace);
}
