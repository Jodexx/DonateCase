package com.jodexindustries.donatecase.api.data.config;

import com.jodexindustries.donatecase.api.data.casedefinition.CaseSettings;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@Accessors(fluent = true)
@Getter
@Setter
@ConfigSerializable
public class ConfigData {

    @Setting("UpdateChecker")
    private boolean updateChecker = true;

    @Setting("MySql")
    private MySQL mySQL = new MySQL();

    @Setting("Languages")
    private String languages = "en_US";

    @Setting("HologramDriver")
    private String hologramDriver = "DecentHolograms";

    @Setting("LevelGroups")
    private CaseSettings.LevelGroups levelGroups;

    @Setting("DateFormat")
    private String dateFormat = "dd.MM HH:mm:ss";

    @Setting("AddonsHelp")
    private boolean addonsHelp = true;

    @Setting("UsePackets")
    private boolean usePackets = false;

    @Setting("Caching")
    private long caching = 20;

    @Comment("If true, formats the nickname of the player (searches for a similar one on the server).")
    @Setting("CheckPlayerName")
    private boolean checkPlayerName = false;

    @Accessors(fluent = true)
    @Getter
    @Setter
    @ConfigSerializable
    public static class MySQL {

        @Setting("Enabled")
        private boolean enabled = false;

        @Setting("Host")
        private String host = "localhost";

        @Setting("Port")
        private int port = 3306;

        @Setting("DataBase")
        private String database = "donatecase";

        @Setting("User")
        private String user = "admin";

        @Setting("Password")
        private String password = "123456";
    }
}
