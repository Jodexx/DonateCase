package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemClickHandler;
import com.jodexindustries.donatecase.api.event.player.GuiClickEvent;
import org.jetbrains.annotations.NotNull;

public class PaginationHandlers {
    public static class NextPageHandler implements TypedItemClickHandler {
        @Override
        public void onClick(@NotNull GuiClickEvent event) {
            event.guiWrapper().nextPage();
            event.guiWrapper().updateInventory();
        }
    }

    public static class PrevPageHandler implements TypedItemClickHandler {
        @Override
        public void onClick(@NotNull GuiClickEvent event) {
            event.guiWrapper().prevPage();
            event.guiWrapper().updateInventory();
        }
    }
}