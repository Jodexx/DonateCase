package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static com.jodexindustries.donatecase.DonateCase.customConfig;
import static com.jodexindustries.donatecase.tools.Tools.resolveSDGCompletions;

public class GiveKeyCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            String player = args[0];
            String caseName = args[1];
            Player target = Bukkit.getPlayer(player);
            int keys;
            try {
                keys = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                Tools.msgRaw(sender, Tools.rt(customConfig.getLang().getString("NumberFormatException"), "%string:" + args[2]));
                return;
            }
            if (Case.hasCaseByType(caseName)) {
                CaseData data = Case.getCase(caseName);
                if(data == null) return;
                String caseTitle = data.getCaseTitle();
                String caseDisplayName = data.getCaseDisplayName();
                Case.addKeys(caseName, player, keys);
                Tools.msg(sender, Tools.rt(customConfig.getLang().getString("GiveKeys"), "%player:" + player, "%key:" + keys, "%casetitle:" + caseTitle, "%casedisplayname:" + caseDisplayName, "%case:" + caseName));
                if (customConfig.getConfig().getBoolean("DonatCase.SetKeysTargetMessage")) {
                    Tools.msg(target, Tools.rt(customConfig.getLang().getString("GiveKeysTarget"), "%player:" + player, "%key:" + keys, "%casetitle:" + caseTitle, "%casedisplayname:" + caseDisplayName, "%case:" + caseName));
                }
            } else {
                Tools.msg(sender, Tools.rt(customConfig.getLang().getString("CaseNotExist"), "%case:" + caseName));
            }
        } else {
            GlobalCommand.sendHelp(sender, "dc");
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
