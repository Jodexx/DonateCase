package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.ActionManager;
import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.MaterialManager;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class RegistryCommand implements SubCommandExecutor, SubCommandTabCompleter {

    public RegistryCommand(SubCommandManager manager) {
        SubCommand subCommand = manager.builder("registry")
                .executor(this)
                .tabCompleter(this)
                .permission(SubCommandType.ADMIN.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            Tools.msgRaw(sender, "&c/" + label + " registry (animations|materials|actions)");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "animations":
                executeAnimations(sender);
                break;
            case "materials":
                executeMaterials(sender);
                break;
            case "actions":
                executeActions(sender);
                break;
            default:
                Tools.msgRaw(sender, "&c/" + label + " registry (animations|materials|actions)");
                break;
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        List<String> value = new ArrayList<>();
        if(args.length == 1) {
            value.add("animations");
            value.add("materials");
            value.add("actions");
        }

        if (args[args.length - 1].isEmpty()) {
            Collections.sort(value);
            return value;
        }

        return value.stream()
                .filter(tmp -> tmp.startsWith(args[args.length - 1]))
                .sorted()
                .collect(Collectors.toList());
    }

    private static void executeAnimations(CommandSender sender) {
        Map<String, List<CaseAnimation>> animationsMap = buildAnimationsMap();
        for (Map.Entry<String, List<CaseAnimation>> entry : animationsMap.entrySet()) {
            Tools.msgRaw(sender, "&6" + entry.getKey());
            for (CaseAnimation animation : entry.getValue()) {
                Tools.msgRaw(sender, "&9- &a" + animation.getName() + " &3- &2" + animation.getDescription());
            }
        }
    }

    private static Map<String, List<CaseAnimation>> buildAnimationsMap() {
        Map<String, List<CaseAnimation>> animationsMap = new HashMap<>();
        AnimationManager.registeredAnimations.forEach((animationName, caseAnimation) -> {
            String addon = caseAnimation.getAddon().getName();

            List<CaseAnimation> animations = animationsMap.getOrDefault(addon, new ArrayList<>());
            animations.add(caseAnimation);

            animationsMap.put(addon, animations);
        });
        return animationsMap;
    }

    private static void executeMaterials(CommandSender sender) {
        Map<String, List<CaseMaterial>> materialsMap = buildMaterialsMap();
        for (Map.Entry<String, List<CaseMaterial>> entry : materialsMap.entrySet()) {
            Tools.msgRaw(sender, "&6" + entry.getKey());
            for (CaseMaterial material : entry.getValue()) {
                Tools.msgRaw(sender, "&9- &a" + material.getId() + " &3- &2" + material.getDescription());
            }
        }
    }

    /**
     * Key - Addon name
     * Value - list of CaseMaterial
     */
    private static Map<String, List<CaseMaterial>> buildMaterialsMap() {
        Map<String, List<CaseMaterial>> materialsMap = new HashMap<>();
        MaterialManager.registeredMaterials.forEach((name, caseMaterial) -> {
            String addon = caseMaterial.getAddon().getName();

            List<CaseMaterial> materials = materialsMap.getOrDefault(addon, new ArrayList<>());
            materials.add(caseMaterial);

            materialsMap.put(addon, materials);
        });

        return materialsMap;
    }

    private static void executeActions(CommandSender sender) {
        Map<String, List<CaseAction>> actionsMap = buildActionsMap();
        for (Map.Entry<String, List<CaseAction>> entry : actionsMap.entrySet()) {
            Tools.msgRaw(sender, "&6" + entry.getKey());
            for (CaseAction action : entry.getValue()) {
                Tools.msgRaw(sender, "&9- &a" + action.getName() + " &3- &2" + action.getDescription());
            }
        }
    }

    /**
     * Key - Addon name
     * Value - list of CaseAction
     */
    private static Map<String, List<CaseAction>> buildActionsMap() {
        Map<String, List<CaseAction>> actionsMap = new HashMap<>();
        ActionManager.registeredActions.forEach((name, caseAction) -> {
            String addon = caseAction.getAddon().getName();

            List<CaseAction> actions = actionsMap.getOrDefault(addon, new ArrayList<>());
            actions.add(caseAction);

            actionsMap.put(addon, actions);
        });

        return actionsMap;
    }
}
