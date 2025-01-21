package com.jodexindustries.donatecase.api.platform;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.manager.HologramManager;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.api.tools.PAPI;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public interface Platform extends Addon {

    String getName();

    String getIdentifier();

    String getVersion();

    Logger getLogger();

    DCTools getTools();

    PAPI getPAPI();

    HologramManager getHologramManager();

    DCAPI getAPI();

    void runSync(@NotNull Runnable task);

    DCPlayer[] getOnlinePlayers();

    DCOfflinePlayer[] getOfflinePlayers();
}
