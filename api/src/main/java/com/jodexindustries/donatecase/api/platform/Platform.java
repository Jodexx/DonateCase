package com.jodexindustries.donatecase.api.platform;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.casedata.MetaUpdater;
import com.jodexindustries.donatecase.api.data.storage.CaseWorld;
import com.jodexindustries.donatecase.api.scheduler.Scheduler;
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

    void dispatchConsoleCommand(@NotNull String command);

    DCTools getTools();

    PAPI getPAPI();

    MetaUpdater getMetaUpdater();

    DCAPI getAPI();

    @NotNull Scheduler getScheduler();

    @Nullable DCPlayer getPlayer(String name);

    DCPlayer[] getOnlinePlayers();

    DCOfflinePlayer[] getOfflinePlayers();

    @Nullable CaseWorld getWorld(String world);

    boolean isWorldLoaded(String world);

    int getSpawnRadius();

    /**
     * Checks that a sound name is known to the platform.
     * <p>
     * Used to validate a sound before it is played to several players at once,
     * so that an invalid name fails before anyone hears it.
     *
     * @param sound the sound name to check.
     * @return true if the sound can be played, false otherwise.
     */
    default boolean isValidSound(@NotNull String sound) {
        return true;
    }
}
