package com.jodexindustries.donatecase.common.io;

import com.jodexindustries.donatecase.api.io.FilesLookup;
import com.jodexindustries.donatecase.api.io.FilesLookupProvider;

import java.io.File;

// from https://github.com/BG-Software-LLC/SuperiorSkyblock2
public class FilesLookupFactory {

    private static final FilesLookupFactory INSTANCE = new FilesLookupFactory();

    public static FilesLookupFactory getInstance() {
        return INSTANCE;
    }

    private FilesLookupProvider filesLookupProvider = findSuitableFilesLookupProvider();

    private FilesLookupFactory() {

    }

    public void setProvider(FilesLookupProvider filesLookupProvider) {
        this.filesLookupProvider = filesLookupProvider;
    }

    public FilesLookup lookupFolder(File folder) {
        try {
            return this.filesLookupProvider.createFilesLookup(folder);
        } catch (IllegalStateException error) {
            if (this.filesLookupProvider == DefaultFilesLookupProvider.getInstance())
                throw error;

            this.filesLookupProvider = DefaultFilesLookupProvider.getInstance();
            return lookupFolder(folder);
        }
    }

    private static FilesLookupProvider findSuitableFilesLookupProvider() {
        try {
            // PaperMC PluginRemapper
            Class.forName("io.papermc.paper.pluginremap.PluginRemapper");
            return (FilesLookupProvider) Class.forName(
                            "com.jodexindustries.donatecase.papermc.remapper.PluginRemapperFilesLookupProvider")
                    .newInstance();
        } catch (Throwable ignored) {
        }

        return DefaultFilesLookupProvider.getInstance();
    }

}