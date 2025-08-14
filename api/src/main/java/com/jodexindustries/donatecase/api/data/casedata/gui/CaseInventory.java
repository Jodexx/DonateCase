package com.jodexindustries.donatecase.api.data.casedata.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CaseInventory {

    Object getHandle();

    void setItem(int index, @Nullable Object item);

    @NotNull
    CaseGuiWrapper getWrapper();
}
