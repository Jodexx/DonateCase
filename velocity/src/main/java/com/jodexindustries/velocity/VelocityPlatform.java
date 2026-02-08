package com.jodexindustries.velocity;

import com.jodexindustries.velocity.database.VelocityCaseDatabase;
import com.jodexindustries.velocity.database.VelocityDatabase;
import com.jodexindustries.velocity.database.VelocityDatabaseConfig;
import com.jodexindustries.velocity.database.VelocityDatabaseSettings;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import lombok.Getter;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.nio.file.Path;

@Plugin(
    id = "donatecase",
    name = "donatecase",
    version = BuildConstants.VERSION
    ,url = "https://www.jodex.xyz"
    ,authors = {"_Jodex__"}
)
public class VelocityPlatform {

    @Inject private Logger logger;
    @Inject @DataDirectory private Path dataDirectory;

    private VelocityDatabase database;
    @Getter
    private VelocityCaseDatabase caseDatabase;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        VelocityDatabaseConfig config = VelocityDatabaseConfig.load(dataDirectory, logger);

        database = new VelocityDatabase(logger);
        VelocityDatabaseSettings settings = config.toSettings();
        database.connect(dataDirectory, config.getType(), settings);

        caseDatabase = new VelocityCaseDatabase(database, logger, config.getCacheMaxAgeTicks());
        logger.info("DonateCase Velocity database initialized");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (caseDatabase != null) {
            caseDatabase.close();
        } else if (database != null) {
            database.close();
        }
    }

}
