package com.jodexindustries.dcphysicalkey.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;

import java.io.File;
import java.util.logging.Logger;

public interface Main {
    DCAPIBukkit getDCAPI();
    File getDataFolder();
    Logger getLogger();
    void saveResource(String resource, boolean replace);
}
