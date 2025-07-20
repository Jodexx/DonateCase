package com.jodexindustries.donatecase.api.data.casedata.gui;

import org.jetbrains.annotations.Nullable;

public interface CaseInventory {

    @Deprecated
    default Object getInventory() {
        return getHandle();
    }

    Object getHandle();

    void setItem(int index, @Nullable Object item);
}
