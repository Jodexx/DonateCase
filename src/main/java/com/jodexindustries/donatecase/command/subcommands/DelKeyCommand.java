package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;

import java.util.List;

import static com.jodexindustries.donatecase.tools.Tools.resolveSDGCompletions;

public class DelKeyCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            GlobalCommand.sendHelp(sender, "dc");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("all")) {
                if (!Case.getInstance().sql) {
                    Case.getInstance().customConfig.getKeys().set("DonatCase.Cases", null);
                    Case.getInstance().customConfig.saveKeys();
                    Tools.msg(sender, Tools.rt(Case.getInstance().customConfig.getLang().getString("ClearAllKeys")));
                } else {
                    Case.getInstance().mysql.delAllKey();
                    Tools.msg(sender, Tools.rt(Case.getInstance().customConfig.getLang().getString("ClearAllKeys")));
                }
            }
        } else {
            String player = args[0];
            String caseName = args[1];
            if (Case.hasCaseByType(caseName)) {
                CaseData data = Case.getCase(caseName);
                if(data == null) return;
                String caseTitle = data.getCaseTitle();
                String caseDisplayName = data.getCaseDisplayName();
                Case.setNullKeys(caseName, player);
                Tools.msg(sender, Tools.rt(Case.getInstance().customConfig.getLang().getString("ClearKeys"), "%player:" + player, "%casetitle:" + caseTitle, "%casedisplayname:" + caseDisplayName, "%case:" + caseName));
            } else {
                Tools.msg(sender, Tools.rt(Case.getInstance().customConfig.getLang().getString("CaseNotExist"), "%case:" + caseName));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return resolveSDGCompletions(args);
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.MODER;
    }
}
