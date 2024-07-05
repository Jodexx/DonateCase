package com.jodexindustries.donatecase.tools;

import java.text.NumberFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.jodexindustries.donatecase.api.Case;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholder extends PlaceholderExpansion {

    private final LoadingCache<String, Integer> keys = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .refreshAfterWrite(10, TimeUnit.SECONDS)
            .build(
                    new CacheLoader<String, Integer>() {
                        @Override
                        public Integer load(@NotNull String params) {
                            String[] args = params.split("_");
                            String player = args[0];
                            String caseType = args[1];
                            return Case.getKeys(caseType, player);
                        }

                        @Override
                        public ListenableFuture<Integer> reload(@NotNull String params, @NotNull Integer prev) {
                            return ListenableFutureTask.create(() -> getKeysByParams(params).join());
                        }
                    });

    private final LoadingCache<String[], Integer> openCount = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .refreshAfterWrite(10, TimeUnit.SECONDS)
            .build(
                    new CacheLoader<String[], Integer>() {
                        @Override
                        public Integer load(String @NotNull [] params) {
                            return getOpenCountByParams(params).join();
                        }

                        @Override
                        public ListenableFuture<Integer> reload(String @NotNull [] params, @NotNull Integer prev) {
                            return ListenableFutureTask.create(() -> getOpenCountByParams(params).join());
                        }
                    });


    public @NotNull String getAuthor() {
        return "JodexIndustries";
    }

    public @NotNull String getIdentifier() {
        return "DonateCase";
    }

    public @NotNull String getVersion() {
        return Case.getInstance().getDescription().getVersion();
    }

    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.startsWith("keys")) {
            String[] parts = params.split("_", 2);
            int keys = 0;
            for (String caseType : Case.caseData.keySet()) {
                String param = player.getName() + "_" + caseType;
                keys += this.keys.getUnchecked(param);
            }
            if(parts.length == 1) {
                return String.valueOf(keys);
            } else if(parts[1].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(keys);
            }
        }

        if (params.startsWith("keys_")) {
            String[] parts = params.split("_", 3);
            String param = player.getName() + "_" + parts[1];
            int keys = this.keys.getUnchecked(param);
            if(parts.length == 2) {
                return String.valueOf(keys);
            } else if(parts[2].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(keys);
            } else {
                return String.valueOf(keys);
            }
        }

        if(params.startsWith("open_count")) {
            String[] parts = params.split("_", 3);
            int openCount = 0;
            for (String caseType : Case.caseData.keySet()) {
                String[] param = {player.getName(), caseType};
                openCount += this.openCount.getUnchecked(param);
            }
            if (parts.length == 2) {
                return String.valueOf(openCount);
            } else if (parts[2].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(openCount);
            } else {
                return String.valueOf(openCount);
            }
        }

        if(params.startsWith("open_count_")) {
            String[] parts = params.split("_", 4);
            String[] param = {player.getName(), parts[2]};
            int openCount = this.openCount.getUnchecked(param);
            if(parts.length == 3) {
                return String.valueOf(openCount);
            } else if(parts[3].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(openCount);
            } else {
                return String.valueOf(openCount);
            }
        }
        return null;
    }

    /*
     * <player>_<case>
     */
    private CompletableFuture<Integer> getKeysByParams(String params) {
        String[] args = params.split("_");
        String player = args[0];
        String caseType = args[1];
        return Case.getKeysAsync(caseType, player);
    }

    /*
     * <player>_<case>
     */
    private CompletableFuture<Integer> getOpenCountByParams(String[] params) {
        String player = params[0];
        String caseType = params[1];
        return Case.getOpenCountAsync(caseType, player);
    }
}
