package com.jodexindustries.donatecase.common.config;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.converter.ConfigType;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.api.data.config.ConfigSerializer;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.common.config.converter.DefaultConfigType;
import com.jodexindustries.donatecase.common.serializer.CaseDataMaterialSerializer;
import com.jodexindustries.donatecase.common.serializer.CaseGuiSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

@Accessors(fluent = true, chain = false)
@Getter
@Setter
public class ConfigImpl implements Config {

    public static final TypeSerializerCollection.Builder SERIALIZER_COLLECTION = TypeSerializerCollection.builder()
            .register(CaseGui.class, new CaseGuiSerializer())
            .register(CaseGui.Item.class, new CaseGuiSerializer.Item())
            .register(CaseDataMaterial.class, new CaseDataMaterialSerializer())
            .register(CaseLocation.class, new CaseLocation());

    private final String path;
    private final File file;
    private final YamlConfigurationLoader loader;

    private int version;
    private ConfigType type;
    private ConfigurationNode node;
    private Object serialized;

    /**
     * Constructs a configuration instance without specifying a config type.
     * This will default to detecting the type during loading.
     *
     * @param file The file to load the configuration from.
     */
    public ConfigImpl(File file) {
        this(file, null);
    }

    /**
     * Constructs a configuration instance with a specified config type.
     * The config type is used for conversion and serialization.
     *
     * @param file The file to load the configuration from.
     * @param type The config type used for conversion, may be {@code null}.
     */
    public ConfigImpl(File file, ConfigType type) {
        this.path = file.getPath().replace("\\", "/");
        this.file = file;
        this.type = type;

        this.loader = YamlConfigurationLoader
                .builder()
                .nodeStyle(NodeStyle.BLOCK)
                .defaultOptions(opts -> opts.serializers(build -> build.registerAll(SERIALIZER_COLLECTION.build())))
                .file(file)
                .build();
    }

    private void setMeta() throws SerializationException {
        ConfigurationNode metaNode = node.node("config");
        String version = metaNode.node("version").getString();

        if (version != null) {
            this.version = parse(version);
            this.type = DefaultConfigType.getType(metaNode.node("type").getString());
        } else {
            this.version = parse(metaNode.getString());
            this.type = node.hasChild("case") ? DefaultConfigType.OLD_CASE : DefaultConfigType.UNKNOWN;
        }

        ConfigSerializer configSerializer = type.getConfigSerializer();
        if(configSerializer != null) {
            this.serialized = node(configSerializer.path()).get(configSerializer.serializer());
        }
    }

    private int parse(String string) {
        if (string == null) return 0;
        if (string.contains(".")) string = string.replace(".", "");
        return Integer.parseInt(string);
    }

    @Override
    public void type(ConfigType type) {
        this.type = type;
    }

    @Override
    public ConfigurationNode node() {
        return node;
    }

    @Override
    public @Nullable Object getSerialized() {
        return serialized;
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
        return path;
    }

}
