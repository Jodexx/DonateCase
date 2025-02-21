package com.jodexindustries.donatecase.common.config;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.common.config.converter.ConfigType;
import com.jodexindustries.donatecase.common.serializer.CaseDataMaterialSerializer;
import com.jodexindustries.donatecase.common.serializer.CaseGuiSerializer;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

public class ConfigImpl implements Config {

    public static final TypeSerializerCollection SERIALIZER_COLLECTION = TypeSerializerCollection.builder()
            .register(CaseGui.class, new CaseGuiSerializer())
            .register(CaseGui.Item.class, new CaseGuiSerializer.Item())
            .register(CaseDataMaterial.class, new CaseDataMaterialSerializer())
            .register(CaseLocation.class, new CaseLocation())
            .build();

    private final File file;
    private final YamlConfigurationLoader loader;

    private int version;
    private ConfigType type;
    private ConfigurationNode node;

    public ConfigImpl(File file) {
        this.file = file;
        this.loader = YamlConfigurationLoader
                .builder()
                .nodeStyle(NodeStyle.BLOCK)
                .defaultOptions(opts -> opts.serializers(build -> build.registerAll(SERIALIZER_COLLECTION)))
                .file(file)
                .build();
    }

    private void setMeta() {
        ConfigurationNode metaNode = node.node("config");
        if (metaNode.hasChild()) {
            this.version = parse(metaNode.node("version").getString());
            this.type = ConfigType.getType(metaNode.node("type").getString());
        } else {
            int temp = metaNode.getInt();
            this.version = temp != 0 ? temp : parse(metaNode.getString());
            this.type = node.hasChild("case") ? ConfigType.OLD_CASE : ConfigType.UNKNOWN;
        }
    }

    private int parse(String string) {
        if (string == null) return 0;
        if (string.contains(".")) string = string.replace(".", "");
        return Integer.parseInt(string);
    }

    public ConfigType type() {
        return type;
    }

    public int version() {
        return version;
    }

    @Override
    public ConfigurationNode node() {
        return node;
    }

    @Override
    public File file() {
        return file;
    }

    @Override
    public void load() throws ConfigurateException {
        node = loader.load();
        setMeta();
    }

    @Override
    public void save() throws ConfigurateException {
        loader.save(node);
    }

    @Override
    public String toString() {
        return file.getPath();
    }

}
