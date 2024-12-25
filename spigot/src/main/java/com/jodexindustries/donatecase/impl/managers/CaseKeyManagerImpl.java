package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.api.caching.SimpleCache;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import com.jodexindustries.donatecase.api.events.KeysTransactionEvent;
import com.jodexindustries.donatecase.api.manager.CaseKeyManager;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.jodexindustries.donatecase.DonateCase.instance;


public class CaseKeyManagerImpl implements CaseKeyManager {

    /**
     * Cache map for storing number of player's keys
     */
    public final static SimpleCache<String, Map<String, Integer>> keysCache = new SimpleCache<>(20);

    /**
     * Set case keys for a specific player, calling an event beforehand
     *
     * @param caseType Case type
     * @param player   Player name
     * @param newKeys  New number of keys
     * @param before   Number of keys before modification
     * @return CompletableFuture of the operation's status
     */
    private static CompletableFuture<DatabaseStatus> setKeysWithEvent(String caseType, String player, int newKeys, int before) {
        KeysTransactionEvent event = new KeysTransactionEvent(caseType, player, newKeys, before);
        Bukkit.getPluginManager().callEvent(event);

        return !event.isCancelled()
                ? instance.database.setKeys(caseType, player, event.after())
                : CompletableFuture.completedFuture(DatabaseStatus.CANCELLED);
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
        return instance.database.delAllKeys();
    }

    @Override
    public int getKeys(String caseType, String player) {
        return getKeysAsync(caseType, player).join();
    }

    @Override
    public Map<String, Integer> getKeys(String player) {
        return getKeysAsync(player).join();
    }

    @Override
    public CompletableFuture<Integer> getKeysAsync(String caseType, String player) {
        return instance.database.getKeys(caseType, player);
    }

    @Override
    public CompletableFuture<Map<String, Integer>> getKeysAsync(String player) {
        return instance.database.getKeys(player);
    }

    @Override
    public int getKeysCache(String caseType, String player) {
        return getKeysCache(player).get(caseType);
    }

    @Override
    public Map<String, Integer> getKeysCache(String player) {
        if(instance.config.getDatabaseType() == DatabaseType.SQLITE) return getKeys(player);

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
