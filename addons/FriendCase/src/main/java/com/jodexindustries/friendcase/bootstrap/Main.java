package com.jodexindustries.friendcase.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;

import java.io.File;
import java.util.logging.Logger;

public interface Main {
    DCAPIBukkit getDCAPI();
    File getDataFolder();
    void saveResource(String resourcePath, boolean replace);
    Logger getLogger();
}
