package com.jodexindustries.donatecase.tools.support.papi;

import com.jodexindustries.donatecase.BukkitBackend;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DonateCaseExpansion extends PlaceholderExpansion {

    private final BukkitBackend backend;

    public DonateCaseExpansion(BukkitBackend backend) {
        this.backend = backend;
    }

    public @NotNull String getAuthor() {
        return "JodexIndustries";
    }

    public @NotNull String getIdentifier() {
        return backend.getName();
    }

    public @NotNull String getVersion() {
        return backend.getVersion();
    }

    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.startsWith("keys")) return processKeys(params, player);
        if (params.startsWith("open_count")) return processOpenCount(params, player);
        if (params.startsWith("history_")) return processHistory(params.replaceFirst("history_", ""));

        return null;
    }

    private String processKeys(@NotNull String params, OfflinePlayer player) {
        if (params.startsWith("keys")) {
            String[] parts = params.split("_", 2);
            Map<String, Integer> map = backend.getAPI().getCaseKeyManager().getCache(player.getName());
            int keys = map.values().stream().mapToInt(key -> key).sum();

            if (parts.length == 1) {
                return String.valueOf(keys);
            } else if (parts[1].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(keys);
            }
        }

        if (params.startsWith("keys_")) {
            String[] parts = params.split("_", 3);
            int keys = backend.getAPI().getCaseKeyManager().getCache(parts[1], player.getName());
            if (parts.length == 2) {
                return String.valueOf(keys);
            } else if (parts[2].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(keys);
            } else {
                return String.valueOf(keys);
            }
        }
        return null;
    }

    private String processOpenCount(@NotNull String params, OfflinePlayer player) {
        if (params.startsWith("open_count")) {
            String[] parts = params.split("_", 3);
            Map<String, Integer> map = backend.getAPI().getCaseOpenManager().getCache(player.getName());
            int count = map.values().stream().mapToInt(c -> c).sum();

            if (parts.length == 2) {
                return String.valueOf(count);
            } else if (parts[2].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(count);
            } else {
                return String.valueOf(count);
            }
        }

        if (params.startsWith("open_count_")) {
            String[] parts = params.split("_", 4);
            int count = backend.getAPI().getCaseOpenManager().getCache(parts[2], player.getName());
            if (parts.length == 3) {
                return String.valueOf(count);
            } else if (parts[3].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(count);
            } else {
                return String.valueOf(count);
            }
        }
        return null;
    }

    private String processHistory(@NotNull String params) {
        String[] parts = params.split("_");
        if (parts.length >= 3) {
            String caseType = parts[0];
            int index = parseInt(parts[1]);
            if(index >= 0) {
                List<CaseData.History> list = backend.getAPI().getDatabase().getCache(caseType);
                if(list.size() > index) {
                    CaseData.History history = list.get(index);
                    String type = parts[2].toLowerCase();

                    switch (type) {
                        case "player" : {
                            return history.getPlayerName();
                        }

                        case "casetype" : {
                            return history.getCaseType();
                        }

                        case "group" : {
                            return history.getGroup();
                        }

                        case "action" : {
                            return history.getAction();
                        }

                        case "item" : {
                            return history.getItem();
                        }

                        case "time" : {
                            DateFormat formatter = new SimpleDateFormat(
                                    backend.getAPI().getConfig().getConfig().node("DonateCase", "DateFormat").getString("dd.MM HH:mm:ss"));
                            return formatter.format(new Date(history.getTime()));
                        }
                    }
                }
            }
        }

        return null;
    }

    private int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ignored) {
        }
        return -1;
    }

}