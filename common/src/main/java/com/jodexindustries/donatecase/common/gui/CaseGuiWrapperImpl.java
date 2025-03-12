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
import com.jodexindustries.donatecase.common.command.sub.KeysCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
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
        this.temporary = caseData.caseGui().clone();
        this.inventory = platform.getTools().createInventory(temporary.size(), DCTools.rc(setPlaceholders(temporary.title())));

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
            for (CaseGui.Item item : temporary.items().values()) {
                try {
                    processItem(item);
                } catch (TypedItemException e) {
                    platform.getLogger().log(Level.WARNING,
                            "Error occurred while loading item: " + item.node().key(), e);
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
        CaseDataMaterial original = getOriginal((String) temp.node().key());
        CaseDataMaterial material = temp.material();
        material.displayName(setPlaceholders(original.displayName()));
        material.lore(setPlaceholders(original.lore()));
        material.updateMeta();
    }

    private void colorize(CaseDataMaterial material) {
        material.displayName(DCTools.rc(material.displayName()));
        material.lore(DCTools.rc(material.lore()));
        material.updateMeta();
    }

    private void startUpdateTask() {
        int updateRate = temporary.updateRate();
        if (updateRate >= 0) {
            platform.getScheduler().async(platform,
                    (task) -> {
                        if (!platform.getAPI().getGUIManager().getMap().containsKey(player.getUniqueId())) task.cancel();
                        load();
                    }, updateRate, updateRate);
        }
    }

    private CaseDataMaterial getOriginal(String itemName) {
        return caseData.caseGui().items().get(itemName).material();
    }

    private void processItem(CaseGui.Item item) throws TypedItemException {
        String itemType = item.type();
        if (!itemType.equalsIgnoreCase("DEFAULT")) {
            Optional<TypedItem> typedItem = platform.getAPI().getGuiTypedItemManager().getFromString(itemType);
            if (typedItem.isPresent()) {
                TypedItemHandler handler = typedItem.get().handler();
                if (handler != null) item = handler.handle(this, item);
                if (typedItem.get().updateMeta()) updateMeta(item);
            }
        } else {
            updateMeta(item);
        }

        CaseDataMaterial material = item.material();

        if (material.itemStack() == null) material.itemStack(platform.getTools().loadCaseItem(material.id()));

        colorize(material);

        for (Integer slot : item.slots()) {
            inventory.setItem(slot, item.material().itemStack());
        }
    }

    private String setPlaceholders(@Nullable String text) {
        if (text == null) return null;
        String caseType = caseData.caseType();
        return platform.getPAPI().setPlaceholders(
                player,
                KeysCommand.formatMessage(player.getName(), text.replace("%casetype%", caseType), true, caseType)
        );
    }

    private List<String> setPlaceholders(List<String> lore) {
        return lore.stream().map(this::setPlaceholders).collect(Collectors.toList());
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
