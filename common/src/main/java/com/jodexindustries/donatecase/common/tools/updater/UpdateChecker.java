package com.jodexindustries.donatecase.common.tools.updater;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jodexindustries.donatecase.common.DonateCase;
import com.jodexindustries.donatecase.api.tools.DCTools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {

    private final DonateCase api;

    public UpdateChecker(DonateCase api) {
        this.api = api;
    }

    public void check() {
        getVersion().thenAcceptAsync(version -> {
            if (version.isNew()) {
                api.getPlatform().getLogger().info("There is a new update " + version.getVersionNumber() + " available.");
                api.getPlatform().getLogger().info("Download - https://modrinth.com/plugin/donatecase");
            }
        });
    }

    public CompletableFuture<VersionInfo> getVersion() {
        return !api.getConfigManager().getConfig().node("DonateCase", "UpdateChecker").getBoolean() ? CompletableFuture.completedFuture(new VersionInfo()) :
                CompletableFuture.supplyAsync(() -> {

                    try {
                        URL url = new URL("https://api.modrinth.com/v2/project/donatecase/version");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Accept", "application/json");

                        if (connection.getResponseCode() == 200) {
                            return getLatest(connection);
                        } else {
                            api.getPlatform().getLogger().warning("Unable to check for updates. HTTP response code: " + connection.getResponseCode());
                        }

                    } catch (Exception exception) {
                        api.getPlatform().getLogger().warning("Unable to check for updates: " + exception.getMessage());
                    }
                    return null;
                });
    }

    private VersionInfo getLatest(HttpURLConnection connection) throws IOException {
        Gson gson = new Gson();
        JsonArray versions;

        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            versions = JsonParser.parseReader(reader).getAsJsonArray();
        }

        VersionInfo latestVersionInfo = null;

        for (JsonElement versionElement : versions) {
            JsonObject versionObject = versionElement.getAsJsonObject();

            VersionInfo versionInfo = gson.fromJson(versionObject, VersionInfo.class);

            if (latestVersionInfo == null || versionInfo.getDatePublished().compareTo(latestVersionInfo.getDatePublished()) > 0) {
                latestVersionInfo = versionInfo;
            }
        }

        if(latestVersionInfo != null) {
            if (DCTools.getPluginVersion(api.getPlatform().getVersion()) < DCTools.getPluginVersion(latestVersionInfo.getVersionNumber())) {
                latestVersionInfo.setNew(true);
            }
        }

        return latestVersionInfo;
    }

}