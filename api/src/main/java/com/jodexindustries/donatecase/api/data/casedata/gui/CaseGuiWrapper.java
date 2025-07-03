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
}
