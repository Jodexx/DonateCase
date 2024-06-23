package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.AddonManager;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for /dc addon subcommand implementation
 */
public class AddonCommand implements SubCommand {
    private final AddonManager manager = Case.getInstance().api.getAddonManager();
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length >= 2) {
            String action = args[0];
            String addonName = args[1];
            InternalJavaAddon addon = manager.getAddon(addonName);
            switch (action) {
                case "enable" : {
                    if (addon != null) {
                        if (!addon.isEnabled()) {
                            manager.enableAddon(addon, AddonManager.PowerReason.DONATE_CASE);
                            Tools.msg(sender, "&aAddon &6" + addonName + " &aenabled successfully!");
                        } else {
                            Tools.msg(sender, "&cAddon &6" + addonName + " &calready enabled!");
                        }
                    } else {
                        Tools.msg(sender, "&cAddon &6" + addonName + " &cnot loaded!");
                    }
                    break;
                }
                case "disable" : {
                    if(addon != null) {
                        if(addon.isEnabled()) {
                            manager.disableAddon(addon);
                            Tools.msg(sender, "&aAddon &6" + addonName + " &adisabled successfully!");
                        } else {
                            Tools.msg(sender, "&cAddon &6" + addonName + "&calready disabled!");
                        }
                    } else {
                        Tools.msg(sender, "&cAddon &6" + addonName + " &cnot loaded!");
                    }
                    break;
                }
                case "load" : {
                    File addonsFolder = new File(Case.getInstance().getDataFolder(), "addons");
                    File addonFile = new File(addonsFolder, addonName);
                    if (manager.loadAddon(addonFile)) {
                        Tools.msg(sender, "&aAddon &6" + addonName + " &aloaded successfully!");
                    } else {
                        Tools.msg(sender, "&cThere was an error loading the addon &6" + addonName + "&c. Check out the console.");
                    }
                    break;
                }
                case "unload" : {
                    if(addon != null) {
                        if(manager.unloadAddon(addon)) {
                            Tools.msg(sender, "&aAddon &6" + addonName + " &aunloaded successfully!");
                        } else {
                            Tools.msg(sender, "&cThere was an error unloading the addon &6" + addonName + "&c. Check out the console.");
                        }
                    } else {
                        Tools.msg(sender, "&cAddon &6" + addonName + " &calready unloaded!");
                    }
                    break;
                }
            }
        } else {
            GlobalCommand.sendHelp(sender, "dc");
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if(args.length == 1) {
            list.add("enable");
            list.add("disable");
            list.add("load");
            list.add("unload");
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("enable")) return getDisabledAddons();
            if(args[0].equalsIgnoreCase("disable")) return getEnabledAddons();
            if(args[0].equalsIgnoreCase("unload")) return getAddons();
            if(args[0].equalsIgnoreCase("load")) return getAddonsFiles();
        }
        return list;
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.ADMIN;
    }

    private List<String> getAddons() {
        return Case.getInstance().api.getAddonManager().getAddons().stream().map(InternalJavaAddon::getName).collect(Collectors.toList());

    }

    private List<String> getDisabledAddons() {
        return Case.getInstance().api.getAddonManager().getAddons().stream().filter(internalJavaAddon -> !internalJavaAddon.isEnabled() ).map(InternalJavaAddon::getName).collect(Collectors.toList());
    }

    private List<String> getEnabledAddons() {
        return Case.getInstance().api.getAddonManager().getAddons().stream().filter(InternalJavaAddon::isEnabled).map(InternalJavaAddon::getName).collect(Collectors.toList());
    }
    private List<String> getAddonsFiles() {
        List<String> addons = new ArrayList<>();
        File addonsDir = new File(Case.getInstance().getDataFolder(), "addons");
        File[] files = addonsDir.listFiles();
        if(files == null) return addons;
        return Arrays.stream(files).map(File::getName).filter(name -> name.endsWith(".jar")).collect(Collectors.toList());
    }
}
