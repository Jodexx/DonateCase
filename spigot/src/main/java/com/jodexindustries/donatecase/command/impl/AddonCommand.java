package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.AddonManager;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.addon.internal.InternalAddonClassLoader;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for /dc addon subcommand implementation
 */
public class AddonCommand implements SubCommandExecutor, SubCommandTabCompleter {
    private final AddonManager manager = Case.getInstance().api.getAddonManager();

    public static void register(SubCommandManager manager) {
        AddonCommand command = new AddonCommand();

        SubCommand subCommand = manager.builder("addon")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.ADMIN.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length < 2) {
            GlobalCommand.sendHelp(sender, label);
            return;
        }

        String action = args[0];
        String addonName = args[1];

        switch (action) {
            case "enable":
                handleEnableCommand(sender, addonName);
                break;
            case "disable":
                handleDisableCommand(sender, addonName);
                break;
            case "load":
                handleLoadCommand(sender, addonName);
                break;
            case "unload":
                handleUnloadCommand(sender, addonName);
                break;
            default:
                GlobalCommand.sendHelp(sender, label);
                break;
        }
    }


    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("enable");
            list.add("disable");
            list.add("load");
            list.add("unload");
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("enable")) return getDisabledAddons();
            if (args[0].equalsIgnoreCase("disable")) return getEnabledAddons();
            if (args[0].equalsIgnoreCase("unload")) return getAddons();
            if (args[0].equalsIgnoreCase("load")) return getAddonsFiles();
        }
        return list;
    }

    private void handleEnableCommand(CommandSender sender, String addonName) {
        InternalJavaAddon addon = AddonManager.getAddon(addonName);
        if (addon == null) {
            Tools.msg(sender, "&cAddon &6" + addonName + " &cnot loaded!");
            return;
        }
        if (addon.isEnabled()) {
            Tools.msg(sender, "&cAddon &6" + addonName + " &calready enabled!");
            return;
        }
        if (manager.enableAddon(addon, AddonManager.PowerReason.DONATE_CASE)) {
            handleAddonSuccess(sender, addonName, "enabled");
        } else {
            handleAddonError(sender, addonName, "enabling");
        }
    }

    private void handleDisableCommand(CommandSender sender, String addonName) {
        InternalJavaAddon addon = AddonManager.getAddon(addonName);
        if (addon == null) {
            Tools.msg(sender, "&cAddon &6" + addonName + " &cnot loaded!");
            return;
        }
        if (!addon.isEnabled()) {
            Tools.msg(sender, "&cAddon &6" + addonName + "&calready disabled!");
            return;
        }
        manager.disableAddon(addon, AddonManager.PowerReason.DONATE_CASE);
        handleAddonSuccess(sender, addonName, "disabled");
    }

    private void handleLoadCommand(CommandSender sender, String addonName) {
        File addonFile = new File(AddonManager.getAddonsFolder(), addonName);
        if (!addonFile.exists()) {
            Tools.msg(sender, "&cFile &6" + addonName + " &cnot found!");
            return;
        }

        InternalAddonClassLoader loader = AddonManager.getAddonClassLoader(addonFile);
        if (loader != null) {
            Tools.msg(sender, "&cAddon &6" + addonName + " &calready loaded!");
            return;
        }
        if (manager.loadAddon(addonFile)) {
            loader = AddonManager.getAddonClassLoader(addonFile);
            if (loader == null) {
                handleAddonError(sender, addonName, "loading");
                return;
            }
            InternalJavaAddon addon = loader.getAddon();
            if (manager.enableAddon(addon, AddonManager.PowerReason.DONATE_CASE)) {
                handleAddonSuccess(sender, addonName, "loaded");
            } else {
                handleAddonError(sender, addonName, "enabling");
            }
        } else {
            handleAddonError(sender, addonName, "loading");
        }
    }

    private void handleUnloadCommand(CommandSender sender, String addonName) {
        InternalJavaAddon addon = AddonManager.getAddon(addonName);
        if (addon == null) {
            Tools.msg(sender, "&cAddon &6" + addonName + " &calready unloaded!");
            return;
        }
        if (manager.unloadAddon(addon, AddonManager.PowerReason.DONATE_CASE)) {
            handleAddonSuccess(sender, addonName, "unloaded");
        } else {
            handleAddonError(sender, addonName, "unloading");
        }
    }

    private void handleAddonError(CommandSender sender, String addonName, String action) {
        Tools.msg(sender, "&cThere was an error " + action + " the addon &6" + addonName + "&c.");
    }

    private void handleAddonSuccess(CommandSender sender, String addonName, String action) {
        Tools.msg(sender, "&aAddon &6" + addonName + " &a" + action + " successfully!");
    }

    private List<String> getAddons() {
        return AddonManager.addons.values().stream().map(InternalJavaAddon::getName).collect(Collectors.toList());
    }

    private List<String> getDisabledAddons() {
        return AddonManager.addons.values().stream().filter(internalJavaAddon -> !internalJavaAddon.isEnabled()).map(InternalJavaAddon::getName).collect(Collectors.toList());
    }

    private List<String> getEnabledAddons() {
        return AddonManager.addons.values().stream().filter(InternalJavaAddon::isEnabled).map(InternalJavaAddon::getName).collect(Collectors.toList());
    }

    private List<String> getAddonsFiles() {
        List<String> addons = new ArrayList<>();
        File addonsDir = new File(Case.getInstance().getDataFolder(), "addons");
        File[] files = addonsDir.listFiles();
        if (files == null) return addons;
        return Arrays.stream(files).map(File::getName).filter(name -> name.endsWith(".jar")).collect(Collectors.toList());
    }

}