package com.jodexindustries.dcphysicalkey.listener;

import com.jodexindustries.dcphysicalkey.bootstrap.MainAddon;
import com.jodexindustries.donatecase.api.event.Subscriber;
import com.jodexindustries.donatecase.api.event.player.PreOpenCaseEvent;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import net.kyori.event.method.annotation.Subscribe;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.jodexindustries.dcphysicalkey.bootstrap.MainAddon.NAMESPACED_KEY;

public class EventListener implements Listener, Subscriber {

    private final MainAddon addon;

    public EventListener(MainAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onReload(DonateCaseReloadEvent e) {
        if(e.type() == DonateCaseReloadEvent.Type.CONFIG) {
            addon.getConfig().reloadConfig();
        }
    }

    @Subscribe
    public void onCaseInteract(PreOpenCaseEvent event) {
        DCPlayer dcPlayer = event.player();

        Player player = (Player) dcPlayer.getHandler();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if(meta == null) continue;

                PersistentDataContainer container = meta.getPersistentDataContainer();
                if(!container.has(NAMESPACED_KEY, PersistentDataType.STRING)) continue;

                String caseType = container.get(NAMESPACED_KEY, PersistentDataType.STRING);
                if(event.caseData().caseType().equals(caseType)) {
                    event.ignoreKeys(true);
                    item.setAmount(item.getAmount() - 1);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onCaseKeyPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;

            PersistentDataContainer container = meta.getPersistentDataContainer();

            if (container.has(NAMESPACED_KEY, PersistentDataType.STRING)) {
                event.setCancelled(true);
            }
        }
    }
}
