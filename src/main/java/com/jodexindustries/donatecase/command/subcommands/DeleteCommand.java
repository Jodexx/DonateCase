package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.CaseManager;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for /dc delete subcommand implementation
 */
public class DeleteCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Location l = player.getTargetBlock(null, 5).getLocation();
                if (Case.hasCaseByLocation(l)) {
                    if(!Case.activeCasesByLocation.containsKey(l)) {
                        Case.deleteCaseByLocation(l);
                        if (CaseManager.getHologramManager() != null)
                            CaseManager.getHologramManager().removeHologram(l.getBlock());
                        Tools.msg(sender, Case.getConfig().getLang().getString("case-removed"));
                    } else {
                        Tools.msg(sender, Case.getConfig().getLang().getString("case-opens"));
                    }
                } else {
                    Tools.msg(sender, Case.getConfig().getLang().getString("block-is-not-case"));
                }
            }
        } else if (args.length == 1) {
            String name = args[0];
            if (Case.hasCaseByCustomName(name)) {
                Location location = Case.getCaseLocationByCustomName(name);
                if(!Case.activeCasesByLocation.containsKey(location)) {
                    if (CaseManager.getHologramManager() != null)
                        if (location != null) CaseManager.getHologramManager().removeHologram(location.getBlock());
                    Case.deleteCaseByName(name);
                    Tools.msg(sender, Case.getConfig().getLang().getString("case-removed"));
                } else {
                    Tools.msg(sender, Case.getConfig().getLang().getString("case-opens"));
                }
            } else {
                Tools.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-does-not-exist"), "%case:" + name));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> value;
        if (args.length == 1) {
            ConfigurationSection section = Case.getConfig().getCases().getConfigurationSection("DonatCase.Cases");
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

    @Override
    public SubCommandType getType() {
        return SubCommandType.ADMIN;
    }
}
