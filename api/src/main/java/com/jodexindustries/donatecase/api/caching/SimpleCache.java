package com.jodexindustries.donatecase.api.caching;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SimpleCache<K, V> {


    private final Map<K, CacheEntry<V>> cache;

    private long maxAge; // Maximum time (ticks) to keep an entry

    /**
     * Default constructor
     *
     * @param maxAge in ticks
     */
    public SimpleCache(long maxAge) {
        this.cache = new HashMap<>();
        this.maxAge = maxAge;
    }

    /**
     * Get value from cache if not expired
     *
     * @param key Key for getting
     * @return Cache value
     */
    @Nullable
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null && isValid(entry)) {
            return entry.getValue();
        }
        return null;
    }

    /**
     * Get previous value from cache map, if present
     *
     * @param key Key for getting
     * @return Previous value
     */
    @Nullable
    public V getPrevious(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) return null;
        return entry.getValue();
    }

    /**
     * Put new value for key
     *
     * @param key   Key for putting
     * @param value Value for putting
     */
    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    /**
     * Setting max age of cache
     *
     * @param maxAge in ticks
     */
    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    private boolean isValid(CacheEntry<V> entry) {
        // 1000 milliseconds = 1 second = 20 ticks
        // 1000 / 20 = 50 milliseconds per tick
        return System.currentTimeMillis() - entry.getTimestamp() <= (maxAge * 50);
    }

    /**
     * Removes all the mappings from this map (optional operation).
     * The map will be empty after this call returns.
     */
    public void clear() {
        cache.clear();
    }

}