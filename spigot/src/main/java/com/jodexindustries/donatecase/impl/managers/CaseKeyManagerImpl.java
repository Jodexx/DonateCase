package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.api.caching.SimpleCache;
import com.jodexindustries.donatecase.api.caching.entry.InfoEntry;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import com.jodexindustries.donatecase.api.events.KeysTransactionEvent;
import com.jodexindustries.donatecase.api.manager.CaseKeyManager;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;

import static com.jodexindustries.donatecase.DonateCase.instance;


public class CaseKeyManagerImpl implements CaseKeyManager {

    /**
     * Cache map for storing number of player's keys
     */
    public final static SimpleCache<InfoEntry, Integer> keysCache = new SimpleCache<>(20);

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
    public CompletableFuture<DatabaseStatus> addKeys(String caseType, String player, int keys) {
        return modifyKeys(caseType, player, keys);
    }

    @Override
    public CompletableFuture<DatabaseStatus> removeKeys(String caseType, String player, int keys) {
        return modifyKeys(caseType, player, -keys);
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
    public CompletableFuture<Integer> getKeysAsync(String caseType, String player) {
        return instance.database.getKeys(caseType, player);
    }

    @Override
    public int getKeysCache(String caseType, String player) {
        if(instance.config.getDatabaseType() == DatabaseType.SQLITE) return getKeys(caseType, player);

        int keys;
        InfoEntry entry = new InfoEntry(player, caseType);
        Integer cachedKeys = keysCache.get(entry);
        if(cachedKeys == null) {
            // Get previous, if current is null
            Integer previous = keysCache.getPrevious(entry);
            keys = previous != null ? previous : getKeys(caseType, player);

            getKeysAsync(caseType, player).thenAcceptAsync(integer -> keysCache.put(entry, integer));
        } else {
            keys = cachedKeys;
        }
        return keys;
    }

    @Override
    public SimpleCache<InfoEntry, Integer> getCache() {
        return keysCache;
    }
}
