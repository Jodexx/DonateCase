package com.jodexindustries.donatecase.api;

/**
 * Class to implement information about case opening histories
 */
public class HistoryData {
    private final String playerName;
    private final long time;
    private final String group;
    private final String caseType;
    private final String action;

    public HistoryData(String caseType, String playerName, long time, String group, String action) {
        this.playerName = playerName;
        this.time = time;
        this.group = group;
        this.caseType = caseType;
        this.action = action;
    }

    /**
     * Get player name, who opened case
     * @return player name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Get timestamp, when case successful opened
     * @return timestamp
     */

    public long getTime() {
        return time;
    }

    /**
     * Get win group
     * @return win group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Get case type
     * @return case type
     */
    public String getCaseType() {
        return caseType;
    }

    public String getAction() {
        return action;
    }
}
