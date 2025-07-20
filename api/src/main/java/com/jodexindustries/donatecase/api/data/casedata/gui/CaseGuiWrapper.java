package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMenu;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CaseGuiWrapper {

    @NotNull
    CaseInventory getInventory();

    @NotNull
    CaseLocation getLocation();

    @NotNull
    DCPlayer getPlayer();

    @Deprecated
    CaseData getCaseData();

    @NotNull
    CaseDefinition getDefinition();

    @Deprecated
    CaseGui getTemporary();

    @NotNull
    CaseMenu getMenu();

    @Deprecated
    List<CaseData.History> getGlobalHistoryData();
}
