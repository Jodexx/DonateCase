package com.jodexindustries.donatecase.api.config;

import com.jodexindustries.donatecase.api.tools.Pair;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

public interface ConfigCasesBukkit extends ConfigCases {

    Map<String, Pair<File, YamlConfiguration>> getCases();

    Pair<File, YamlConfiguration> getCase(String name);
}
