package com.jodexindustries.donatecase.common.io;

import com.jodexindustries.donatecase.api.io.FilesLookup;
import com.jodexindustries.donatecase.api.io.FilesLookupProvider;

import java.io.File;

// from https://github.com/BG-Software-LLC/SuperiorSkyblock2
public class DefaultFilesLookupProvider implements FilesLookupProvider {

    private static final DefaultFilesLookupProvider INSTANCE = new DefaultFilesLookupProvider();

    public static DefaultFilesLookupProvider getInstance() {
        return INSTANCE;
    }

    private DefaultFilesLookupProvider() {

    }

    @Override
    public FilesLookup createFilesLookup(File folder) {
        return new DefaultFilesLookup(folder);
    }

    private static class DefaultFilesLookup implements FilesLookup {

        private final File folder;

        DefaultFilesLookup(File folder) {
            this.folder = folder;
        }

        @Override
        public File getFile(String name) {
            return new File(this.folder, name);
        }

        @Override
        public void close() {
            // Do nothing
        }
    }

}