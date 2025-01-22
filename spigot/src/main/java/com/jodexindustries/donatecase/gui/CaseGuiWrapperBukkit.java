package com.jodexindustries.donatecase.gui;

import com.jodexindustries.donatecase.BukkitBackend;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.api.data.casedata.gui.GuiTypedItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.TypedItemHandler;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;


/**
 * Class for initializing case GUI
 */
public class CaseGuiWrapperBukkit extends CaseGuiWrapper {

    private final BukkitBackend backend;

    private final Inventory inventory;
    private List<CaseData.History> globalHistoryData;

    /**
     * Default constructor
     *
     * @param player   Player object
     * @param caseData CaseData object
     */
    public CaseGuiWrapperBukkit(BukkitBackend backend, @NotNull DCPlayer player, @NotNull CaseData caseData, @NotNull CaseLocation location) {
        super(player, caseData, location);
        this.backend = backend;

        String title = getTemporary().getTitle();
        inventory = Bukkit.createInventory(null, temporary.getSize(), DCTools.rc(setPlaceholders(title)));

        load().thenAccept((unused) -> {
            Bukkit.getScheduler().runTask(backend.getPlugin(), () -> player.openInventory(inventory));
            startUpdateTask();
        });
    }

    /**
     * Loads all items asynchronously
     *
     * @return Void future
     */
    @Override
    public CompletableFuture<Void> load() {
        return CompletableFuture.supplyAsync(() -> {
            globalHistoryData = DCTools.sortHistoryDataByDate(backend.getAPI().getDatabase().getCache());
            for (CaseGui.Item item : temporary.getItems().values()) {
                try {
                    processItem(item);
                } catch (Throwable e) {
                    backend.getLogger().log(Level.WARNING,
                            "Error occurred while loading item " + item.getItemName() + ":", e);
                }
            }
            return null;
        });
    }

    private void updateMeta(CaseGui.Item temp) {
        CaseDataMaterial original = getOriginal(temp.getItemName());
        CaseDataMaterial material = temp.getMaterial();
        material.setDisplayName(setPlaceholders(original.getDisplayName()));
        material.setLore(setPlaceholders(original.getLore()));
        material.updateMeta();
    }

    private void colorize(CaseDataMaterial material) {
        material.setDisplayName(DCTools.rc(material.getDisplayName()));
        material.setLore(DCTools.rc(material.getLore()));
        material.updateMeta();
    }

    private void startUpdateTask() {
        int updateRate = temporary.getUpdateRate();
        if (updateRate >= 0) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(backend.getPlugin(),
                    (task) -> {
                        if (!backend.getAPI().getGUIManager().getMap().containsKey(player.getUniqueId())) task.cancel();
                        load();
                    }, updateRate, updateRate);
        }
    }

    private CaseDataMaterial getOriginal(String itemName) {
        return caseData.getCaseGui().getItems().get(itemName).getMaterial();
    }

    private void processItem(CaseGui.Item item) {
        String itemType = item.getType();
        if (!itemType.equalsIgnoreCase("DEFAULT")) {
            GuiTypedItem typedItem = backend.getAPI().getGuiTypedItemManager().getFromString(itemType);
            if (typedItem != null) {
                TypedItemHandler handler = typedItem.getHandler();
                if (handler != null) item = handler.handle(this, item);
                if (typedItem.isUpdateMeta()) updateMeta(item);
            }
        } else {
            updateMeta(item);
        }

        CaseDataMaterial material = item.getMaterial();

        if (material.getItemStack() == null) material.setItemStack(backend.getTools().loadCaseItem(material.getId()));

        colorize(material);

        for (Integer slot : item.getSlots()) {
            inventory.setItem(slot, (ItemStack) item.getMaterial().getItemStack());
        }
    }

    private String setPlaceholders(@Nullable String text) {
        if (text == null) return null;
        String caseType = caseData.getCaseType();
        return backend.getPAPI().setPlaceholders(player,
                processPlaceholders(text.replace("%case%", caseType), caseType, player));
    }

    private List<String> setPlaceholders(List<String> lore) {
        return lore.stream().map(this::setPlaceholders).collect(Collectors.toList());
    }

    private String processPlaceholders(String line, String caseType, DCPlayer p) {
        String placeholder = DCTools.getLocalPlaceholder(line);

        if (p != null && placeholder.startsWith("keys")) {
            if (placeholder.startsWith("keys_")) {
                String[] parts = placeholder.split("_", 2);
                if (parts.length == 2) {
                    caseType = parts[1];
                }
            }

            line = line.replace("%" + placeholder + "%",
                    String.valueOf(backend.getAPI().getCaseKeyManager().getCache(caseType, p.getName())));
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
     * Gets GUI global history data
     *
     * @return global history data
     */
    @NotNull
    public List<CaseData.History> getGlobalHistoryData() {
        return globalHistoryData;
    }
}