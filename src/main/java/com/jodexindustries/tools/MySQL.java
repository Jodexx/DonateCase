package com.jodexindustries.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jodexindustries.dc.Main;
import org.bukkit.Bukkit;

public class MySQL {
    public Connection con;
    public Statement stmt;

    public MySQL(String host, String user, String password) {
        try {
            if (this.con != null) {
                this.con.close();
            }

            this.con = DriverManager.getConnection(host, user, password);
            this.stmt = this.con.createStatement();
        } catch (SQLException var5) {
            var5.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(Main.instance);
        }

    }

    public int getKey(String name, String player) {
        try {
            player = player.toLowerCase();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM `donate_cases` WHERE `player`='" + player + "' AND case_name='" + name + "'");
            if (rs.next()) {
                return rs.getInt(3);
            }
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

        return 0;
    }

    public void createTable() {
        try {
            this.stmt.executeUpdate("CREATE TABLE `donate_cases` (`player` varchar(16) NOT NULL, `case_name` varchar(32) NOT NULL, `keys_count` int(16) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1");
        } catch (SQLException var2) {
            var2.printStackTrace();
        }

    }

    public void setKey(String name, String player, int keys) {
        try {
            player = player.toLowerCase();
            if (!this.hasField("donate_cases", "player='" + player + "' AND case_name='" + name + "'")) {
                this.stmt.executeUpdate(Main.t.rt("INSERT INTO `donate_cases` (`player`, `case_name`, `keys_count`) VALUES ('%player', '%case', '%keys')",
                        "%player:" + player, "%keys:" + keys, "%case:" + name));
            } else {
                this.stmt.executeUpdate(Main.t.rt("UPDATE `donate_cases` SET keys_count='%keys' WHERE player='%player' AND case_name='%case'",
                        "%player:" + player, "%keys:" + keys, "%case:" + name));
            }
        } catch (SQLException var5) {
            var5.printStackTrace();
        }

    }

    public void delAllKey() {
        try {
            this.stmt.executeUpdate(Main.t.rt("DELETE FROM `donate_cases`"));
        } catch (SQLException var2) {
            var2.printStackTrace();
        }

    }

    public boolean hasTable(String table) {
        try {
            this.stmt.executeQuery("SELECT * FROM " + table);
            return true;
        } catch (SQLException var3) {
            return false;
        }
    }

    public boolean hasField(String table, String t) {
        try {
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM `" + table + "` WHERE " + t);
            rs.next();
            rs.getString(1);
            return true;
        } catch (SQLException var4) {
            return false;
        }
    }

    public void close() {
        try {
            this.con.close();
        } catch (SQLException var2) {
            var2.printStackTrace();
        }

    }
}
