package com.jodexindustries.velocity.database;

import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public final class VelocitySimpleCache<K, V> {

    private final Map<K, CacheEntry<V>> cache = new HashMap<>();
    @Setter
    private long maxAge; // ticks

    public VelocitySimpleCache(long maxAge) {
        this.maxAge = maxAge;
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null && isValid(entry)) {
            return entry.value;
        }
        return null;
    }

    public V getPrevious(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) return null;
        return entry.value;
    }

    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    public void clear() {
        cache.clear();
    }

    private boolean isValid(CacheEntry<V> entry) {
        return System.currentTimeMillis() - entry.timestamp <= (maxAge * 50);
    }

    private record CacheEntry<V>(V value, long timestamp) {
    }
}
