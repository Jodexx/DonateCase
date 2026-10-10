package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.event.plugin.KeysTransactionEvent;
import com.jodexindustries.donatecase.api.manager.CaseKeyManager;
import com.jodexindustries.donatecase.api.scheduler.DCFuture;

import java.util.Map;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public class CaseKeyManagerImpl extends CaseKeyManager {

    private final DCAPI api;

    public CaseKeyManagerImpl(DCAPI api) {
        this.api = api;
    }

    private DCFuture<DatabaseStatus> transaction(String caseType, String player,
                                                 IntUnaryOperator afterFn,
                                                 Function<KeysTransactionEvent, DCFuture<DatabaseStatus>> write) {
        return getAsync(caseType, player).thenComposeAsync(before -> {
            KeysTransactionEvent event =
                    new KeysTransactionEvent(caseType, player, afterFn.applyAsInt(before), before);
            api.getEventBus().post(event);

            if (event.cancelled()) return DCFuture.completedFuture(DatabaseStatus.CANCELLED);

            DCFuture<DatabaseStatus> result = write.apply(event);
            result.thenAccept(status -> {
                if (status == DatabaseStatus.COMPLETE) refreshCache(player);
            });
            return result;
        });
    }

    @Override
    public DCFuture<DatabaseStatus> set(String caseType, String player, int keys) {
        return transaction(caseType, player,
                before -> keys,
                event -> api.getDatabase().setKeys(caseType, player, event.after()));
    }

    public DCFuture<DatabaseStatus> add(String caseType, String player, int amount) {
        if (amount <= 0) return DCFuture.completedFuture(DatabaseStatus.FAIL);

        return transaction(caseType, player,
                before -> before + amount,
                event -> api.getDatabase().addKeys(caseType, player, amount));
    }

    @Override
    public DCFuture<DatabaseStatus> take(String caseType, String player, int amount) {
        if (amount <= 0) return DCFuture.completedFuture(DatabaseStatus.FAIL);

        return transaction(caseType, player,
                before -> before - amount,
                event -> api.getDatabase().takeKeys(caseType, player, amount));
    }

    @Override
    public DCFuture<DatabaseStatus> modify(String caseType, String player, int keys) {
        return keys >= 0 ? add(caseType, player, keys) : take(caseType, player, -keys);
    }

    @Override
    public DCFuture<DatabaseStatus> remove(String caseType, String player, int amount) {
        return take(caseType, player, amount);
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
        return getCache(player).getOrDefault(caseType, 0);
    }

    @Override
    public Map<String, Integer> getCache(String player) {
        if (api.getDatabase().getType().noCache()) return get(player);

        Map<String, Integer> cached = cache.get(player);
        if (cached != null) return cached;

        Map<String, Integer> previous = cache.getPrevious(player);
        Map<String, Integer> result = previous != null ? previous : get(player);
        refreshCache(player);
        return result;
    }

    private void refreshCache(String player) {
        if (api.getDatabase().getType().noCache()) return;
        getAsync(player).thenAcceptAsync(map -> cache.put(player, map));
    }
}