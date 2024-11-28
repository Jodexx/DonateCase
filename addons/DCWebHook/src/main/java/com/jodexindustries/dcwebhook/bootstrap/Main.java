package com.jodexindustries.dcwebhook.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;

import java.io.File;

public interface Main {
    File getDataFolder();
    void saveResource(String resource, boolean replace);
    DCAPIBukkit getDCAPI();
}
