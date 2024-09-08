package com.jodexindustries.testaddon.boot;

import com.jodexindustries.donatecase.api.ActionManager;
import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.CaseManager;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.addon.external.ExternalJavaAddon;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.events.KeysTransactionEvent;
import com.jodexindustries.testaddon.TestAction;
import com.jodexindustries.testaddon.TestAnimation;
import com.jodexindustries.testaddon.commands.FirstCommand;
import com.jodexindustries.testaddon.commands.SecondCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Loader implements Listener {
    private final CaseManager api;
    private final Plugin plugin;
    private final Addon addon;


    public Loader(JavaPlugin plugin) {
        this.addon = new ExternalJavaAddon(plugin);
        this.plugin = plugin;
        this.api = new CaseManager(plugin);
    }

    public Loader(InternalJavaAddon addon) {
        this.addon = addon;
        this.plugin = addon.getDonateCase();
        this.api = addon.getCaseAPI();
    }

    public void load() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        // register subcommands

        // the first method of registering subcommand
        SubCommandManager subCommandManager = api.getSubCommandManager();
        FirstCommand executor = new FirstCommand();

        SubCommand first = subCommandManager.builder("test")
                .type(SubCommandType.PLAYER)
                .executor(executor)
                .tabCompleter(executor)
                .args(new String[]{"(test)", "(test2)"})
                .description("This is cool command!")
                .build();
        subCommandManager.registerSubCommand(first);

        // the second method of registering subcommand
        SecondCommand second = new SecondCommand("test2", api.getAddon());
        subCommandManager.registerSubCommand(second);

        // register animation
        AnimationManager animationManager = api.getAnimationManager();
        animationManager.registerAnimation("test", TestAnimation.class, "Here some description");

        // register action
        ActionManager actionManager = api.getActionManager();
        actionManager.registerAction("[test]", new TestAction(), "Awesome action!");
    }

    public void unload() {
        // unregister subcommand
        api.getSubCommandManager().unregisterSubCommand("test");
        api.getSubCommandManager().unregisterSubCommand("test2");
        // unregister animation
        api.getAnimationManager().unregisterAnimation("test");
    }

    @EventHandler
    public void onInventory(CaseGuiClickEvent e) {
        Bukkit.getServer().broadcastMessage(e.getGui().getCaseData().getCaseType());
    }

    @EventHandler
    public void onTransaction(KeysTransactionEvent e) {
        Logger logger = addon.getLogger();
        logger.info("Transaction: " + e.transactionType());
        logger.info("Type: " + e.type());
        logger.info("From: " + e.from());
        logger.info("To: " + e.to());
        logger.info("Amount: " + e.amount());
        logger.info("Player: " + e.playerName());
        logger.info("Case type: " + e.caseType());
    }
}
