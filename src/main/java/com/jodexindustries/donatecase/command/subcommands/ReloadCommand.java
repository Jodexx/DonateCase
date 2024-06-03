package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static com.jodexindustries.donatecase.DonateCase.*;

public class ReloadCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            instance.setupConfigs();
            instance.setupLangs();
            if(hologramManager != null) hologramManager.removeAllHolograms();
            instance.loadHolograms();
            Tools.msg(sender, Tools.rt(customConfig.getLang().getString("ReloadConfig")));
        } else {
            if(args[0].equalsIgnoreCase("cache")) {
                instance.cleanCache();
                instance.setupConfigs();
                instance.setupLangs();
                Tools.msg(sender, Tools.rt(customConfig.getLang().getString("ReloadConfigCache", "&aReloaded all DonateCase Cache")));
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
