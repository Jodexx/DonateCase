package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.api.caching.SimpleCache;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import com.jodexindustries.donatecase.api.manager.CaseOpenManager;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.jodexindustries.donatecase.DonateCase.instance;

public class CaseOpenManagerImpl implements CaseOpenManager {

    /**
     * Cache map for storing number of player's cases opens
     */
    public final static SimpleCache<String, Map<String, Integer>> openCache = new SimpleCache<>(20);

    @Override
    public int getOpenCount(String caseType, String player) {
        return getOpenCountAsync(caseType, player).join();
    }

    @Override
    public Map<String, Integer> getOpenCount(String player) {
        return getOpenCountAsync(player).join();
    }

    @Override
    public CompletableFuture<Integer> getOpenCountAsync(String caseType, String player) {
        return instance.database.getOpenCount(player, caseType);
    }

    @Override
    public CompletableFuture<Map<String, Integer>> getOpenCountAsync(String player) {
        return instance.database.getOpenCount(player);
    }

    @Override
    public int getOpenCountCache(String caseType, String player) {
        Integer count = getOpenCountCache(player).get(caseType);
        if (count == null) return 0;
        return count;
    }

    @Override
    public Map<String, Integer> getOpenCountCache(String player) {
        if (instance.config.getDatabaseType() == DatabaseType.SQLITE) return getOpenCount(player);

        Map<String, Integer> count;
        Map<String, Integer> cachedCount = openCache.get(player);
        if (cachedCount == null) {
            Map<String, Integer> previous = openCache.getPrevious(player);
            count = previous != null ? previous : getOpenCount(player);

            getOpenCountAsync(player).thenAcceptAsync(map -> openCache.put(player, map));
        } else {
            count = cachedCount;
        }

        return count;
    }

    @Override
    public CompletableFuture<DatabaseStatus> setOpenCount(String caseType, String player, int openCount) {
        return instance.database.setCount(caseType, player, openCount);
    }

    @Override
    public CompletableFuture<DatabaseStatus> addOpenCount(String caseType, String player, int openCount) {
        return getOpenCountAsync(caseType, player).thenComposeAsync(integer -> setOpenCount(caseType, player, integer + openCount));
    }

    @Override
    public SimpleCache<String, Map<String, Integer>> getCache() {
        return openCache;
    }
}