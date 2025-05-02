package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.platform.DCPlayer;

import java.util.List;

public interface CaseGuiWrapper {
    CaseInventory getInventory();
    CaseLocation getLocation();
    DCPlayer getPlayer();
    CaseData getCaseData();
    CaseGui getTemporary();
    List<CaseData.History> getGlobalHistoryData();

    default void nextPage() {
        CaseGui gui = getCaseData().caseGui();
        if (gui.getPageData().getCurrentPage() < gui.getPageData().getTotalPages() - 1) {
            gui.getPageData().setCurrentPage(gui.getPageData().getCurrentPage() + 1);
        }
    }

    default void prevPage() {
        CaseGui gui = getCaseData().caseGui();
        if (gui.getPageData().getCurrentPage() > 0) {
            gui.getPageData().setCurrentPage(gui.getPageData().getCurrentPage() - 1);
        }
    }

    default int getCurrentPage() {
        return getCaseData().caseGui().getPageData().getCurrentPage() + 1;

    default int getTotalPages() {
        return Math.max(1, getCaseData().caseGui().getPageData().getTotalPages());
    }

    void updateInventory();
}