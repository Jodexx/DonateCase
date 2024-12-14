package com.jodexindustries.freecases.commands;

import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.freecases.utils.CooldownManager;
import com.jodexindustries.freecases.utils.Tools;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

public class MainCommand implements SubCommandExecutor<CommandSender> {
    private final Tools t;
    public MainCommand(Tools t) {
        this.t = t;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Don't use console!");
            return;
        }
        Player player = (Player) sender;
        if (!sender.hasPermission("freecases.use")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    t.getConfig().getConfig().getString("PermissionsNeed", "")));
            return;
        }

        if (t.getConfig().getConfig().getStringList("Used").contains(player.getName())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    t.getConfig().getConfig().getString("AlreadyReceived", "")));
            return;
        }

        long timeStamp = (CooldownManager.getCooldown(player.getUniqueId()) + (t.getConfig().getConfig().getLong("TimeToPlay") * 1000L)) - System.currentTimeMillis();
        long time = Duration.ofMillis(timeStamp).getSeconds();
        String caseName = t.getConfig().getConfig().getString("Casename");
        if (time <= 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    t.getConfig().getConfig().getString("Done", "")));
            t.getMain().getDCAPI().getCaseKeyManager().addKeys(caseName, sender.getName(), 1);
            List<String> players = t.getConfig().getData().getStringList("Used");
            players.add(player.getName());

            if (t.getConfig().getConfig().getBoolean("GetOneTime")) {
                t.getConfig().getData().set("Used", players);
                t.getConfig().saveData();
            } else {
                CooldownManager.setCooldown(player.getUniqueId(), System.currentTimeMillis());
            }
        } else {
            sender.sendMessage(t.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&',
                    t.getConfig().getConfig().getString("Wait", "")
                            .replaceAll("%time%", t.formatTime(time)))));
        }
    }
}
