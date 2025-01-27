package com.jodexindustries.donatecase.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@DatabaseTable(tableName = "player_keys")
public class PlayerKeysTable {

    @DatabaseField(canBeNull = false)
    private String player;

    @DatabaseField(canBeNull = false, columnName = "case_name")
    private String caseType;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int keys;

}