package com.jodexindustries.donatecase.api.data.casedata.gui;

import java.util.HashMap;
import java.util.Map;

public class GuiRenderer {
    public void render(CaseGuiWrapper wrapper) {
        CaseGui gui = wrapper.getCaseData().caseGui();
        CaseInventory inventory = wrapper.getInventory();

        clearInventory(inventory, gui.size());
        addPageItems(wrapper, gui, inventory);
        addPaginationButtons(wrapper, gui, inventory);
    }

    private void clearInventory(CaseInventory inventory, int size) {
        for (int i = 0; i < size; i++) {
            inventory.setItem(i, null);
        }
    }

    private void addPageItems(CaseGuiWrapper wrapper, CaseGui gui, CaseInventory inventory) {
        Map<String, CaseGui.Item> currentItems = gui.getPageData().getPages()
                .getOrDefault(gui.getPageData().getCurrentPage(), new HashMap<>());

        currentItems.values().forEach(item ->
                item.slots().forEach(slot ->
                        inventory.setItem(slot, createItemStack(item))
                )
        );
    }

    private void addPaginationButtons(CaseGuiWrapper wrapper, CaseGui gui, CaseInventory inventory) {
        gui.getPaginationSlots().forEach(slot -> inventory.setItem(slot, null));

        if (gui.getItems() != null) {
            addButtonIfExists(inventory, "prev_page", gui);
            addButtonIfExists(inventory, "next_page", gui);
            addPageInfoButton(wrapper, gui, inventory);
        }
    }

    private void addButtonIfExists(CaseInventory inventory, String buttonType, CaseGui gui) {
        CaseGui.Item button = gui.getItems().get(buttonType);
        if (button != null) {
            button.slots().forEach(slot ->
                    inventory.setItem(slot, createItemStack(button))
            );
        }
    }

    private void addPageInfoButton(CaseGuiWrapper wrapper, CaseGui gui, CaseInventory inventory) {
        CaseGui.Item pageInfoItem = gui.getItems().get("page_info");
        if (pageInfoItem != null) {
            final CaseGui.Item updatedPageInfoItem = updatePageInfoItem(pageInfoItem, wrapper);
            updatedPageInfoItem.slots().forEach(slot ->
                    inventory.setItem(slot, createItemStack(updatedPageInfoItem))
            );
        }
    }


    private CaseGui.Item updatePageInfoItem(CaseGui.Item item, CaseGuiWrapper wrapper) {
        CaseGui.Item cloned = item.clone();
        cloned.material().displayName(
                cloned.material().displayName()
                        .replace("{page}", String.valueOf(wrapper.getCurrentPage()))
                        .replace("{total}", String.valueOf(wrapper.getTotalPages()))
        );
        return cloned;
    }

    private Object createItemStack(CaseGui.Item item) {
        return item.material().itemStack();
    }
}