package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import com.jodexindustries.donatecase.api.manager.CaseOpenManager;
import com.jodexindustries.donatecase.common.database.CaseDatabaseImpl;
import com.jodexindustries.donatecase.common.database.entities.OpenInfoTable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<DatabaseStatus> set(String caseType, String player, int openCount) {
        return api.getDatabase().setCount(caseType, player, openCount);
    }

    @Override
    public CompletableFuture<DatabaseStatus> add(String caseType, String player, int openCount) {
        return getAsync(caseType, player).thenComposeAsync(integer -> set(caseType, player, integer + openCount));
    }

    @Override
    public int getGlobalOpenCount() {
        int sum = 0;
        for (OpenInfoTable entry : ((CaseDatabaseImpl) api.getDatabase()).getAllOpenInfo()) {
            sum += entry.getCount();
        }
        return sum;
    }

    @Override
    public int getGlobalOpenCount(String caseType) {
        int sum = 0;
        for (OpenInfoTable entry : ((CaseDatabaseImpl) api.getDatabase()).getAllOpenInfo()) {
            if (caseType.equals(entry.getCaseType())) {
                sum += entry.getCount();
            }
        }
        return sum;
    }

}