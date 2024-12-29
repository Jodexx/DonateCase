package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.jodexindustries.donatecase.DonateCase.instance;

/**
 * Class for /dc reload subcommand implementation
 */
public class ReloadCommand implements SubCommandExecutor<CommandSender>, SubCommandTabCompleter<CommandSender> {

    public static void register(SubCommandManager<CommandSender> manager) {
        ReloadCommand command = new ReloadCommand();

        SubCommand<CommandSender> subCommand = manager.builder("reload")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.ADMIN.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length == 0) {
            load();
            instance.api.getTools().msg(sender, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("config-reloaded")));
        } else {
            if (args[0].equalsIgnoreCase("cache")) {
                Case.cleanCache();
                load();
                Case.getInstance().loadDatabase();
                instance.api.getTools().msg(sender, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("config-cache-reloaded",
                        "&aReloaded all DonateCase Cache")));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("cache");
        }
        return list;
    }

    private void load() {
        instance.api.getConfig().load();
        Case.getInstance().loader.load();
        Case.getInstance().loadHolograms();
    }

}
