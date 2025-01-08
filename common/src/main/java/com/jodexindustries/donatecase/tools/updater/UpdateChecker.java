package com.jodexindustries.donatecase.tools.updater;

import com.google.gson.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class UpdateChecker {

    private final Logger logger;

    public UpdateChecker(Logger logger) {
        this.logger = logger;
    }

    public CompletableFuture<VersionInfo> getVersion() {
        return CompletableFuture.supplyAsync(() -> {

            try {
                URL url = new URL("https://api.modrinth.com/v2/project/donatecase/version");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                if (connection.getResponseCode() == 200) {
                    return getLatest(connection);
                } else {
                    logger.warning("Unable to check for updates. HTTP response code: " + connection.getResponseCode());
                }

            } catch (Exception exception) {
                logger.warning("Unable to check for updates: " + exception.getMessage());
            }
            return null;
        });
    }

    private static VersionInfo getLatest(HttpURLConnection connection) throws IOException {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray versions;

        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            versions = parser.parse(reader).getAsJsonArray();
        }

        VersionInfo latestVersionInfo = null;

        for (JsonElement versionElement : versions) {
            JsonObject versionObject = versionElement.getAsJsonObject();

            VersionInfo versionInfo = gson.fromJson(versionObject, VersionInfo.class);

            if (latestVersionInfo == null || versionInfo.getDatePublished().compareTo(latestVersionInfo.getDatePublished()) > 0) {
                latestVersionInfo = versionInfo;
            }
        }

        return latestVersionInfo;
    }

}