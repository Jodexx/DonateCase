package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import com.jodexindustries.donatecase.api.event.plugin.KeysTransactionEvent;
import com.jodexindustries.donatecase.api.manager.CaseKeyManager;
import com.jodexindustries.donatecase.api.scheduler.DCFuture;

import java.util.Map;


public class CaseKeyManagerImpl extends CaseKeyManager {

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
     * @return DCFuture of the operation's status
     */
    private DCFuture<DatabaseStatus> setKeysWithEvent(String caseType, String player, int newKeys, int before) {
        KeysTransactionEvent event = new KeysTransactionEvent(caseType, player, newKeys, before);
        api.getEventBus().post(event);

        return !event.cancelled()
                ? api.getDatabase().setKeys(caseType, player, event.after())
                : DCFuture.completedFuture(DatabaseStatus.CANCELLED);
    }

    @Override
    public DCFuture<DatabaseStatus> set(String caseType, String player, int keys) {
        return getAsync(caseType, player).thenComposeAsync(before -> setKeysWithEvent(caseType, player, keys, before));
    }

    @Override
    public DCFuture<DatabaseStatus> modify(String caseType, String player, int keys) {
        return getAsync(caseType, player)
                .thenComposeAsync(before -> setKeysWithEvent(caseType, player, before + keys, before));
    }

    @Override
    public DCFuture<DatabaseStatus> delete() {
        return api.getDatabase().delAllKeys();
    }

    @Override
    public DCFuture<DatabaseStatus> delete(String caseType) {
        return api.getDatabase().delKeys(caseType);
    }

    @Override
    public DCFuture<Integer> getAsync(String caseType, String player) {
        return api.getDatabase().getKeys(caseType, player);
    }

    @Override
    public DCFuture<Map<String, Integer>> getAsync(String player) {
        return api.getDatabase().getKeys(player);
    }

    @Override
    public int getCache(String caseType, String player) {
        Integer keys = getCache(player).get(caseType);
        if(keys == null) return 0;
        return keys;
    }

    @Override
    public Map<String, Integer> getCache(String player) {
        if(api.getDatabase().getType() == DatabaseType.SQLITE) return get(player);

        Map<String, Integer> keys;
        Map<String, Integer> cachedKeys = cache.get(player);
        if(cachedKeys == null) {
            // Get previous, if current is null
            Map<String, Integer> previous = cache.getPrevious(player);
            keys = previous != null ? previous : get(player);

            getAsync(player).thenAcceptAsync(map -> cache.put(player, map));
        } else {
            keys = cachedKeys;
        }

        return keys;
    }

}
