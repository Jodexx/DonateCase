package com.jodexindustries.donatecase.api.data.casedata;

/**
 * Class to implement information about case opening histories
 */
public class CaseDataHistory implements Cloneable {
    private int id;
    private String item;
    private String playerName;
    private long time;
    private String group;
    private String caseType;
    private String action;

    /**
     * Default constructor
     *
     * @param item       Item name
     * @param caseType   Case type
     * @param playerName Player name
     * @param time       Timestamp
     * @param group      Group name
     * @param action     Action name
     */
    public CaseDataHistory(String item, String caseType, String playerName, long time, String group, String action) {
        this.item = item;
        this.playerName = playerName;
        this.time = time;
        this.group = group;
        this.caseType = caseType;
        this.action = action;
    }

    @Override
    public String toString() {
        return "HistoryData{" +
                "playerName='" + playerName + '\'' +
                ", time=" + time +
                ", group='" + group + '\'' +
                ", caseType='" + caseType + '\'' +
                ", action='" + action + '\'' +
                '}';
    }

    @Override
    public CaseDataHistory clone() {
        try {
            return (CaseDataHistory) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Set history item name
     *
     * @param item item name
     */
    public void setItem(String item) {
        this.item = item;
    }

    /**
     * Set history player name
     *
     * @param playerName player name
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Set history timestamp
     *
     * @param time timestamp
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Set history group name
     *
     * @param group group name
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Set history case type
     *
     * @param caseType case type
     */
    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    /**
     * Set history action name
     *
     * @param action action name
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Get material id like HDB:1234, HEAD:name, RED_WOOL etc.
     *
     * @return material id
     */
    public int getId() {
        return id;
    }

    /**
     * Set material id like HDB:1234, HEAD:name, RED_WOOL etc.
     *
     * @param id material id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get player name, who opened case
     *
     * @return player name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Get timestamp, when case successful opened
     *
     * @return timestamp
     */
    public long getTime() {
        return time;
    }

    /**
     * Get win group
     *
     * @return win group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Get case type
     *
     * @return case type
     */
    public String getCaseType() {
        return caseType;
    }

    /**
     * Get action (like group, but from RandomActions section)
     *
     * @return action
     */
    public String getAction() {
        return action;
    }

    /**
     * Get win item name (like path of item in case config)
     *
     * @return win item name
     */
    public String getItem() {
        return item;
    }
}