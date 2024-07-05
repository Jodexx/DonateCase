package com.jodexindustries.donatecase.tools.caching;

import com.jodexindustries.donatecase.tools.caching.entry.CacheEntry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SimpleCache<K, V> {


    private final Map<K, CacheEntry<V>> cache;
    private final long maxAge; // Maximum time (milliseconds) to keep an entry

    public SimpleCache(long maxAge) {
        this.cache = new HashMap<>();
        this.maxAge = maxAge;
    }

    @Nullable
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null && isValid(entry)) {
            return entry.getValue();
        }
        return null;
    }

    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    private boolean isValid(CacheEntry<V> entry) {
        return System.currentTimeMillis() - entry.getTimestamp() <= maxAge;
    }

}