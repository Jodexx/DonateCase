package com.jodexindustries.donatecase.api.platform;

import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import org.jetbrains.annotations.NotNull;

public interface DCPlayer extends DCOfflinePlayer {

    @NotNull String getName();

    CaseLocation getTargetBlock(int maxDistance);

    void openInventory(Object inventory);

    void closeInventory();
}
