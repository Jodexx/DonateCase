package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.command.GlobalCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for /dc help subcommand implementation
 */
public class HelpCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        GlobalCommand.sendHelp(sender, "dc");
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.PLAYER;
    }
}
