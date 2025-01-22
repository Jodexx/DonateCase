package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.PowerReason;
import com.jodexindustries.donatecase.api.addon.internal.InternalAddonClassLoader;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.managers.AddonManagerImpl;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddonCommand extends SubCommand.SubCommandBuilder implements SubCommandExecutor, SubCommandTabCompleter {

    private final DCAPI api;

    public AddonCommand(DCAPI api) {
        super();
        name("addon");
        addon(api.getPlatform());
        permission(SubCommandType.ADMIN.permission);
        executor(this);
        tabCompleter(this);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        if (args.length < 2) {
            return false;
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
                return false;
        }

        return true;
    }


    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
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

    private void handleEnableCommand(DCCommandSender sender, String addonName) {
        InternalJavaAddon addon = api.getAddonManager().get(addonName);
        if (addon == null) {
            sender.sendMessage(DCTools.prefix("&cAddon &6" + addonName + " &cnot loaded!"));
            return;
        }
        if (addon.isEnabled()) {
            sender.sendMessage(DCTools.prefix("&cAddon &6" + addonName + " &calready enabled!"));
            return;
        }
        if (api.getAddonManager().enable(addon, PowerReason.DONATE_CASE)) {
            handleAddonSuccess(sender, addonName, "enabled");
        } else {
            handleAddonError(sender, addonName, "enabling");
        }
    }

    private void handleDisableCommand(DCCommandSender sender, String addonName) {
        InternalJavaAddon addon = api.getAddonManager().get(addonName);
        if (addon == null) {
            sender.sendMessage(DCTools.prefix("&cAddon &6" + addonName + " &cnot loaded!"));
            return;
        }
        if (!addon.isEnabled()) {
            sender.sendMessage(DCTools.prefix("&cAddon &6" + addonName + "&calready disabled!"));
            return;
        }
        api.getAddonManager().disable(addon, PowerReason.DONATE_CASE);
        handleAddonSuccess(sender, addonName, "disabled");
    }

    private void handleLoadCommand(DCCommandSender sender, String addonName) {
        File addonFile = new File(api.getAddonManager().getFolder(), addonName);
        if (!addonFile.exists()) {
            sender.sendMessage(DCTools.prefix("&cFile &6" + addonName + " &cnot found!"));
            return;
        }

        InternalAddonClassLoader loader = AddonManagerImpl.getAddonClassLoader(addonFile);
        if (loader != null) {
            sender.sendMessage(DCTools.prefix("&cAddon &6" + addonName + " &calready loaded!"));
            return;
        }
        if (api.getAddonManager().load(addonFile)) {
            loader = AddonManagerImpl.getAddonClassLoader(addonFile);
            if (loader == null) {
                handleAddonError(sender, addonName, "loading");
                return;
            }
            InternalJavaAddon addon = loader.getAddon();
            if (api.getAddonManager().enable(addon, PowerReason.DONATE_CASE)) {
                handleAddonSuccess(sender, addonName, "loaded");
            } else {
                handleAddonError(sender, addonName, "enabling");
            }
        } else {
            handleAddonError(sender, addonName, "loading");
        }
    }

    private void handleUnloadCommand(DCCommandSender sender, String addonName) {
        InternalJavaAddon addon = api.getAddonManager().get(addonName);
        if (addon == null) {
            sender.sendMessage(DCTools.prefix("&cAddon &6" + addonName + " &calready unloaded!"));
            return;
        }
        if (api.getAddonManager().unload(addon, PowerReason.DONATE_CASE)) {
            handleAddonSuccess(sender, addonName, "unloaded");
        } else {
            handleAddonError(sender, addonName, "unloading");
        }
    }

    private void handleAddonError(DCCommandSender sender, String addonName, String action) {
        sender.sendMessage(DCTools.prefix("&cThere was an error " + action + " the addon &6" + addonName + "&c."));
    }

    private void handleAddonSuccess(DCCommandSender sender, String addonName, String action) {
        sender.sendMessage(DCTools.prefix("&aAddon &6" + addonName + " &a" + action + " successfully!"));
    }

    private List<String> getAddons() {
        return api.getAddonManager().getMap().values().stream().map(InternalJavaAddon::getName).collect(Collectors.toList());
    }

    private List<String> getDisabledAddons() {
        return api.getAddonManager().getMap().values().stream().filter(internalJavaAddon -> !internalJavaAddon.isEnabled()).map(InternalJavaAddon::getName).collect(Collectors.toList());
    }

    private List<String> getEnabledAddons() {
        return api.getAddonManager().getMap().values().stream().filter(InternalJavaAddon::isEnabled).map(InternalJavaAddon::getName).collect(Collectors.toList());
    }

    private List<String> getAddonsFiles() {
        List<String> addons = new ArrayList<>();
        File addonsDir = api.getAddonManager().getFolder();
        File[] files = addonsDir.listFiles();
        if (files == null) return addons;
        return Arrays.stream(files).map(File::getName).filter(name -> name.endsWith(".jar")).collect(Collectors.toList());
    }

}