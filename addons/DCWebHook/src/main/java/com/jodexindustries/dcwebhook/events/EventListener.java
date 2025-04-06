package com.jodexindustries.dcwebhook.events;

import com.jodexindustries.dcwebhook.bootstrap.MainAddon;
import com.jodexindustries.dcwebhook.config.DiscordWebhook;
import com.jodexindustries.donatecase.api.event.Subscriber;
import com.jodexindustries.donatecase.api.event.animation.AnimationEndEvent;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;
import net.kyori.event.PostOrders;
import net.kyori.event.method.annotation.PostOrder;
import net.kyori.event.method.annotation.Subscribe;


import java.io.IOException;
import java.util.logging.Level;

public class EventListener implements Subscriber {

    private final MainAddon addon;

    public EventListener(MainAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    @PostOrder(PostOrders.LAST)
    public void onReload(DonateCaseReloadEvent e) {
        if (e.type() == DonateCaseReloadEvent.Type.CONFIG) addon.config.load();
    }

    @Subscribe
    @PostOrder(PostOrders.LAST)
    public void onAnimationEnd(AnimationEndEvent e) {
        addon.api.getPlatform().getScheduler().async(addon, () -> {
            try {
                DiscordWebhook webhook = addon.config.getWebhook();
                if(webhook != null) webhook.execute(e);
            } catch (IOException ex) {
                addon.getLogger().log(Level.WARNING, "Error with webhook sending: ", ex);
            }
        }, 0L);
    }
}
