package com.jodexindustries.donatecase.api.event;

import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.jetbrains.annotations.NotNull;

public interface GUIClickEvent {

    @NotNull
    DCPlayer getPlayer();

    @NotNull
    CaseGuiWrapper getCaseGUI();

    @NotNull
    String getItemType();
}
