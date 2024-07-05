package com.jodexindustries.donatecase.tools;

import java.text.NumberFormat;
import java.util.concurrent.CompletableFuture;

import com.jodexindustries.donatecase.api.Case;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholder extends PlaceholderExpansion {

    SimpleCache<SimpleCache.InfoEntry, Integer> keysCache = new SimpleCache<>(10 * 1000);

    SimpleCache<SimpleCache.InfoEntry, Integer> openCache = new SimpleCache<>(10 * 1000);



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
                SimpleCache.InfoEntry entry = new SimpleCache.InfoEntry(player.getName(), caseType);
                Integer cachedKeys = keysCache.get(entry);
                if(cachedKeys == null) {
                    cachedKeys = getKeysByParams(entry).join();
                    keysCache.put(entry, cachedKeys);
                }
                keys += cachedKeys;
            }
            if(parts.length == 1) {
                return String.valueOf(keys);
            } else if(parts[1].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(keys);
            }
        }

        if (params.startsWith("keys_")) {
            String[] parts = params.split("_", 3);
            SimpleCache.InfoEntry entry = new SimpleCache.InfoEntry(player.getName(), parts[1]);
            Integer cachedKeys = keysCache.get(entry);
            if(cachedKeys == null) {
                cachedKeys = getKeysByParams(entry).join();
                keysCache.put(entry, cachedKeys);
            }
            int keys = cachedKeys;
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
                SimpleCache.InfoEntry entry = new SimpleCache.InfoEntry(player.getName(), caseType);
                Integer cachedCount = openCache.get(entry);
                if(cachedCount == null) {
                    cachedCount = getOpenCountByParams(entry).join();
                    openCache.put(entry, cachedCount);
                }
                openCount += cachedCount;
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
            SimpleCache.InfoEntry entry = new SimpleCache.InfoEntry(player.getName(), parts[2]);
            Integer cachedCount = openCache.get(entry);
            if(cachedCount == null) {
                cachedCount = getOpenCountByParams(entry).join();
                openCache.put(entry, cachedCount);
            }
            if(parts.length == 3) {
                return String.valueOf(cachedCount);
            } else if(parts[3].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(cachedCount);
            } else {
                return String.valueOf(cachedCount);
            }
        }
        return null;
    }

    /*
     * <player>_<case>
     */
    private CompletableFuture<Integer> getKeysByParams(SimpleCache.InfoEntry entry) {
        return Case.getKeysAsync(entry.getCaseType(), entry.getPlayer());
    }

    /*
     * <player>_<case>
     */
    private CompletableFuture<Integer> getOpenCountByParams(SimpleCache.InfoEntry entry) {
        return Case.getOpenCountAsync(entry.getCaseType(), entry.getPlayer());
    }
}
