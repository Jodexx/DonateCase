package com.jodexindustries.donatecase.api.caching;

import com.jodexindustries.donatecase.api.caching.entry.CacheEntry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SimpleCache<K, V> {


    private final Map<K, CacheEntry<V>> cache;

    private long maxAge; // Maximum time (ticks) to keep an entry

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

    public V getPrevious(K key) {
        CacheEntry<V> entry = cache.get(key);
        if(entry == null) return null;
        return entry.getValue();
    }

    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    private boolean isValid(CacheEntry<V> entry) {
        // 1000 milliseconds = 1 second = 20 ticks
        // 1000 / 20 = 50 milliseconds per tick
        return System.currentTimeMillis() - entry.getTimestamp() <= (maxAge * 50);
    }

}