package com.jodexindustries.donatecase.api.io;

import java.io.File;

// from https://github.com/BG-Software-LLC/SuperiorSkyblock2
public interface FilesLookup extends AutoCloseable {

    File getFile(String name);

    @Override
    void close();

}