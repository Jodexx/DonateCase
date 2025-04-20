package com.jodexindustries.donatecase.api.data.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Map;

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
    private Map<String, Integer> levelGroups;

    @Setting("DateFormat")
    private String dateFormat = "dd.MM HH:mm:ss";

    @Setting("AddonsHelp")
    private boolean addonsHelp = true;

    @Setting("UsePackets")
    private boolean usePackets = false;

    @Setting("Caching")
    private long caching = 20;

    @Comment("Set spawn-protection to 0 in server.properties")
    @Setting("DisableSpawnProtection")
    private boolean disableSpawnProtection = true;

    @Comment("If true, checks whether the player with the nickname exists on the server.")
    @Setting("CheckPlayerName")
    private boolean checkPlayerName = true;

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
