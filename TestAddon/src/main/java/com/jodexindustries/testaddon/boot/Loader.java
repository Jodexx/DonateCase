package com.jodexindustries.testaddon.boot;

import com.jodexindustries.donatecase.api.ActionManager;
import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.CaseManager;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.testaddon.TestAction;
import com.jodexindustries.testaddon.TestAnimation;
import com.jodexindustries.testaddon.commands.FirstCommand;
import com.jodexindustries.testaddon.commands.SecondCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Loader implements Listener {
    private final CaseManager api;
    private JavaPlugin plugin;
    private InternalJavaAddon addon;


    public Loader(JavaPlugin plugin) {
        this.plugin = plugin;
        this.api = new CaseManager(plugin);
    }

    public Loader(InternalJavaAddon addon) {
        this.addon = addon;
        this.api = addon.getCaseAPI();
    }

    public void load() {
        if(plugin != null) plugin.getServer().getPluginManager().registerEvents(this, plugin);
        if(addon != null) addon.getDonateCase().getServer().getPluginManager().registerEvents(this, addon.getDonateCase());

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
}
