package com.jodexindustries.donatecase.api;

public class HistoryData {
    private final String playerName;
    private final long time;
    private final String group;

    public HistoryData(String playerName, long time, String group) {
        this.playerName = playerName;
        this.time = time;
        this.group = group;
    }

    public String getPlayerName() {
        return playerName;
    }

    public long getTime() {
        return time;
    }


    public String getGroup() {
        return group;
    }
}
