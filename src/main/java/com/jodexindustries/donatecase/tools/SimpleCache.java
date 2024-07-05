package com.jodexindustries.donatecase.tools;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
            return entry.value;
        }
        return null;
    }

    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    private boolean isValid(CacheEntry<V> entry) {
        return System.currentTimeMillis() - entry.timestamp <= maxAge;
    }

    public Map<K, CacheEntry<V>> getCache() {
        return cache;
    }

    public static class InfoEntry {
        private final String player;
        private final String caseType;

        public InfoEntry(String player, String caseType) {
            this.player = player;
            this.caseType = caseType;
        }

        public String getCaseType() {
            return caseType;
        }

        public String getPlayer() {
            return player;
        }

        @Override
        public String toString() {
            return "InfoEntry{" +
                    "player='" + player + '\'' +
                    ", caseType='" + caseType + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InfoEntry infoEntry = (InfoEntry) o;
            return Objects.equals(player, infoEntry.player) && Objects.equals(caseType, infoEntry.caseType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(player, caseType);
        }
    }

}

class CacheEntry<V> {
    final V value;
    final long timestamp;

    public CacheEntry(V value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CacheEntry{" +
                "value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}