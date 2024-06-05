package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            Case.getInstance().setupConfigs();
            Case.getInstance().setupLangs();
            if(Case.getInstance().hologramManager != null) Case.getInstance().hologramManager.removeAllHolograms();
            Case.getInstance().loadHolograms();
            Tools.msg(sender, Tools.rt(Case.getInstance().customConfig.getLang().getString("ReloadConfig")));
        } else {
            if(args[0].equalsIgnoreCase("cache")) {
                Case.getInstance().cleanCache();
                Case.getInstance().setupConfigs();
                Case.getInstance().setupLangs();
                Tools.msg(sender, Tools.rt(Case.getInstance().customConfig.getLang().getString("ReloadConfigCache", "&aReloaded all DonateCase Cache")));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.ADMIN;
    }
}
