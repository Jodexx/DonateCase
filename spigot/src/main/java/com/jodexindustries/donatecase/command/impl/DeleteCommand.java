package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class DeleteCommand extends SubCommand<CommandSender> {
    
    private final DCAPIBukkit api;
    
    public DeleteCommand(DCAPIBukkit api) {
        super("delete", api.getAddon());
        setPermission(SubCommandType.ADMIN.permission);
        this.api = api;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Block block = player.getTargetBlock(null, 5);
                String customName = Case.getCaseCustomNameByLocation(block.getLocation());
                if(customName == null) {
                    api.getTools().msg(sender, api.getConfig().getLang().getString("block-is-not-case"));
                    return;
                }

                if(api.getAnimationManager().getActiveCasesByBlock().containsKey(block)) {
                    api.getTools().msg(sender, api.getConfig().getLang().getString("case-opens"));
                    return;
                }

                Case.deleteCaseByName(customName);
                if (api.getHologramManager() != null) api.getHologramManager().removeHologram(block);
                api.getTools().msg(sender, api.getConfig().getLang().getString("case-removed"));
            }
        } else if (args.length == 1) {
            String name = args[0];
            Location location = Case.getCaseLocationByCustomName(name);
            if (location != null) {
                if(location.getWorld() != null) {
                    if(api.getAnimationManager().getActiveCasesByBlock().containsKey(location.getBlock())) {
                        api.getTools().msg(sender, api.getConfig().getLang().getString("case-opens"));
                        return;
                    }

                    if (api.getHologramManager() != null) api.getHologramManager().removeHologram(location.getBlock());
                }

                Case.deleteCaseByName(name);
                api.getTools().msg(sender, api.getConfig().getLang().getString("case-removed"));
            } else {
                api.getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("case-does-not-exist"), "%case:" + name));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<String> value;
        if (args.length == 1) {
            ConfigurationSection section = api.getConfig().getCases().getConfigurationSection("DonateCase.Cases");
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
