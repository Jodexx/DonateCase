package com.jodexindustries.testpluginaddon;

import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TestCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("Bukkit command");
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.PLAYER;
    }

    @Override
    public String[] getArgs() {
        return new String[]{"(test)", "(test2)"};
    }

    @Override
    public String getDescription() {
        return "This is cool command!";
    }
}
