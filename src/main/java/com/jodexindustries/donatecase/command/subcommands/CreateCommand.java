package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CreateCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Location l = player.getTargetBlock(null, 5).getLocation().setDirection(player.getLocation().getDirection());
            if (args.length >= 2) {
                String caseType = args[0];
                String caseName = args[1];
                if (Case.hasCaseByType(caseType)) {
                    if (Case.hasCaseByLocation(l)) {
                        Tools.msg(sender, Case.getInstance().customConfig.getLang().getString("HasDonatCase"));
                    } else {
                        if (!Case.hasCaseTypeByCustomName(caseName)) {
                            Case.saveLocation(caseName, caseType, l);
                            Tools.msg(sender, Tools.rt(Case.getInstance().customConfig.getLang().getString("AddDonatCase"),
                                    "%casename:" + caseName, "%casetype:" + caseType));
                        } else {
                            Tools.msg(sender, Tools.rt(Case.getInstance().customConfig.getLang().getString("CaseAlreadyHasByName"),
                                    "%casename:" + caseName));
                        }
                    }
                } else {
                    Tools.msg(sender, Tools.rt(Case.getInstance().customConfig.getLang().getString("CaseNotExist"),
                            "%case:" + caseType));
                }
            } else {
                GlobalCommand.sendHelp(sender, "dc");
            }
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>(Case.getInstance().casesConfig.getCases().keySet());
        if(args.length >= 2) {
            return new ArrayList<>();
        }
        return list;
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.ADMIN;
    }
}
