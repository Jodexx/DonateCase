package com.jodexindustries.dcphysicalkey.listener;

import com.jodexindustries.dcphysicalkey.bootstrap.Bootstrap;
import com.jodexindustries.donatecase.api.events.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.api.events.PreOpenCaseEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.jodexindustries.dcphysicalkey.bootstrap.Bootstrap.NAMESPACED_KEY;

public class EventListener implements Listener {
    private final Bootstrap bootstrap;
    public EventListener(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @EventHandler
    public void onReload(DonateCaseReloadEvent e) {
        if(e.getType() == DonateCaseReloadEvent.Type.CONFIG) {
            bootstrap.getConfig().reloadConfig();
            bootstrap.unload();
            bootstrap.load();
        }
    }

    @EventHandler
    public void onCaseInteract(PreOpenCaseEvent event) {
        Player p = event.getPlayer();

        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if(meta == null) continue;

                PersistentDataContainer container = meta.getPersistentDataContainer();
                if(!container.has(NAMESPACED_KEY, PersistentDataType.STRING)) continue;

                String caseType = container.get(NAMESPACED_KEY, PersistentDataType.STRING);
                if(event.getCaseData().getCaseType().equals(caseType)) {
                    event.setIgnoreKeys(true);
                    item.setAmount(item.getAmount() - 1);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onCaseKeyPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();

        // Проверка, что предмет в руке игрока не пуст и имеет метаданные
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;

            PersistentDataContainer container = meta.getPersistentDataContainer();

            // Проверка, что предмет является ключом для кейса
            if (container.has(NAMESPACED_KEY, PersistentDataType.STRING)) {
                // Отменяем установку блока, если предмет - это ключ
                event.setCancelled(true);
                player.sendMessage("Этот ключ можно использовать только для открытия кейсов!");
            }
        }
    }
}
