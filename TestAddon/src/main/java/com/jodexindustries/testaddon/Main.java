package com.jodexindustries.testaddon;

import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.addon.JavaAddon;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Main extends JavaAddon implements Listener {
    @Override
    public void onEnable() {
        AnimationManager.registerAnimation("test", new TestAnimation());
        getDonateCase().getServer().getPluginManager().registerEvents(this, getDonateCase());
    }
    @EventHandler
    public void onInventory(CaseGuiClickEvent e) {
        getDonateCase().getServer().broadcastMessage(e.getCaseType());
    }
}