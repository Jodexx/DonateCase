package com.jodexindustries.testaddon;

import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.CaseManager;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Main extends InternalJavaAddon implements Listener {
    @Override
    public void onEnable() {
        // getting CaseAPI for addon
        CaseManager api = getCaseAPI();
        // register subcommand
        SubCommandManager subCommandManager = api.getSubCommandManager();
        subCommandManager.registerSubCommand("test", new TestCommand());
        // register animation
        AnimationManager animationManager = api.getAnimationManager();
        animationManager.registerAnimation("test", new TestAnimation());

        // register event listener
        getDonateCase().getServer().getPluginManager().registerEvents(this, getDonateCase());
    }

    @Override
    public void onDisable() {
        getCaseAPI().getSubCommandManager().unregisterSubCommand("test");
        getCaseAPI().getAnimationManager().unregisterAnimation("test");
    }

    @EventHandler
    public void onInventory(CaseGuiClickEvent e) {
        getDonateCase().getServer().broadcastMessage(e.getCaseType());
    }
}