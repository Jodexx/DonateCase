package com.jodexindustries.donatecase.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "player_keys")
public class PlayerKeysTable {
    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public int getKeys() {
        return keys;
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }

    @DatabaseField(id = true)
    private String player;
    @DatabaseField(canBeNull = false, columnName = "case_name")
    private String caseName;
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int keys;
}
