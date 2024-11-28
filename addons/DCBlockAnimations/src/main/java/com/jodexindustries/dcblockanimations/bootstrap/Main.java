package com.jodexindustries.dcblockanimations.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;

import java.io.File;
import java.util.logging.Logger;

public interface Main {
    DCAPIBukkit getDCAPI();
    Logger getLogger();
    File getDataFolder();
    void saveResource(String resource, boolean replace);
}
