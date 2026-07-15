package com.jodexindustries.dcwebhook.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jodexindustries.dcwebhook.bootstrap.MainAddon;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.event.animation.AnimationEndEvent;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.api.tools.Placeholder;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class DiscordWebhook {

    @Setting
    private String url;

    @Setting
    @Expose
    private String content;

    @Setting
    @Expose
    private String username;

    @Setting("avatar_url")
    @Expose
    @SerializedName("avatar_url")
    private String avatarUrl;

    @Setting
    @Expose
    private boolean tts;

    @Setting
    @Expose
    private List<EmbedObject> embeds;

    @Setting
    private Mappings mappings;

    public void execute(AnimationEndEvent event) throws IOException {
        if (content == null && (embeds == null || embeds.isEmpty())) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }

        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Set webhook url!");
        }

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        URL url = new URL(this.url);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Java-DiscordWebhook");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        String json = formatPlaceholders(gson.toJson(this), event.activeCase());

        byte[] payload = json.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", String.valueOf(payload.length));

        try (OutputStream stream = connection.getOutputStream()) {
            stream.write(payload);
            stream.flush();
        }

        int responseCode = connection.getResponseCode();

        InputStream stream =
                responseCode >= 200 && responseCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();

        if (stream != null) {
            String response = readFully(stream);
            if (!response.isEmpty()) {
                MainAddon.instance.getLogger().warning("Discord webhook error: " + response);
            }
        }

        connection.disconnect();
    }

    private String formatPlaceholders(String text, ActiveCase activeCase) {
        String player = activeCase.player().getName();
        CaseItem winItem = activeCase.winItem();
        String group = winItem.group() != null ? winItem.group() : winItem.name();
        String caseType = activeCase.caseType();

        return DCTools.rt(
                text,
                Placeholder.of("%player%", player),
                Placeholder.of("%group%", group),
                Placeholder.of(
                        "%group_mapping%",
                        mappings.groups.getOrDefault(group, group)
                ),
                Placeholder.of("%casetype%", caseType),
                Placeholder.of(
                        "%casetype_mapping%",
                        mappings.cases.getOrDefault(caseType, caseType)
                )
        );
    }

    private String readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, length);
        }

        return output.toString(StandardCharsets.UTF_8.name());
    }

    @ConfigSerializable
    public static class EmbedObject {

        @Setting
        @Expose
        private String title;

        @Setting
        @Expose
        private String description;

        @Setting
        @Expose
        private String url;

        @Setting
        @Expose
        private int color;

        @Setting
        @Expose
        private Footer footer;

        @Setting
        @Expose
        private Thumbnail thumbnail;

        @Setting
        @Expose
        private Image image;

        @Setting
        @Expose
        private Author author;

        @Setting
        @Expose
        private List<Field> fields;

        @ConfigSerializable
        private static class Footer {

            @Setting
            @Expose
            private String text;

            @Setting("icon_url")
            @Expose
            @SerializedName("icon_url")
            private String iconUrl;
        }

        @ConfigSerializable
        private static class Thumbnail {

            @Setting
            @Expose
            private String url;
        }

        @ConfigSerializable
        private static class Image {

            @Setting
            @Expose
            private String url;
        }

        @ConfigSerializable
        private static class Author {

            @Setting
            @Expose
            private String name;

            @Setting
            @Expose
            private String url;

            @Setting("icon_url")
            @Expose
            @SerializedName("icon_url")
            private String iconUrl;
        }

        @ConfigSerializable
        private static class Field {

            @Setting
            @Expose
            private String name;

            @Setting
            @Expose
            private String value;

            @Setting
            @Expose
            private boolean inline;

        }
    }

    @ConfigSerializable
    public static class Mappings {

        @Setting
        protected Map<String, String> groups = new HashMap<>();

        @Setting
        protected Map<String, String> cases = new HashMap<>();
    }

}