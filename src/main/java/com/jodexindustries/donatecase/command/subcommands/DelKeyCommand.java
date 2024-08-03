package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

import static com.jodexindustries.donatecase.tools.Tools.resolveSDGCompletions;

/**
 * Class for /dc delkey subcommand implementation
 */
public class DelKeyCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(Case.getInstance(), () -> {
            if (args.length == 0) {
                GlobalCommand.sendHelp(sender, "dc");
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("all")) {
                    if (!Case.getInstance().sql) {
                        Case.getConfig().getKeys().set("DonatCase.Cases", null);
                        Case.getConfig().saveKeys();
                    } else {
                        Case.getInstance().mysql.delAllKey();
                    }
                    Tools.msg(sender, Tools.rt(Case.getConfig().getLang().getString("all-keys-cleared")));
                }
            } else {
                String player = args[0];
                String caseType = args[1];
                if (Case.hasCaseByType(caseType)) {
                    CaseData data = Case.getCase(caseType);
                    if (data == null) return;
                    String caseTitle = data.getCaseTitle();
                    String caseDisplayName = data.getCaseDisplayName();
                    int keys;
                    if (args.length == 2) {
                        keys = Case.getKeys(caseType, player);
                        Case.setKeys(caseType, player, 0);
                    } else {
                        try {
                            keys = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            Tools.msgRaw(sender, Tools.rt(Case.getConfig().getLang().getString("number-format-exception"), "%string:" + args[2]));
                            return;
                        }

                        Case.removeKeys(caseType, player, keys);
                    }
                    Tools.msg(sender, Tools.rt(Case.getConfig().getLang().getString("keys-cleared"),
                            "%player:" + player, "%casetitle:" + caseTitle,
                            "%casedisplayname:" + caseDisplayName, "%case:" + caseType, "%key:" + keys));
                } else {
                    Tools.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-does-not-exist"), "%case:" + caseType));
                }
            }
        });
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
