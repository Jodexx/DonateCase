package com.jodexindustries.dcblockanimations;

import com.jodexindustries.donatecase.common.config.ConfigImpl;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config extends ConfigImpl {

    public Config(File file, MainAddon addon) {
        super(file);
        if(!file.exists()) addon.saveResource("config.yml", false);
    }

    public List<String> getEnabledTypes() {
        try {
            return node("enabled-types").getList(String.class);
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

}
