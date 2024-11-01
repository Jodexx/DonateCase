package com.jodexindustries.dcphysicalkey.commands;

import com.jodexindustries.dcphysicalkey.bootstrap.Bootstrap;
import com.jodexindustries.dcphysicalkey.tools.ItemManager;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.dcphysicalkey.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.jodexindustries.donatecase.tools.Tools.rc;

public class MainCommand implements SubCommandExecutor, SubCommandTabCompleter {
    private final Config config;
    private final SubCommandManager manager;

    private String commandName;

    public MainCommand(SubCommandManager manager, Bootstrap bootstrap) {
        this.config = bootstrap.getConfig();
        this.manager = manager;
    }

    public void register() {
        commandName = config.get().getString("command", "physicalkey");
        SubCommand subCommand = manager.builder(commandName)
                .permission(config.get().getString("permissions.give", "dcphysicalkey.give"))
                .description("&2Gives physical key to specific player")
                .args(new String[]{
                        "givekey",
                        "&7(&aPlayer&7)",
                        "&7(&aKey name&7)",
                        "&7(&aAmount&7)"
                })
                .executor(this)
                .tabCompleter(this)
                .build();
        manager.registerSubCommand(subCommand);
    }

    public void unregister() {
        if(commandName != null) manager.unregisterSubCommand(commandName);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length < 1) {
            return;
        }

        if (args[0].equalsIgnoreCase("givekey")) {
            if (sender.hasPermission(config.get().getString("permissions.give", "dcphysicalkey.give"))) {
                handleGiveKeyCommand(sender, args);
            }
        }
    }

    private void handleGiveKeyCommand(@NotNull CommandSender sender, String[] args) {
        if (args.length < 4) {
            return;
        }

        String playerName = args[1];
        String keyName = args[2];
        int amount;

        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(rc(config.get().getString("messages.invalid-number", "")));
            return;
        }

        Player targetPlayer = Bukkit.getServer().getPlayer(playerName);
        if (targetPlayer == null) {
            sender.sendMessage(rc(
                    config.get().getString("messages.player-not-found", "")
                            .replace("%player%", playerName)
            ));
            return;
        }

        ItemStack itemStack = ItemManager.items.get(keyName);

        if (itemStack == null) {
            sender.sendMessage(rc(config.get().getString("messages.key-not-found", "")));
            return;
        }

        itemStack.setAmount(amount);

        targetPlayer.getInventory().addItem(itemStack);

        sender.sendMessage(rc(
                config.get().getString("messages.give-key", "")
                        .replace("%player%", playerName)
                        .replace("%amount%", String.valueOf(amount))
        ));
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("givekey");
        } else {
            if(args[0].equalsIgnoreCase("givekey")) {
                if (args.length == 2) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        completions.add(p.getName());
                    }
                }

                if(args.length == 3) {
                    completions.addAll(ItemManager.items.keySet());
                }
            }
        }

        return completions;
    }

}
