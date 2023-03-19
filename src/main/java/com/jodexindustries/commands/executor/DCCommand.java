package com.jodexindustries.commands.executor;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public abstract class DCCommand implements CommandExecutor, TabExecutor {
    private Player p;
    private final transient String name;

    public DCCommand(String name) {
        this.name = name;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            this.run(sender, cmd, label, args);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return this.onTabComplete(sender, this.p, cmd, label, args);
    }

    public abstract boolean run(CommandSender var1, Command var2, String var3, String[] var4) throws Exception;

    public abstract List<String> onTabComplete(CommandSender var1, Player var2, Command var3, String var4, String[] var5);
}
