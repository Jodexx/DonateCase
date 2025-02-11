package com.jodexindustries.donatecase.common.gui;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseInventory;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemException;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemHandler;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.platform.Platform;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CaseGuiWrapperImpl implements CaseGuiWrapper {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    protected final Platform platform;
    protected final DCPlayer player;
    protected final CaseData caseData;
    protected final CaseLocation location;
    protected final CaseGui temporary;
    protected final CaseInventory inventory;

    private List<CaseData.History> globalHistoryData;

    public CaseGuiWrapperImpl(@NotNull Platform platform, @NotNull DCPlayer player, @NotNull CaseData caseData, @NotNull CaseLocation location) {
        this.platform = platform;
        this.player = player;
        this.caseData = caseData;
        this.location = location;
        this.temporary = caseData.getCaseGui().clone();
        this.inventory = platform.getTools().createInventory(temporary.getSize(), DCTools.rc(setPlaceholders(temporary.getTitle())));

        load().thenAccept((loaded) -> {
            platform.getScheduler().run(platform, () -> player.openInventory(inventory.getInventory()), 0L);
            startUpdateTask();
        }).exceptionally(ex -> {
            platform.getLogger().log(Level.WARNING, "GUI loading failed: " + ex.getMessage());
            player.sendMessage(DCTools.rc("&cFailed to load the GUI. Please try again later."));
            return null;
        });
    }

    private CompletableFuture<Void> load() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        platform.getScheduler().async(platform, () -> {
            globalHistoryData = DCTools.sortHistoryDataByDate(platform.getAPI().getDatabase().getCache());
            for (CaseGui.Item item : temporary.getItems().values()) {
                try {
                    processItem(item);
                } catch (TypedItemException e) {
                    platform.getLogger().log(Level.WARNING,
                            "Error occurred while loading item: " + item.getNode().key(), e);
                }
            }
            future.complete(null);
        }, 0L);

        SCHEDULER.schedule(() -> {
            if (!future.isDone()) {
                future.completeExceptionally(new TimeoutException("GUI loading timed out"));
            }
        }, 5, TimeUnit.SECONDS);

        return future;
    }

    private void updateMeta(CaseGui.Item temp) {
        CaseDataMaterial original = getOriginal((String) temp.getNode().key());
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
            platform.getScheduler().async(platform,
                    (task) -> {
                        if (!platform.getAPI().getGUIManager().getMap().containsKey(player.getUniqueId())) task.cancel();
                        load();
                    }, updateRate, updateRate);
        }
    }

    private CaseDataMaterial getOriginal(String itemName) {
        return caseData.getCaseGui().getItems().get(itemName).getMaterial();
    }

    private void processItem(CaseGui.Item item) throws TypedItemException {
        String itemType = item.getType();
        if (!itemType.equalsIgnoreCase("DEFAULT")) {
            TypedItem typedItem = platform.getAPI().getGuiTypedItemManager().getFromString(itemType);
            if (typedItem != null) {
                TypedItemHandler handler = typedItem.getHandler();
                if (handler != null) item = handler.handle(this, item);
                if (typedItem.isUpdateMeta()) updateMeta(item);
            }
        } else {
            updateMeta(item);
        }

        CaseDataMaterial material = item.getMaterial();

        if (material.getItemStack() == null) material.setItemStack(platform.getTools().loadCaseItem(material.getId()));

        colorize(material);

        for (Integer slot : item.getSlots()) {
            inventory.setItem(slot, item.getMaterial().getItemStack());
        }
    }

    private String setPlaceholders(@Nullable String text) {
        if (text == null) return null;
        String caseType = caseData.getCaseType();
        return platform.getPAPI().setPlaceholders(player,
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
                    String.valueOf(platform.getAPI().getCaseKeyManager().getCache(caseType, p.getName())));
        }

        return line;
    }

    /**
     * Gets GUI Inventory
     *
     * @return inventory
     */
    @NotNull
    @Override
    public CaseInventory getInventory() {
        return inventory;
    }

    /**
     * Gets location where GUI opened
     *
     * @return GUI location
     */
    @NotNull
    @Override
    public CaseLocation getLocation() {
        return location;
    }

    /**
     * Gets player who opened GUI
     *
     * @return player who opened
     */
    @NotNull
    @Override
    public DCPlayer getPlayer() {
        return player;
    }

    /**
     * Gets GUI CaseData. Can be modified, cause this is clone of original {@link com.jodexindustries.donatecase.api.manager.CaseManager#get(String)}
     *
     * @return data
     */
    @NotNull
    @Override
    public CaseData getCaseData() {
        return caseData;
    }

    /**
     * Gets temporary GUI. Used for updating placeholders, if UpdateRate enabled
     *
     * @return GUI
     */
    @NotNull
    @Override
    public CaseGui getTemporary() {
        return temporary;
    }

    /**
     * Gets GUI global history data
     *
     * @return global history data
     */
    @NotNull
    @Override
    public List<CaseData.History> getGlobalHistoryData() {
        return globalHistoryData;
    }
}
