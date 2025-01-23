package com.jodexindustries.donatecase.api.platform;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.casedata.MetaUpdater;
import com.jodexindustries.donatecase.api.manager.HologramManager;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.api.tools.PAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public interface Platform extends Addon {

    String getName();

    String getIdentifier();

    String getVersion();

    Logger getLogger();

    DCTools getTools();

    PAPI getPAPI();

    @Nullable HologramManager getHologramManager();

    MetaUpdater getMetaUpdater();

    DCAPI getAPI();

    void runSync(@NotNull Runnable task);

    @Nullable DCPlayer getPlayer(String name);

    DCPlayer[] getOnlinePlayers();

    DCOfflinePlayer[] getOfflinePlayers();

    boolean isWorldLoaded(String world);
}
