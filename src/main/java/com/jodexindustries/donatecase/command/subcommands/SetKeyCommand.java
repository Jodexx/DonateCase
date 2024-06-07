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

import static com.jodexindustries.donatecase.tools.Tools.resolveSDGCompletions;

public class SetKeyCommand implements SubCommand {
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
                Tools.msgRaw(sender, Tools.rt(Case.getCustomConfig().getLang().getString("number-format-exception"), "%string:" + args[3]));
                return;
            }
            if (Case.hasCaseByType(caseName)) {
                CaseData data = Case.getCase(caseName);
                if(data == null) return;
                String caseTitle = data.getCaseTitle();
                String caseDisplayName = data.getCaseDisplayName();
                Case.setKeys(caseName, player, keys);
                Tools.msg(sender, Tools.rt(Case.getCustomConfig().getLang().getString("keys-sets"), "%player:" + player, "%key:" + keys, "%casetitle:" + caseTitle, "%casedisplayname:" + caseDisplayName, "%case:" + caseName));
                if (Case.getCustomConfig().getConfig().getBoolean("DonatCase.SetKeysTargetMessage")) {
                    Tools.msg(target, Tools.rt(Case.getCustomConfig().getLang().getString("keys-sets-target"), "%player:" + player, "%key:" + keys, "%casetitle:" + caseTitle, "%casedisplayname:" + caseDisplayName, "%case:" + caseName));
                }
            } else {
                Tools.msg(sender, Tools.rt(Case.getCustomConfig().getLang().getString("case-does-not-exist"), "%case:" + caseName));
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
