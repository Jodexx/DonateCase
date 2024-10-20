package com.jodexindustries.friendcase.bootstrap;

import com.jodexindustries.donatecase.api.CaseManager;

import java.io.File;

public interface Main {
    CaseManager getCaseAPI();
    File getDataFolder();
    void saveResource(String resourcePath, boolean replace);
}
