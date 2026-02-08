package com.jodexindustries.velocity.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public record VelocityHistory(
        int id,
        String item,
        String playerName,
        long time,
        String group,
        String caseType,
        String action
) {

    public static VelocityHistory fromResultSet(ResultSet rs) throws SQLException {
        return new VelocityHistory(
                rs.getInt("id"),
                rs.getString("item"),
                rs.getString("player_name"),
                rs.getLong("time"),
                rs.getString("group_name"),
                rs.getString("case_type"),
                rs.getString("action")
        );
    }
}
