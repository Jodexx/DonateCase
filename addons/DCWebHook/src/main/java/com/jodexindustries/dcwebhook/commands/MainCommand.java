package com.jodexindustries.dcwebhook.commands;

import com.jodexindustries.dcwebhook.tools.Tools;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.jodexindustries.donatecase.tools.Tools.rc;

public class MainCommand implements SubCommandExecutor, SubCommandTabCompleter {
    private final Tools t;

    public MainCommand(Tools t) {
        this.t = t;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(rc("&a/dc webhook reload"));
        } else {
            if(args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(rc("&aConfig reloaded!"));
                t.getConfig().reloadConfig();
            }
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if(args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("reload");
            return completions;
        }
        return new ArrayList<>();
    }
}
