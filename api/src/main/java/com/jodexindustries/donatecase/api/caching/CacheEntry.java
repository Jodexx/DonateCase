package com.jodexindustries.donatecase.api.caching;

public class CacheEntry<V> {

    private final V value;
    private final long timestamp;

    public CacheEntry(V value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public V getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "CacheEntry{" +
                "value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}