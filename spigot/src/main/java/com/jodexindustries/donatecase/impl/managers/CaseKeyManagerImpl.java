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

    /**
     * Directly set case keys to a specific player (bypassing addition/subtraction)
     *
     * @param caseType Case type
     * @param player   Player name
     * @param keys     Number of keys
     * @return CompletableFuture of completion status
     */
    @Override
    public CompletableFuture<DatabaseStatus> setKeys(String caseType, String player, int keys) {
        return getKeysAsync(caseType, player).thenComposeAsync(before -> setKeysWithEvent(caseType, player, keys, before));
    }

    /**
     * Modify case keys for a specific player
     *
     * @param caseType Case type
     * @param player   Player name
     * @param keys     Number of keys to modify (positive to add, negative to remove)
     * @return Completable future of completion status
     */
    @Override
    public CompletableFuture<DatabaseStatus> modifyKeys(String caseType, String player, int keys) {
        return getKeysAsync(caseType, player)
                .thenComposeAsync(before -> setKeysWithEvent(caseType, player, before + keys, before));
    }

    /**
     * Add case keys to a specific player (async)
     *
     * @param caseType Case type
     * @param player   Player name
     * @param keys     Number of keys
     * @return Completable future of completes
     * @see #modifyKeys(String, String, int)
     */
    @Override
    public CompletableFuture<DatabaseStatus> addKeys(String caseType, String player, int keys) {
        return modifyKeys(caseType, player, keys);
    }

    /**
     * Delete case keys for a specific player (async)
     *
     * @param caseType Case name
     * @param player   Player name
     * @param keys     Number of keys
     * @return Completable future of completes
     * @see #modifyKeys(String, String, int)
     */
    @Override
    public CompletableFuture<DatabaseStatus> removeKeys(String caseType, String player, int keys) {
        return modifyKeys(caseType, player, -keys);
    }

    /**
     * Delete all keys
     */
    @Override
    public CompletableFuture<DatabaseStatus> removeAllKeys() {
        return instance.database.delAllKeys();
    }

    /**
     * Get the keys to a certain player's case
     * @param caseType Case type
     * @param player Player name
     * @return Number of keys
     */
    @Override
    public int getKeys(String caseType, String player) {
        return getKeysAsync(caseType, player).join();
    }

    /**
     * Get the keys to a certain player's case
     * @param caseType Case type
     * @param player Player name
     * @return CompletableFuture of keys
     */
    @Override
    public CompletableFuture<Integer> getKeysAsync(String caseType, String player) {
        return instance.database.getKeys(caseType, player);
    }

    /**
     * Get the keys to a certain player's case from cache <br/>
     * Returns no-cached, if mysql disabled
     * @param caseType Case type
     * @param player Player name
     * @return Number of keys
     * @since 2.2.3.8
     */
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
}
