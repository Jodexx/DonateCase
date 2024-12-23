package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataHistory;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.jodexindustries.donatecase.DonateCase.instance;

public class Placeholder extends PlaceholderExpansion {


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
        if (params.startsWith("keys")) return processKeys(params, player);
        if (params.startsWith("open_count")) return processOpenCount(params, player);
        if (params.startsWith("history_")) return processHistory(params.replaceFirst("history_", ""));

        return null;
    }

    private String processKeys(@NotNull String params, OfflinePlayer player) {
        if (params.startsWith("keys")) {
            String[] parts = params.split("_", 2);
            int keys = 0;
            for (String caseType : instance.api.getCaseManager().getMap().keySet()) {
                int cachedKeys = instance.api.getCaseKeyManager().getKeysCache(caseType, player.getName());
                keys += cachedKeys;
            }
            if (parts.length == 1) {
                return String.valueOf(keys);
            } else if (parts[1].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(keys);
            }
        }

        if (params.startsWith("keys_")) {
            String[] parts = params.split("_", 3);
            int keys = instance.api.getCaseKeyManager().getKeysCache(parts[1], player.getName());
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
            int openCount = 0;
            for (String caseType : instance.api.getCaseManager().getMap().keySet()) {
                openCount += instance.api.getCaseOpenManager().getOpenCountCache(caseType, player.getName());
            }
            if (parts.length == 2) {
                return String.valueOf(openCount);
            } else if (parts[2].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(openCount);
            } else {
                return String.valueOf(openCount);
            }
        }

        if (params.startsWith("open_count_")) {
            String[] parts = params.split("_", 4);
            Integer cachedCount = instance.api.getCaseOpenManager().getOpenCountCache(parts[2], player.getName());
            if (parts.length == 3) {
                return String.valueOf(cachedCount);
            } else if (parts[3].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(cachedCount);
            } else {
                return String.valueOf(cachedCount);
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
                List<CaseDataHistory> list = instance.api.getDatabase().getHistoryDataCache(caseType);
                if(list.size() > index) {
                    CaseDataHistory history = list.get(index);
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
                            DateFormat formatter = new SimpleDateFormat(instance.api.getConfig().getConfig().getString("DonateCase.DateFormat", "dd.MM HH:mm:ss"));
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