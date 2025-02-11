package com.jodexindustries.donatecase.api.data.casedata.gui;

import org.jetbrains.annotations.Nullable;

public interface CaseInventory {

    Object getInventory();

    void setItem(int index, @Nullable Object item);
}
