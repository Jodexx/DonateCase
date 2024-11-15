package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.ToolsBukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.jodexindustries.donatecase.DonateCase.instance;

/**
 * Class for /dc delete subcommand implementation
 */
public class DeleteCommand implements SubCommandExecutor<CommandSender>, SubCommandTabCompleter<CommandSender> {

    public static void register(SubCommandManager<CommandSender> manager) {
        DeleteCommand command = new DeleteCommand();

        SubCommand<CommandSender> subCommand = manager.builder("delete")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.ADMIN.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Block block = player.getTargetBlock(null, 5);
                String customName = Case.getCaseCustomNameByLocation(block.getLocation());
                if (customName != null) {
                    if (!instance.api.getAnimationManager().getActiveCasesByBlock().containsKey(block)) {
                        Case.deleteCaseByName(customName);
                        if (instance.hologramManager != null)
                            instance.hologramManager.removeHologram(block);
                        ToolsBukkit.msg(sender, Case.getConfig().getLang().getString("case-removed"));
                    } else {
                        ToolsBukkit.msg(sender, Case.getConfig().getLang().getString("case-opens"));
                    }
                } else {
                    ToolsBukkit.msg(sender, Case.getConfig().getLang().getString("block-is-not-case"));
                }
            }
        } else if (args.length == 1) {
            String name = args[0];
            Location location = Case.getCaseLocationByCustomName(name);
            if (location != null) {
                if (!instance.api.getAnimationManager().getActiveCasesByBlock().containsKey(location.getBlock())) {
                    if (instance.hologramManager != null)
                        instance.hologramManager.removeHologram(location.getBlock());

                    Case.deleteCaseByName(name);
                    ToolsBukkit.msg(sender, Case.getConfig().getLang().getString("case-removed"));
                } else {
                    ToolsBukkit.msg(sender, Case.getConfig().getLang().getString("case-opens"));
                }
            } else {
                ToolsBukkit.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-does-not-exist"), "%case:" + name));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<String> value;
        if (args.length == 1) {
            ConfigurationSection section = Case.getConfig().getCases().getConfigurationSection("DonateCase.Cases");
            if (section != null) {
                value = new ArrayList<>(section.getKeys(false));
            } else {
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
        return value;
    }

}
