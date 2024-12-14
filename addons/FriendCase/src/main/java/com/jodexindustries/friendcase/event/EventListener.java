package com.jodexindustries.friendcase.event;

import com.jodexindustries.donatecase.api.events.DonateCaseReloadEvent;
import com.jodexindustries.friendcase.utils.Tools;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EventListener implements Listener {

    private final Tools t;

    public EventListener(Tools t) {
        this.t = t;
    }

    @EventHandler
    public void onReload(DonateCaseReloadEvent event) {
        if(event.getType() == DonateCaseReloadEvent.Type.CONFIG) t.getConfig().reloadConfig();
    }
}
