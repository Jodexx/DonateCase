package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import com.jodexindustries.donatecase.api.manager.CaseOpenManager;
import com.jodexindustries.donatecase.api.scheduler.DCFuture;

import java.util.Map;

public class CaseOpenManagerImpl extends CaseOpenManager {

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
    public DCFuture<Integer> getAsync(String caseType, String player) {
        return api.getDatabase().getOpenCount(player, caseType);
    }

    @Override
    public DCFuture<Map<String, Integer>> getAsync(String player) {
        return api.getDatabase().getOpenCount(player);
    }

    @Override
    public DCFuture<Map<String, Map<String, Integer>>> getGlobalAsync() {
        return api.getDatabase().getGlobalOpenCount();
    }

    @Override
    public DCFuture<Map<String, Integer>> getGlobalAsync(String caseType) {
        return api.getDatabase().getGlobalOpenCount(caseType);
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
        Map<String, Integer> cachedCount = cache.get(player);
        if (cachedCount == null) {
            Map<String, Integer> previous = cache.getPrevious(player);
            count = previous != null ? previous : get(player);

            getAsync(player).thenAcceptAsync(map -> cache.put(player, map));
        } else {
            count = cachedCount;
        }

        return count;
    }

    @Override
    public DCFuture<DatabaseStatus> set(String caseType, String player, int openCount) {
        return api.getDatabase().setCount(caseType, player, openCount);
    }

    @Override
    public DCFuture<DatabaseStatus> add(String caseType, String player, int openCount) {
        return getAsync(caseType, player).thenComposeAsync(integer -> set(caseType, player, integer + openCount));
    }

}