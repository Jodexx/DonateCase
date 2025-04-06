package com.jodexindustries.dcprizepreview.config;

import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;
import com.jodexindustries.donatecase.common.config.ConfigImpl;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Config extends ConfigImpl {

    private static final TypeToken<Map<String, CasePreview>> MAP_TYPE_TOKEN = new TypeToken<Map<String, CasePreview>>() {
    };

    public Map<String, CasePreview> previewMap;

    public Config(File file, InternalJavaAddon addon) {
        super(file);
        if (!file().exists()) addon.saveResource("config.yml", false);
    }

    @Override
    public void load() throws ConfigurateException {
        node(loader().load());
        this.previewMap = node("cases").get(MAP_TYPE_TOKEN, new HashMap<>());
    }

}
