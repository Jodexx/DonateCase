package com.jodexindustries.donatecase.api.platform;

import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.data.storage.CaseWorld;
import org.jetbrains.annotations.NotNull;

public interface DCPlayer extends DCCommandSender, DCOfflinePlayer {

    @NotNull String getName();

    CaseWorld getWorld();

    CaseLocation getLocation();

    CaseLocation getTargetBlock(int maxDistance);

    void openInventory(Object inventory);

    void closeInventory();
}
