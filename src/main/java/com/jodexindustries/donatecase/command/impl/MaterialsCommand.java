package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.MaterialManager;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for /dc materials subcommand implementation
 */
public class MaterialsCommand implements SubCommandExecutor, SubCommandTabCompleter {

    public MaterialsCommand(SubCommandManager manager) {
        SubCommand subCommand = manager.builder("materials")
                .executor(this)
                .tabCompleter(this)
                .type(SubCommandType.ADMIN)
                .build();
        manager.registerSubCommand(subCommand);
    }


    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        Map<String, List<CaseMaterial>> materialsMap = buildMaterialsMap();
        for (Map.Entry<String, List<CaseMaterial>> entry : materialsMap.entrySet()) {
            Tools.msgRaw(sender, "&6" + entry.getKey());
            for (CaseMaterial material : entry.getValue()) {
                Tools.msgRaw(sender, "&9- &a" + material.getId() + " &3- &2" + material.getDescription());
            }
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

    /**
     * Key - Addon name
     * Value - list of CaseMaterial
     */
    private static Map<String, List<CaseMaterial>> buildMaterialsMap() {
        Map<String, List<CaseMaterial>> materialsMap = new HashMap<>();
        MaterialManager.getRegisteredMaterials().forEach((name, caseMaterial) -> {
            String addon = caseMaterial.getAddon().getName();

            List<CaseMaterial> materials = materialsMap.getOrDefault(addon, new ArrayList<>());
            materials.add(caseMaterial);

            materialsMap.put(addon, materials);
        });

        return materialsMap;
    }
}