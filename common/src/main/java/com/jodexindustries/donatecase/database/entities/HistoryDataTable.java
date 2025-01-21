package com.jodexindustries.donatecase.database.entities;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public HistoryDataTable(CaseData.CaseDataHistory historyData) {
        update(historyData);
    }

    public CaseData.CaseDataHistory toHistoryData() {
        return new CaseData.CaseDataHistory(item, caseType, playerName, time, group, action);
    }

    public void update(CaseData.CaseDataHistory historyData) {
        this.caseType = historyData.getCaseType();
        this.id = historyData.getId();
        this.item = historyData.getItem();
        this.playerName = historyData.getPlayerName();
        this.time = historyData.getTime();
        this.group = historyData.getGroup();
        this.action = historyData.getAction();
    }

}
