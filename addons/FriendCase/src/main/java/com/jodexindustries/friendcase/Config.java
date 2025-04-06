package com.jodexindustries.friendcase;


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

    public String getString(Object... path) {
        return node(path).getString();
    }

    public List<String> getList(Object... path) {
        try {
            return node(path).getList(String.class);
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

}