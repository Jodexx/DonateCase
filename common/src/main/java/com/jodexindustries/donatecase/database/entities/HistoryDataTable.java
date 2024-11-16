package com.jodexindustries.donatecase.database.entities;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataHistory;

@DatabaseTable(tableName = "history_data")
public class HistoryDataTable {

    @DatabaseField(columnName = "case_type")
    private String caseType;

    @DatabaseField(columnName = "id")
    private int id;

    @DatabaseField(columnName = "item")
    private String item;

    @DatabaseField(columnName = "player_name")
    private String playerName;

    @DatabaseField(columnName = "time")
    private long time;

    @DatabaseField(columnName = "group")
    private String group;

    @DatabaseField(columnName = "action")
    private String action;

    public HistoryDataTable() {
    }

    public HistoryDataTable(CaseDataHistory historyData) {
        this.caseType = historyData.getCaseType();
        this.id = historyData.getId();
        this.item = historyData.getItem();
        this.playerName = historyData.getPlayerName();
        this.time = historyData.getTime();
        this.group = historyData.getGroup();
        this.action = historyData.getAction();
    }

    public CaseDataHistory toHistoryData() {
        return new CaseDataHistory(item, caseType, playerName, time, group, action);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
