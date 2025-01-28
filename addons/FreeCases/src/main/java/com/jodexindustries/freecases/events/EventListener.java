package com.jodexindustries.freecases.events;

import com.jodexindustries.donatecase.api.event.DonateCaseReloadEvent;
import com.jodexindustries.freecases.utils.CooldownManager;
import com.jodexindustries.freecases.utils.Tools;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {
    private final Tools t;

    public EventListener(Tools t) {
        this.t = t;
    }

    @EventHandler
    public void onReload(DonateCaseReloadEvent event) {
        if(event.getType() == DonateCaseReloadEvent.Type.CONFIG) t.getConfig().reloadConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
            if (!t.getConfig().getData().getStringList("Used").contains(player.getName()) ||
                    !t.getConfig().getConfig().getBoolean("GetOneTime")) {
                CooldownManager.setCooldown(player.getUniqueId(), System.currentTimeMillis());
            }
    }
}
