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

    @Setting
    private boolean updateChecker = true;

    @Setting
    private MySQL mysql = new MySQL();

    @Setting
    private String language = "en_US";

    @Setting
    private String hologramDriver = "DecentHolograms";

    @Setting
    private CaseSettings.LevelGroups levelGroups;

    @Setting
    private String dateFormat = "dd.MM HH:mm:ss";

    @Setting
    private boolean addonsHelp = true;

    @Setting
    private boolean usePackets = true;

    @Setting
    private long caching = 20;

    @Comment("If true, formats the nickname of the player (searches for a similar one on the server).")
    @Setting
    private boolean formatPlayerName = false;

    @Setting
    private Converter converter;

    @Accessors(fluent = true)
    @Getter
    @Setter
    @ConfigSerializable
    public static class MySQL {

        @Setting
        private boolean enabled = false;

        @Setting
        private String host = "localhost";

        @Setting
        private int port = 3306;

        @Setting
        private String database = "donatecase";

        @Setting
        private String username = "admin";

        @Setting
        private String password = "123456";
    }

    @Accessors(fluent = true)
    @Getter
    @Setter
    @ConfigSerializable
    public static class Converter {

        @Setting
        private boolean keys = false;

        @Setting
        private boolean data = false;
    }
}
