package com.jodexindustries.donatecase.common.gui;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseInventory;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemException;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemHandler;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMaterial;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMenu;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.platform.Platform;
import com.jodexindustries.donatecase.api.scheduler.DCFuture;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.command.sub.KeysCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

// TODO must be updated with JGuiWrapper
public class CaseGuiWrapperImpl implements CaseGuiWrapper {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    protected final Platform platform;
    protected final DCPlayer player;
    protected final CaseDefinition definition;
    protected final CaseLocation location;
    protected final CaseMenu menu;
    protected final CaseMenu temporary;
    protected final CaseInventory inventory;

    private List<CaseData.History> globalHistoryData;

    public CaseGuiWrapperImpl(@NotNull Platform platform,
                              @NotNull DCPlayer player,
                              @NotNull CaseDefinition definition,
                              @NotNull CaseMenu caseMenu,
                              @NotNull CaseLocation location) {
        this.platform = platform;
        this.player = player;
        this.definition = definition;
        this.location = location;
        this.menu = caseMenu;
        this.temporary = menu.clone();
        this.inventory = platform.getTools().createInventory(this, temporary.size(),
                DCTools.rc(setPlaceholders(temporary.title())));

        load().thenAcceptSync((loaded) -> {
            player.openInventory(inventory.getHandle());
            startUpdateTask();
        }).exceptionally(ex -> {
            platform.getLogger().log(Level.WARNING, "GUI loading failed: " + ex.getMessage());
            player.sendMessage(DCTools.rc("&cFailed to load the GUI. Please try again later."));
            return null;
        });
    }

    private DCFuture<Void> load() {
        DCFuture<Void> future = new DCFuture<>();

        platform.getScheduler().async(platform, () -> {
            globalHistoryData = DCTools.sortHistoryDataByDate(platform.getAPI().getDatabase().getCache());
            for (CaseMenu.Item item : temporary.items().values()) {
                platform.getScheduler().async(platform, () -> {
                    try {
                        processItem(item);
                    } catch (TypedItemException e) {
                        platform.getLogger().log(Level.WARNING,
                                "Error occurred while loading item: " + item.name(), e);
                    }
                }, 0L);
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

    private void updateMeta(CaseMenu.Item temp) {
        CaseMaterial original = getOriginal(temp.name());
        CaseMaterial material = temp.material();
        material.displayName(setPlaceholders(original.displayName()));
        material.lore(setPlaceholders(original.lore()));
        material.updateMeta();
    }

    private void startUpdateTask() {
        int updateRate = temporary.updateRate();
        if (updateRate >= 0) {
            platform.getScheduler().async(platform,
                    (task) -> {
                        if (!platform.getAPI().getGUIManager().getMap().containsKey(player.getUniqueId())) {
                            task.cancel();
                            return;
                        }
                        load();
                    }, updateRate, updateRate);
        }
    }

    private CaseMaterial getOriginal(String itemName) {
        return menu.items().get(itemName).material();
    }

    private void processItem(CaseMenu.Item item) throws TypedItemException {
        String itemType = item.type();
        if (!itemType.equalsIgnoreCase("DEFAULT")) {
            Optional<TypedItem> typedItem = platform.getAPI().getGuiTypedItemManager().getFromString(itemType);
            if (typedItem.isPresent()) {
                TypedItemHandler handler = typedItem.get().handler();
                if (handler != null)
                    item = handler.handle(this, item);
                if (typedItem.get().updateMeta())
                    updateMeta(item);
            }
        } else {
            updateMeta(item);
        }

        CaseMaterial material = item.material();

        if (material.itemStack() == null)
            material.itemStack(platform.getTools().loadCaseItem(material.id()));

        material.updateMeta();

        for (Integer slot : item.slots()) {
            inventory.setItem(slot, item.material().itemStack());
        }
    }

    private String setPlaceholders(@Nullable String text) {
        if (text == null)
            return null;
        String caseType = definition.settings().type();
        text = platform.getPAPI().setPlaceholders(
                player,
                text);
        return KeysCommand.formatMessage(player.getName(), text.replace("%casetype%", caseType), true, caseType);
    }

    private List<String> setPlaceholders(List<String> lore) {
        return lore.stream().map(this::setPlaceholders).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public CaseInventory getInventory() {
        return inventory;
    }

    @NotNull
    @Override
    public CaseLocation getLocation() {
        return location;
    }

    @NotNull
    @Override
    public DCPlayer getPlayer() {
        return player;
    }

    @Deprecated
    @NotNull
    @Override
    public CaseData getCaseData() {
        return CaseData.fromDefinition(definition);
    }

    @Override
    public @NotNull CaseDefinition getDefinition() {
        return definition;
    }

    @Deprecated
    @Override
    public CaseGui getTemporary() {
        return CaseGui.fromMenu(menu);
    }

    @NotNull
    @Override
    public CaseMenu getMenu() {
        return temporary;
    }

    @NotNull
    @Override
    public List<CaseData.History> getGlobalHistoryData() {
        return globalHistoryData;
    }
}
