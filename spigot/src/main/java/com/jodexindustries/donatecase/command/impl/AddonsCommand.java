package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.jodexindustries.donatecase.DonateCase.instance;

/**
 * Class for /dc addons subcommand implementation
 */
public class AddonsCommand implements SubCommandExecutor<CommandSender>, SubCommandTabCompleter<CommandSender> {

    public static void register(SubCommandManager<CommandSender> manager) {
        AddonsCommand command = new AddonsCommand();

        SubCommand<CommandSender> subCommand = manager.builder("addons")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.ADMIN.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<InternalJavaAddon> addons = new ArrayList<>(instance.api.getAddonManager().getAddons().values());
        addons.sort(Comparator.comparing(InternalJavaAddon::getName));
        DCToolsBukkit.msgRaw(sender, "&7Currently loaded addons in DonateCase (&a" + addons.size() + "&7): " + compileAddons(addons));
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

    private @NotNull StringBuilder compileAddons(List<InternalJavaAddon> addons) {
        StringBuilder builder = new StringBuilder();
        if (addons != null && !addons.isEmpty()) {
            for (int i = 0; i < addons.size(); i++) {
                InternalJavaAddon addon = addons.get(i);
                String name = addon.isEnabled() ? "&a" + addon.getName() : "&c" + addon.getName();
                builder.append(name);
                if (i < addons.size() - 1) {
                    builder.append("&7, ");
                }
            }
        }
        return builder;
    }

}
