package com.jodexindustries.donatecase.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "open_info")
public class OpenInfoTable {
    @DatabaseField(canBeNull = false)
    private String player;
    @DatabaseField(columnName = "case_type")
    private String caseType;
    @DatabaseField(defaultValue = "0")
    private int count;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "OpenInfoTable{" +
                "player='" + player + '\'' +
                ", caseType='" + caseType + '\'' +
                ", count=" + count +
                '}';
    }

}
