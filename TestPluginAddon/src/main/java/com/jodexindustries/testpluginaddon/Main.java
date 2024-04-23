package com.jodexindustries.testpluginaddon;

import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.CaseManager;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        // getting CaseAPI for addon
        CaseManager api = new CaseManager(this);
        // register subcommand
        SubCommandManager subCommandManager = api.getSubCommandManager();
        subCommandManager.registerSubCommand("test", new TestCommand());
        // register animation
        AnimationManager animationManager = api.getAnimationManager();
        animationManager.registerAnimation("test", new TestAnimation());
        // register event listener
        getServer().getPluginManager().registerEvents(this, this);
    }
    @EventHandler
    public void onInventory(CaseGuiClickEvent e) {
        getServer().broadcastMessage(e.getCaseType());
    }
}