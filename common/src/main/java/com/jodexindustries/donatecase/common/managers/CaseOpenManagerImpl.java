package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.caching.SimpleCache;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import com.jodexindustries.donatecase.api.manager.CaseOpenManager;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CaseOpenManagerImpl implements CaseOpenManager {

    public final static SimpleCache<String, Map<String, Integer>> openCache = new SimpleCache<>(20);
    
    private final DCAPI api;

    public CaseOpenManagerImpl(DCAPI api) {
        this.api = api;
    }

    @Override
    public int get(String caseType, String player) {
        return getAsync(caseType, player).join();
    }

    @Override
    public Map<String, Integer> get(String player) {
        return getAsync(player).join();
    }

    @Override
    public CompletableFuture<Integer> getAsync(String caseType, String player) {
        return api.getDatabase().getOpenCount(player, caseType);
    }

    @Override
    public CompletableFuture<Map<String, Integer>> getAsync(String player) {
        return api.getDatabase().getOpenCount(player);
    }

    @Override
    public int getCache(String caseType, String player) {
        Integer count = getCache(player).get(caseType);
        if (count == null) return 0;
        return count;
    }

    @Override
    public Map<String, Integer> getCache(String player) {
        if (api.getDatabase().getType() == DatabaseType.SQLITE) return get(player);

        Map<String, Integer> count;
        Map<String, Integer> cachedCount = openCache.get(player);
        if (cachedCount == null) {
            Map<String, Integer> previous = openCache.getPrevious(player);
            count = previous != null ? previous : get(player);

            getAsync(player).thenAcceptAsync(map -> openCache.put(player, map));
        } else {
            count = cachedCount;
        }

        return count;
    }

    @Override
    public CompletableFuture<DatabaseStatus> set(String caseType, String player, int openCount) {
        return api.getDatabase().setCount(caseType, player, openCount);
    }

    @Override
    public CompletableFuture<DatabaseStatus> add(String caseType, String player, int openCount) {
        return getAsync(caseType, player).thenComposeAsync(integer -> set(caseType, player, integer + openCount));
    }

    @Override
    public SimpleCache<String, Map<String, Integer>> getCache() {
        return openCache;
    }
}