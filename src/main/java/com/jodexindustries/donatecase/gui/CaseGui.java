package com.jodexindustries.donatecase.gui;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.GUITypedItemManager;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.GUI;
import com.jodexindustries.donatecase.api.data.gui.GUITypedItem;
import com.jodexindustries.donatecase.api.data.gui.TypedItemHandler;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Class for initializing case GUI
 */
public class CaseGui {
    private final Inventory inventory;
    private final CaseData caseData;
    private final GUI tempGUI;
    private final Location location;
    private final Player player;
    private List<CaseData.HistoryData> globalHistoryData;

    /**
     * Default constructor
     *
     * @param player   Player object
     * @param caseData CaseData object
     */
    public CaseGui(Player player, CaseData caseData, Location location) {
        this.player = player;
        this.caseData = caseData;
        this.tempGUI = caseData.getGui().clone();
        this.location = location;

        String title = caseData.getCaseTitle();
        inventory = Bukkit.createInventory(null, tempGUI.getSize(), Tools.rc(title));
        load();
        player.openInventory(inventory);
        startUpdateTask();
    }

    /**
     * Loads all items asynchronously
     *
     * @return Void future
     */
    public CompletableFuture<Void> load() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(Case.getInstance(), () -> {
            globalHistoryData = Case.getSortedHistoryDataCache();
            for (GUI.Item item : tempGUI.getItems().values()) {
                try {
                    processItem(item);
                } catch (Throwable e) {
                    Case.getInstance().getLogger().log(Level.WARNING,
                            "Error occurred while loading item " + item.getItemName() + ":", e);
                }
            }
            future.complete(null);
        });
        return future;
    }

    private void updateMeta(GUI.Item temp) {
        CaseData.Item.Material original = getOriginal(temp.getItemName());
        CaseData.Item.Material material = temp.getMaterial();
        material.setDisplayName(setPlaceholders(original.getDisplayName()));
        material.setLore(setPlaceholders(original.getLore()));
        material.updateMeta();
    }

    private void colorize(CaseData.Item.Material material) {
        material.setDisplayName(Tools.rc(material.getDisplayName()));
        material.setLore(Tools.rc(material.getLore()));
        material.updateMeta();
    }

    private void startUpdateTask() {
        int updateRate = tempGUI.getUpdateRate();
        if (updateRate >= 0) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(Case.getInstance(),
                    (task) -> {
                        if (!Case.playersGui.containsKey(player.getUniqueId())) task.cancel();
                        load();
                    }, updateRate, updateRate);
        }
    }

    private CaseData.Item.Material getOriginal(String itemName) {
        return caseData.getGui().getItems().get(itemName).getMaterial();
    }

    private void processItem(GUI.Item item) {
        String itemType = item.getType();
        if (!itemType.equalsIgnoreCase("DEFAULT")) {
            GUITypedItem typedItem = GUITypedItemManager.getFromString(itemType);
            if (typedItem != null) {
                TypedItemHandler handler = typedItem.getItemHandler();
                if (handler != null) item = handler.handle(this, item);
                if (typedItem.isUpdateMeta()) updateMeta(item);
            }
        } else {
            updateMeta(item);
        }

        CaseData.Item.Material material = item.getMaterial();

        if (material.getItemStack() == null) material.setItemStack(Tools.loadCaseItem(material.getId()));

        colorize(material);

        for (Integer slot : item.getSlots()) {
            inventory.setItem(slot, item.getMaterial().getItemStack());
        }
    }

    private String setPlaceholders(@Nullable String text) {
        if (text == null) return null;
        String caseType = caseData.getCaseType();
        return Case.getInstance().papi.setPlaceholders(player,
                processPlaceholders(text.replace("%case%", caseType), caseType, player));
    }

    private List<String> setPlaceholders(List<String> lore) {
        return lore.stream().map(this::setPlaceholders).collect(Collectors.toList());
    }

    private String processPlaceholders(String line, String caseType, Player p) {
        String placeholder = Tools.getLocalPlaceholder(line);

        if (p != null && placeholder.startsWith("keys")) {
            if (placeholder.startsWith("keys_")) {
                String[] parts = placeholder.split("_", 2);
                if (parts.length == 2) {
                    caseType = parts[1];
                }
            }

            line = line.replace("%" + placeholder + "%",
                    String.valueOf(Case.getKeysCache(caseType, p.getName())));
        }

        return line;
    }


    /**
     * Gets GUI Inventory
     *
     * @return inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Gets location where GUI opened
     *
     * @return GUI location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets player who opened GUI
     *
     * @return player who opened
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets GUI CaseData. Can be modified, cause this is clone of original {@link Case#getCase(String)}
     *
     * @return data
     */
    public CaseData getCaseData() {
        return caseData;
    }

    /**
     * Gets temporary GUI. Used for updating placeholders, if UpdateRate enabled
     *
     * @return GUI
     */
    public GUI getTempGUI() {
        return tempGUI;
    }

    /**
     * Gets GUI global history data
     *
     * @return global history data
     */
    public List<CaseData.HistoryData> getGlobalHistoryData() {
        return globalHistoryData;
    }
}