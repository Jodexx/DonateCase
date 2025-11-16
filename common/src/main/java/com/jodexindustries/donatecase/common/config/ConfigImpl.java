package com.jodexindustries.donatecase.common.config;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.converter.ConfigType;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItems;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMaterial;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseSettings;
import com.jodexindustries.donatecase.api.data.config.ConfigSerializer;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMenu;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.common.config.converter.DefaultConfigType;
import com.jodexindustries.donatecase.common.config.serializer.CaseDataMaterialSerializer;
import com.jodexindustries.donatecase.common.config.serializer.CaseGuiSerializer;
import com.jodexindustries.donatecase.common.config.serializer.casedefinition.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
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
            .register(CaseSettings.class, new CaseSettingsSerializer())
            .register(CaseSettings.Hologram.class, new CaseSettingsSerializer.Hologram())
            .register(CaseSettings.LevelGroups.class, new CaseSettingsSerializer.LevelGroups())

            .register(CaseItems.class, new CaseItemsSerializer())

            .register(CaseItem.class, new CaseItemSerializer())
            .register(CaseItem.RandomAction.class, new CaseItemSerializer.RandomAction())

            .register(CaseMenu.class, new CaseMenuSerializer())
            .register(CaseMenu.Item.class, new CaseMenuSerializer.Item())

            .register(CaseMaterial.class, new CaseMaterialSerializer())
            .register(CaseLocation.class, new CaseLocation())

            // deprecated
            .register(CaseGui.class, new CaseGuiSerializer())
            .register(CaseGui.Item.class, new CaseGuiSerializer.Item())
            .register(CaseDataMaterial.class, new CaseDataMaterialSerializer());

    private final String path;
    private final File file;
    private final YamlConfigurationLoader loader;

    private int version;
    private ConfigType type;
    private CommentedConfigurationNode node;
    private Object serialized;

    private boolean deleted;

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
                .indent(2)
                .defaultOptions(opts -> opts.serializers(build -> build.registerAll(SERIALIZER_COLLECTION.build())))
                .file(file)
                .build();
    }

    private void setMeta() throws SerializationException {
        CommentedConfigurationNode metaNode = node.node("config");
        String version = metaNode.node("version").getString();

        if (version != null) {
            this.version = parse(version);
            if (this.type == null)
                this.type = DefaultConfigType.getType(metaNode.node("type").getString());
        } else {
            this.version = parse(metaNode.getString());
            if (this.type == null)
                this.type = node.hasChild("case") ? DefaultConfigType.OLD_CASE : DefaultConfigType.UNKNOWN;
        }

        ConfigSerializer configSerializer = type.getConfigSerializer();
        if (configSerializer != null) {
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
    public CommentedConfigurationNode node() {
        return node;
    }

    @Override
    public @Nullable <T> T getSerialized(Class<T> clazz) {
        return clazz.cast(serialized);
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
    public boolean delete() {
        deleted = file.delete();
        return deleted;
    }

    @Override
    public void save() throws ConfigurateException {
        if (!deleted) loader.save(node);
    }

    @Override
    public String toString() {
        return path;
    }

}
