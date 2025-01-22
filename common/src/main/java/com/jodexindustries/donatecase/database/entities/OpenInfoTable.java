package com.jodexindustries.donatecase.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@DatabaseTable(tableName = "open_info")
public class OpenInfoTable {
    @DatabaseField(canBeNull = false)
    private String player;
    @DatabaseField(columnName = "case_type")
    private String caseType;
    @DatabaseField(defaultValue = "0")
    private int count;

}