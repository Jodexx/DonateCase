package com.jodexindustries.donatecase.spigot.materials;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.api.scheduler.DCFuture;
import com.jodexindustries.donatecase.spigot.BukkitBackend;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Creates a player head from a player name.
 * <p>
 * Resolving a skull may hit the network, so menu building never waits for it:
 * a head is either taken from the cache or a plain one is returned while the
 * real skull is resolved in the background. Heads of players present in the
 * case history are warmed up in background, so an opened menu is served from
 * the cache. Requests for the same name share a single lookup, and names that
 * failed to resolve are not retried for a while.
 */
public class HEADMaterialHandlerImpl implements MaterialHandler {

    private static final long CACHE_EXPIRE_MILLIS = TimeUnit.HOURS.toMillis(6);
    private static final long FAILURE_EXPIRE_MILLIS = TimeUnit.MINUTES.toMillis(10);
    private static final int CACHE_MAX_SIZE = 1024;
    private static final int WARMUP_LIMIT = 25;
    private static final long WARMUP_DELAY_TICKS = 100L;
    private static final long WARMUP_PERIOD_TICKS = 1200L;

    private final Map<String, CachedSkull> cache = Collections.synchronizedMap(
            new LinkedHashMap<String, CachedSkull>(64, 0.75F, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, CachedSkull> eldest) {
                    return size() > CACHE_MAX_SIZE || eldest.getValue().isExpired();
                }
            });

    private final Map<String, DCFuture<?>> pending = new HashMap<>();
    private final Map<String, Long> failures = new ConcurrentHashMap<>();

    public HEADMaterialHandlerImpl(BukkitBackend backend) {
        backend.getScheduler().async(backend, this::warmUp, WARMUP_DELAY_TICKS, WARMUP_PERIOD_TICKS);
    }

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        ItemStack cached = fromCache(context);
        if (cached != null) return cached;

        if (isFailedRecently(context)) return createPlainHead();

        DCFuture<?> future = resolve(context);

        if (future != null && future.isDone() && !future.isCompletedExceptionally()) {
            try {
                Object skull = future.get();
                if (skull instanceof ItemStack) return ((ItemStack) skull).clone();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception ignored) {
            }
        }

        return createPlainHead();
    }

    private void warmUp() {
        int limit = WARMUP_LIMIT;

        try {
            for (CaseData.History history : DCAPI.getInstance().getDatabase().getCache()) {
                if (limit <= 0) break;
                if (history == null) continue;

                String playerName = history.playerName();
                if (playerName == null) continue;
                if (fromCache(playerName) != null || isFailedRecently(playerName)) continue;

                resolve(playerName);
                limit--;
            }
        } catch (Throwable e) {
            DCAPI.getInstance().getPlatform().getLogger()
                    .log(Level.WARNING, "Error with warming up heads cache", e);
        }
    }

    @Nullable
    private ItemStack fromCache(String playerName) {
        CachedSkull cached = cache.get(playerName);
        return (cached != null && !cached.isExpired()) ? cached.itemStack.clone() : null;
    }

    private boolean isFailedRecently(String playerName) {
        Long time = failures.get(playerName);
        if (time == null) return false;

        if (System.currentTimeMillis() - time > FAILURE_EXPIRE_MILLIS) {
            failures.remove(playerName);
            return false;
        }

        return true;
    }

    private void markFailed(String playerName) {
        if (failures.size() >= CACHE_MAX_SIZE) {
            long now = System.currentTimeMillis();
            failures.values().removeIf(time -> now - time > FAILURE_EXPIRE_MILLIS);
        }

        failures.put(playerName, System.currentTimeMillis());
    }

    @Nullable
    private DCFuture<?> resolve(String playerName) {
        synchronized (pending) {
            DCFuture<?> existing = pending.get(playerName);
            if (existing != null) return existing;

            DCFuture<?> future;
            try {
                future = DCAPI.getInstance().getPlatform().getTools().createSkullFromPlayer(playerName);
            } catch (Throwable e) {
                markFailed(playerName);
                DCAPI.getInstance().getPlatform().getLogger()
                        .log(Level.WARNING, "Error with loading head of " + playerName, e);
                return null;
            }

            pending.put(playerName, future);

            future.whenComplete((result, throwable) -> {
                synchronized (pending) {
                    pending.remove(playerName);
                }

                if (result instanceof ItemStack) {
                    failures.remove(playerName);
                    cache.put(playerName, new CachedSkull(((ItemStack) result).clone()));
                    return;
                }

                markFailed(playerName);

                if (throwable != null) {
                    DCAPI.getInstance().getPlatform().getLogger()
                            .log(Level.WARNING, "Error with loading head of " + playerName, throwable);
                }
            });

            return future;
        }
    }

    private ItemStack createPlainHead() {
        Material material = Material.getMaterial("PLAYER_HEAD");
        if (material == null) material = Material.getMaterial("SKULL_ITEM");

        return material != null ? new ItemStack(material) : new ItemStack(Material.STONE);
    }

    private static class CachedSkull {

        private final ItemStack itemStack;
        private final long time;

        private CachedSkull(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.time = System.currentTimeMillis();
        }

        private boolean isExpired() {
            return System.currentTimeMillis() - time > CACHE_EXPIRE_MILLIS;
        }
    }
}
