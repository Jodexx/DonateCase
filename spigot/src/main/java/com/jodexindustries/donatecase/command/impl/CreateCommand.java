package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CreateCommand extends SubCommand<CommandSender> {
    
    private final DCAPIBukkit api;
    
    public CreateCommand(DCAPIBukkit api) {
        super("create", api.getAddon());
        setPermission(SubCommandType.ADMIN.permission);
        this.api = api;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location l = player.getTargetBlock(null, 5).getLocation().setDirection(player.getLocation().getDirection());
            if (args.length >= 2) {
                String caseType = args[0];
                String caseName = args[1];
                if (api.getCaseManager().hasCaseByType(caseType)) {
                    if (Case.hasCaseByLocation(l)) {
                        api.getTools().msg(sender, api.getConfig().getLang().getString("case-already-created"));
                    } else {
                        if (!Case.hasCaseByCustomName(caseName)) {
                            Case.saveLocation(caseName, caseType, l);
                            api.getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("case-added"),
                                    "%casename:" + caseName, "%casetype:" + caseType));
                        } else {
                            api.getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("case-already-exist"),
                                    "%casename:" + caseName));
                        }
                    }
                } else {
                    api.getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("case-does-not-exist"),
                            "%case:" + caseType));
                }
            } else {
                GlobalCommand.sendHelp(sender, label);
            }
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<String> list = new ArrayList<>(api.getConfig().getConfigCases().getCases().keySet());
        if (args.length >= 2) {
            return new ArrayList<>();
        }
        return list;
    }

}
