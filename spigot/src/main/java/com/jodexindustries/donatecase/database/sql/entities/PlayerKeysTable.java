package com.jodexindustries.donatecase.database.sql.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "player_keys")
public class PlayerKeysTable {
    @DatabaseField(canBeNull = false)
    private String player;
    @DatabaseField(canBeNull = false, columnName = "case_name")
    private String caseType;
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int keys;

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

    public int getKeys() {
        return keys;
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }

}