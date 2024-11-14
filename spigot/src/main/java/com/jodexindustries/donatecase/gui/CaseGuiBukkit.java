package com.jodexindustries.donatecase.gui;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataHistory;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.data.casedata.gui.GUI;
import com.jodexindustries.donatecase.api.data.casedata.gui.GUITypedItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.TypedItemHandler;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.ToolsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Class for initializing case GUI
 */
public class CaseGuiBukkit implements CaseGui {
    private final Inventory inventory;
    private final CaseDataBukkit caseData;
    private final GUI<CaseDataMaterialBukkit> tempGUI;
    private final Location location;
    private final Player player;
    private List<CaseDataHistory> globalHistoryData;

    /**
     * Default constructor
     *
     * @param player   Player object
     * @param caseData CaseData object
     */
    public CaseGuiBukkit(@NotNull Player player, @NotNull CaseDataBukkit caseData, @NotNull Location location) {
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
    @Override
    public CompletableFuture<Void> load() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(Case.getInstance(), () -> {
            globalHistoryData = Case.getSortedHistoryDataCache();
            for (GUI.Item<CaseDataMaterialBukkit> item : tempGUI.getItems().values()) {
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

    private void updateMeta(GUI.Item<CaseDataMaterialBukkit> temp) {
        CaseDataMaterialBukkit original = getOriginal(temp.getItemName());
        CaseDataMaterialBukkit material = temp.getMaterial();
        material.setDisplayName(setPlaceholders(original.getDisplayName()));
        material.setLore(setPlaceholders(original.getLore()));
        material.updateMeta();
    }

    private void colorize(CaseDataMaterialBukkit material) {
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

    private CaseDataMaterialBukkit getOriginal(String itemName) {
        return caseData.getGui().getItems().get(itemName).getMaterial();
    }

    private void processItem(GUI.Item<CaseDataMaterialBukkit> item) {
        String itemType = item.getType();
        if (!itemType.equalsIgnoreCase("DEFAULT")) {
            GUITypedItem<CaseDataMaterialBukkit, CaseGui, CaseGuiClickEvent> typedItem = Case.getInstance().api.getGuiTypedItemManager().getFromString(itemType);
            if (typedItem != null) {
                TypedItemHandler<CaseDataMaterialBukkit, CaseGui> handler = typedItem.getItemHandler();
                if (handler != null) item = handler.handle(this, item);
                if (typedItem.isUpdateMeta()) updateMeta(item);
            }
        } else {
            updateMeta(item);
        }

        CaseDataMaterialBukkit material = item.getMaterial();

        if (material.getItemStack() == null) material.setItemStack(ToolsBukkit.loadCaseItem(material.getId()));

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
                    String.valueOf(Case.getInstance().api.getCaseKeyManager().getKeysCache(caseType, p.getName())));
        }

        return line;
    }


    /**
     * Gets GUI Inventory
     *
     * @return inventory
     */
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Gets location where GUI opened
     *
     * @return GUI location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }

    /**
     * Gets player who opened GUI
     *
     * @return player who opened
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets GUI CaseData. Can be modified, cause this is clone of original {@link Case#getCase(String)}
     *
     * @return data
     */
    @NotNull
    public CaseDataBukkit getCaseData() {
        return caseData;
    }

    /**
     * Gets temporary GUI. Used for updating placeholders, if UpdateRate enabled
     *
     * @return GUI
     */
    @NotNull
    public GUI<CaseDataMaterialBukkit> getTempGUI() {
        return tempGUI;
    }

    /**
     * Gets GUI global history data
     *
     * @return global history data
     */
    @NotNull
    public List<CaseDataHistory> getGlobalHistoryData() {
        return globalHistoryData;
    }
}