package com.jodexindustries.donatecase.common.command.sub;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.command.DefaultCommand;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class RegistryCommand extends DefaultCommand {

    private final DCAPI api;

    public RegistryCommand(DCAPI api) {
        super(api, "registry", SubCommandType.ADMIN);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return false;

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
            case "guitypeditems":
                executeGuiTypedItems(sender);
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, @NotNull String[] args) {
        List<String> value = new ArrayList<>();
        if(args.length == 1) {
            value.add("animations");
            value.add("materials");
            value.add("actions");
            value.add("guitypeditems");
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

    private void executeAnimations(DCCommandSender sender) {
        Map<String, List<CaseAnimation>> animationsMap = buildAnimationsMap();
        for (Map.Entry<String, List<CaseAnimation>> entry : animationsMap.entrySet()) {
            sender.sendMessage(
                    DCTools.rc("&6" + entry.getKey())
            );
            for (CaseAnimation animation : entry.getValue()) {
                sender.sendMessage(
                        DCTools.rc( "&9- &a" + animation.getName() + " &3- &2" + animation.getDescription())
                );
            }
        }
    }

    private Map<String, List<CaseAnimation>> buildAnimationsMap() {
        Map<String, List<CaseAnimation>> animationsMap = new HashMap<>();
        api.getAnimationManager().getMap().forEach((animationName, caseAnimation) -> {
            String addon = caseAnimation.getAddon().getName();

            List<CaseAnimation> animations = animationsMap.getOrDefault(addon, new ArrayList<>());
            animations.add(caseAnimation);

            animationsMap.put(addon, animations);
        });
        return animationsMap;
    }

    private void executeMaterials(DCCommandSender sender) {
        Map<String, List<CaseMaterial>> materialsMap = buildMaterialsMap();
        for (Map.Entry<String, List<CaseMaterial>> entry : materialsMap.entrySet()) {
            sender.sendMessage(
                    DCTools.rc("&6" + entry.getKey())
            );
            for (CaseMaterial material : entry.getValue()) {
                sender.sendMessage(
                        DCTools.rc("&9- &a" + material.id() + " &3- &2" + material.description())
                );
            }
        }
    }

    private Map<String, List<CaseMaterial>> buildMaterialsMap() {
        Map<String, List<CaseMaterial>> materialsMap = new HashMap<>();
        api.getMaterialManager().getMap().forEach((name, caseMaterial) -> {
            String addon = caseMaterial.addon().getName();

            List<CaseMaterial> materials = materialsMap.getOrDefault(addon, new ArrayList<>());
            materials.add(caseMaterial);

            materialsMap.put(addon, materials);
        });

        return materialsMap;
    }

    private void executeActions(DCCommandSender sender) {
        Map<String, List<CaseAction>> actionsMap = buildActionsMap();
        for (Map.Entry<String, List<CaseAction>> entry : actionsMap.entrySet()) {
            sender.sendMessage(
                    DCTools.rc("&6" + entry.getKey())
            );
            for (CaseAction action : entry.getValue()) {
                sender.sendMessage(
                        DCTools.rc("&9- &a" + action.addon() + " &3- &2" + action.description())
                );
            }
        }
    }

    private Map<String, List<CaseAction>> buildActionsMap() {
        Map<String, List<CaseAction>> actionsMap = new HashMap<>();
        api.getActionManager().getMap().forEach((name, caseAction) -> {
            String addon = caseAction.addon().getName();

            List<CaseAction> actions = actionsMap.getOrDefault(addon, new ArrayList<>());
            actions.add(caseAction);

            actionsMap.put(addon, actions);
        });

        return actionsMap;
    }

    private void executeGuiTypedItems(DCCommandSender sender) {
        Map<String, List<TypedItem>> guitypeditemsMap = buildGuiTypedItemsMap();
        for (Map.Entry<String, List<TypedItem>> entry : guitypeditemsMap.entrySet()) {
            sender.sendMessage(
                    DCTools.rc("&6" + entry.getKey())
            );
            for (TypedItem typedItem : entry.getValue()) {
                sender.sendMessage(
                        DCTools.rc("&9- &a" + typedItem.id() + " &3- &2" + typedItem.description())
                );
            }
        }
    }

    private Map<String, List<TypedItem>> buildGuiTypedItemsMap() {
        Map<String, List<TypedItem>> guiTypedItemsMap = new HashMap<>();
        api.getGuiTypedItemManager().getMap().forEach((name, guiTypedItem) -> {
            String addon = guiTypedItem.addon().getName();

            List<TypedItem> actions = guiTypedItemsMap.getOrDefault(addon, new ArrayList<>());
            actions.add(guiTypedItem);

            guiTypedItemsMap.put(addon, actions);
        });

        return guiTypedItemsMap;
    }
}
