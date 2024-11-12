package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.api.caching.entry.InfoEntry;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import com.jodexindustries.donatecase.api.manager.CaseOpenManager;

import java.util.concurrent.CompletableFuture;

public class CaseOpenManagerImpl implements CaseOpenManager {

    /**
     * Get count of opened cases by player
     * @param caseType Case type
     * @param player Player, who opened
     * @return opened count
     */
    public int getOpenCount(String caseType, String player) {
        return getOpenCountAsync(caseType, player).join();
    }

    /**
     * Get count of opened cases by player
     * @param caseType Case type
     * @param player Player, who opened
     * @return CompletableFuture of open count
     */
    public CompletableFuture<Integer> getOpenCountAsync(String caseType, String player) {
        return getDatabase().getOpenCount(player, caseType);
    }

    /**
     * Get count of opened cases by player from cache <br/>
     * Returns no-cached, if mysql disabled
     * @param caseType Case type
     * @param player Player, who opened
     * @return opened count
     * @since 2.2.3.8
     */
    public int getOpenCountCache(String caseType, String player) {
        if(getConfig().getDatabaseType() == DatabaseType.SQLITE) return getOpenCount(caseType, player);

        int openCount;
        InfoEntry entry = new InfoEntry(player, caseType);
        Integer cachedKeys = openCache.get(entry);
        if(cachedKeys == null) {
            getOpenCountAsync(caseType, player).thenAcceptAsync(integer -> openCache.put(entry, integer));
            // Get previous, if current is null
            Integer previous = keysCache.getPrevious(entry);
            openCount = previous != null ? previous : getOpenCount(caseType, player);
        } else {
            openCount = cachedKeys;
        }
        return openCount;
    }

    /**
     * Set case keys to a specific player (async)
     *
     * @param caseType  Case type
     * @param player    Player name
     * @param openCount Opened count
     * @return Completable future of completes
     * @since 2.2.4.4
     */
    public CompletableFuture<DatabaseStatus> setOpenCount(String caseType, String player, int openCount) {
        return getDatabase().setCount(caseType, player, openCount);
    }

    /**
     * Add count of opened cases by player (async)
     *
     * @param caseType  Case type
     * @param player    Player name
     * @param openCount Opened count
     * @return Completable future of completes
     * @since 2.2.4.4
     */
    public CompletableFuture<DatabaseStatus> addOpenCount(String caseType, String player, int openCount) {
        return getOpenCountAsync(caseType, player).thenComposeAsync(integer -> setOpenCount(caseType, player, integer + openCount));
    }
}
