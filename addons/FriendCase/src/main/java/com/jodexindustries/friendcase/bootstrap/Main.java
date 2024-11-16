package com.jodexindustries.friendcase.bootstrap;

import com.jodexindustries.donatecase.api.DCAPIBukkit;

import java.io.File;

public interface Main {
    DCAPIBukkit getDCAPI();
    File getDataFolder();
    void saveResource(String resourcePath, boolean replace);
}
