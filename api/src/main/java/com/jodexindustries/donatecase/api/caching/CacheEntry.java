package com.jodexindustries.donatecase.api.caching;

import lombok.Getter;

@Getter
public class CacheEntry<V> {

    private final V value;
    private final long timestamp;

    public CacheEntry(V value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

}