package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.casedata.gui.GUITypedItem;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.jodexindustries.donatecase.DonateCase.instance;

public class RegistryCommand implements SubCommandExecutor<CommandSender>, SubCommandTabCompleter<CommandSender> {

    public static void register(SubCommandManager<CommandSender> manager) {
        RegistryCommand command = new RegistryCommand();

        SubCommand<CommandSender> subCommand = manager.builder("registry")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.ADMIN.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            DCToolsBukkit.msgRaw(sender, "&c/" + label + " registry (animations|materials|actions|guitypeditems)");
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
            case "guitypeditems":
                executeGuiTypedItems(sender);
                break;
            default:
                DCToolsBukkit.msgRaw(sender, "&c/" + label + " registry (animations|materials|actions|guitypeditems)");
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

    private static void executeAnimations(CommandSender sender) {
        Map<String, List<CaseAnimation<JavaAnimationBukkit, CaseDataMaterialBukkit, ItemStack>>> animationsMap = buildAnimationsMap();
        for (Map.Entry<String, List<CaseAnimation<JavaAnimationBukkit, CaseDataMaterialBukkit, ItemStack>>> entry : animationsMap.entrySet()) {
            DCToolsBukkit.msgRaw(sender, "&6" + entry.getKey());
            for (CaseAnimation<JavaAnimationBukkit, CaseDataMaterialBukkit, ItemStack> animation : entry.getValue()) {
                DCToolsBukkit.msgRaw(sender, "&9- &a" + animation.getName() + " &3- &2" + animation.getDescription());
            }
        }
    }

    private static Map<String, List<CaseAnimation<JavaAnimationBukkit, CaseDataMaterialBukkit, ItemStack>>> buildAnimationsMap() {
        Map<String, List<CaseAnimation<JavaAnimationBukkit, CaseDataMaterialBukkit, ItemStack>>> animationsMap = new HashMap<>();
        instance.api.getAnimationManager().getRegisteredAnimations().forEach((animationName, caseAnimation) -> {
            String addon = caseAnimation.getAddon().getName();

            List<CaseAnimation<JavaAnimationBukkit, CaseDataMaterialBukkit, ItemStack>> animations = animationsMap.getOrDefault(addon, new ArrayList<>());
            animations.add(caseAnimation);

            animationsMap.put(addon, animations);
        });
        return animationsMap;
    }

    private static void executeMaterials(CommandSender sender) {
        Map<String, List<CaseMaterial<ItemStack>>> materialsMap = buildMaterialsMap();
        for (Map.Entry<String, List<CaseMaterial<ItemStack>>> entry : materialsMap.entrySet()) {
            DCToolsBukkit.msgRaw(sender, "&6" + entry.getKey());
            for (CaseMaterial<ItemStack> material : entry.getValue()) {
                DCToolsBukkit.msgRaw(sender, "&9- &a" + material.getId() + " &3- &2" + material.getDescription());
            }
        }
    }

    /**
     * Key - Addon name
     * Value - list of CaseMaterial
     */
    private static Map<String, List<CaseMaterial<ItemStack>>> buildMaterialsMap() {
        Map<String, List<CaseMaterial<ItemStack>>> materialsMap = new HashMap<>();
        instance.api.getMaterialManager().getRegisteredMaterials().forEach((name, caseMaterial) -> {
            String addon = caseMaterial.getAddon().getName();

            List<CaseMaterial<ItemStack>> materials = materialsMap.getOrDefault(addon, new ArrayList<>());
            materials.add(caseMaterial);

            materialsMap.put(addon, materials);
        });

        return materialsMap;
    }

    private static void executeActions(CommandSender sender) {
        Map<String, List<CaseAction<Player>>> actionsMap = buildActionsMap();
        for (Map.Entry<String, List<CaseAction<Player>>> entry : actionsMap.entrySet()) {
            DCToolsBukkit.msgRaw(sender, "&6" + entry.getKey());
            for (CaseAction<Player> action : entry.getValue()) {
                DCToolsBukkit.msgRaw(sender, "&9- &a" + action.getName() + " &3- &2" + action.getDescription());
            }
        }
    }

    /**
     * Key - Addon name
     * Value - list of CaseAction
     */
    private static Map<String, List<CaseAction<Player>>> buildActionsMap() {
        Map<String, List<CaseAction<Player>>> actionsMap = new HashMap<>();
        instance.api.getActionManager().getRegisteredActions().forEach((name, caseAction) -> {
            String addon = caseAction.getAddon().getName();

            List<CaseAction<Player>> actions = actionsMap.getOrDefault(addon, new ArrayList<>());
            actions.add(caseAction);

            actionsMap.put(addon, actions);
        });

        return actionsMap;
    }

    private static void executeGuiTypedItems(CommandSender sender) {
        Map<String, List<GUITypedItem<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent>>> guitypeditemsMap = buildGuiTypedItemsMap();
        for (Map.Entry<String, List<GUITypedItem<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent>>> entry : guitypeditemsMap.entrySet()) {
            DCToolsBukkit.msgRaw(sender, "&6" + entry.getKey());
            for (GUITypedItem<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent> guiTypedItem : entry.getValue()) {
                DCToolsBukkit.msgRaw(sender, "&9- &a" + guiTypedItem.getId() + " &3- &2" + guiTypedItem.getDescription());
            }
        }
    }

    /**
     * Key - Addon name
     * Value - list of GUITypedItem
     */
    private static Map<String, List<GUITypedItem<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent>>> buildGuiTypedItemsMap() {
        Map<String, List<GUITypedItem<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent>>> guiTypedItemsMap = new HashMap<>();
        instance.api.getGuiTypedItemManager().getRegisteredItems().forEach((name, guiTypedItem) -> {
            String addon = guiTypedItem.getAddon().getName();

            List<GUITypedItem<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent>> actions = guiTypedItemsMap.getOrDefault(addon, new ArrayList<>());
            actions.add(guiTypedItem);

            guiTypedItemsMap.put(addon, actions);
        });

        return guiTypedItemsMap;
    }
}
