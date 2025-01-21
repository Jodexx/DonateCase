package com.jodexindustries.donatecase.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.caching.SimpleCache;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import com.jodexindustries.donatecase.api.manager.CaseKeyManager;

import java.util.Map;
import java.util.concurrent.CompletableFuture;



public class CaseKeyManagerImpl implements CaseKeyManager {

    /**
     * Cache map for storing number of player's keys
     */
    public final static SimpleCache<String, Map<String, Integer>> keysCache = new SimpleCache<>(20);

    private final DCAPI api;

    public CaseKeyManagerImpl(DCAPI api) {
        this.api = api;
    }

    /**
     * Set case keys for a specific player, calling an event beforehand
     *
     * @param caseType Case type
     * @param player   Player name
     * @param newKeys  New number of keys
     * @param before   Number of keys before modification
     * @return CompletableFuture of the operation's status
     */
    private CompletableFuture<DatabaseStatus> setKeysWithEvent(String caseType, String player, int newKeys, int before) {
//        KeysTransactionEvent event = new KeysTransactionEvent(caseType, player, newKeys, before);
//        Bukkit.getPluginManager().callEvent(event);
//
//        return !event.isCancelled()
//                ? api.getDatabase().setKeys(caseType, player, event.after())
//                : CompletableFuture.completedFuture(DatabaseStatus.CANCELLED);
        return api.getDatabase().setKeys(caseType, player, newKeys);
    }

    @Override
    public CompletableFuture<DatabaseStatus> setKeys(String caseType, String player, int keys) {
        return getKeysAsync(caseType, player).thenComposeAsync(before -> setKeysWithEvent(caseType, player, keys, before));
    }

    @Override
    public CompletableFuture<DatabaseStatus> modifyKeys(String caseType, String player, int keys) {
        return getKeysAsync(caseType, player)
                .thenComposeAsync(before -> setKeysWithEvent(caseType, player, before + keys, before));
    }

    @Override
    public CompletableFuture<DatabaseStatus> removeAllKeys() {
        return api.getDatabase().delAllKeys();
    }

    @Override
    public CompletableFuture<Integer> getKeysAsync(String caseType, String player) {
        return api.getDatabase().getKeys(caseType, player);
    }

    @Override
    public CompletableFuture<Map<String, Integer>> getKeysAsync(String player) {
        return api.getDatabase().getKeys(player);
    }

    @Override
    public int getKeysCache(String caseType, String player) {
        Integer keys = getKeysCache(player).get(caseType);
        if(keys == null) return 0;
        return keys;
    }

    @Override
    public Map<String, Integer> getKeysCache(String player) {
        if(api.getDatabase().getType() == DatabaseType.SQLITE) return getKeys(player);

        Map<String, Integer> keys;
        Map<String, Integer> cachedKeys = keysCache.get(player);
        if(cachedKeys == null) {
            // Get previous, if current is null
            Map<String, Integer> previous = keysCache.getPrevious(player);
            keys = previous != null ? previous : getKeys(player);

            getKeysAsync(player).thenAcceptAsync(map -> keysCache.put(player, map));
        } else {
            keys = cachedKeys;
        }

        return keys;
    }

    @Override
    public SimpleCache<String, Map<String, Integer>> getCache() {
        return keysCache;
    }
}
