package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for /dc reload subcommand implementation
 */
public class ReloadCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            Case.getInstance().setupConfigs();
            if(Case.getInstance().hologramManager != null) Case.getInstance().hologramManager.removeAllHolograms();
            Case.getInstance().loadHolograms();
            Tools.msg(sender, Tools.rt(Case.getCustomConfig().getLang().getString("config-reloaded")));
        } else {
            if(args[0].equalsIgnoreCase("cache")) {
                Case.getInstance().cleanCache();
                Case.getInstance().setupConfigs();
                Tools.msg(sender, Tools.rt(Case.getCustomConfig().getLang().getString("config-cache-reloaded", "&aReloaded all DonateCase Cache")));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if(args.length == 1) {
            list.add("cache");
        }
        return list;
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.ADMIN;
    }
}
