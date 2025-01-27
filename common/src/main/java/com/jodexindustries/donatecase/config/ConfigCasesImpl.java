package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.api.config.ConfigCases;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.serializer.CaseDataMaterialSerializer;
import com.jodexindustries.donatecase.serializer.CaseGuiSerializer;
import com.jodexindustries.donatecase.platform.BackendPlatform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for loading cases configurations
 */
public class ConfigCasesImpl implements ConfigCases {

    private final Map<String, ConfigurationNode> cases = new HashMap<>();
    private final BackendPlatform platform;

    private final TypeSerializerCollection serializerCollection = TypeSerializerCollection.builder()
            .register(CaseGui.class, new CaseGuiSerializer())
            .register(CaseGui.Item.class, new CaseGuiSerializer.Item())
            .register(CaseDataMaterial.class, new CaseDataMaterialSerializer())
            .build();

    public ConfigCasesImpl(BackendPlatform platform) {
        this.platform = platform;

    }

    /**
     * Get file name without file format
     *
     * @param file File for checking
     * @return File name without format
     */
    private static String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        return fileName.lastIndexOf(".") == -1 ? fileName : fileName.substring(0, fileName.lastIndexOf("."));
    }

    @NotNull
    private File[] getCasesInFolder() {
        File directory = new File(platform.getDataFolder(), "cases");
        File[] files = directory.listFiles();
        return files != null ? files : new File[0];
    }

    @Override
    public void load() throws ConfigurateException {
        cases.clear();

        if (getCasesInFolder().length == 0) {
            platform.saveResource("cases/case.yml", false);
        }

        for (File file : getCasesInFolder()) {
            String name = getFileNameWithoutExtension(file);
            YamlConfigurationLoader loader = YamlConfigurationLoader
                    .builder()
                    .defaultOptions(opts -> opts.serializers(build -> build.registerAll(serializerCollection)))
                    .file(file)
                    .build();

            ConfigurationNode node = loader.load();

            if (node.hasChild("case")) {
                cases.put(name, node);
            } else {
                platform.getLogger().warning("Case " + name + " has a broken case section, skipped.");
            }
        }
    }

    @Override
    public @Nullable ConfigurationNode getCase(@NotNull String name) {
        return cases.get(name);
    }

    @Override
    public @NotNull Map<String, ConfigurationNode> getMap() {
        return cases;
    }
}
