package com.jodexindustries.dcwebhook.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jodexindustries.dcwebhook.bootstrap.MainAddon;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.event.animation.AnimationEndEvent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    public void execute(AnimationEndEvent event) throws IOException {
        if (this.content == null && this.embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }

        if (this.url == null || this.url.isEmpty()) {
            throw new IllegalArgumentException("Set webhook url!");
        }


        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        URL url = new URL(this.url);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Java-DiscordWebhook");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        String json = replace(gson.toJson(this), event.activeCase());

        byte[] payload = json.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", String.valueOf(payload.length));

        try (OutputStream stream = connection.getOutputStream()) {
            stream.write(payload);
            stream.flush();
        }

        int responseCode = connection.getResponseCode();

        InputStream stream;
        if (responseCode >= 200 && responseCode < 300) {
            stream = connection.getInputStream();
        } else {
            stream = connection.getErrorStream();
        }

        if (stream != null && stream.read() != -1) {
            String response = readFully(stream).toString();
            if(!response.isEmpty()) MainAddon.instance.getLogger().warning("Discord webhook error: " + response);
        }

        connection.disconnect();
    }

    private String replace(String text, ActiveCase activeCase) {
        if(text == null) return null;
        String caseTitle = "";
        try {
            CaseData caseData = MainAddon.instance.api.getCaseManager().get(activeCase.caseType());
            if (caseData != null && caseData.caseGui() != null && caseData.caseGui().title() != null) {
                caseTitle = caseData.caseGui().title();
            }
        } catch (Exception ignored) {}
        if (caseTitle == null) caseTitle = "";
        caseTitle = stripColorCodes(caseTitle);
        return text
                .replaceAll("%player%", safe(activeCase.player() != null ? activeCase.player().getName() : null))
                .replaceAll("%group%", safe(activeCase.winItem() != null ? activeCase.winItem().group() : null))
                .replaceAll("%casetype%", safe(activeCase.caseType()))
                .replaceAll("%casetitle%", safe(caseTitle));
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }


    private String stripColorCodes(String input) {
        if (input == null) return "";
        input = input.replaceAll("(?i)&x((&[0-9A-F]){6})", "");
        input = input.replaceAll("(?i)&#[0-9A-F]{6}", "");
        input = input.replaceAll("(?i)#[0-9A-F]{6}", "");
        input = input.replaceAll("(?i)&[0-9A-FK-OR]", "");
        input = input.replaceAll("(?i)§[0-9A-FK-OR]", "");
        input = input.replaceAll("(?i)§x(§[0-9A-F]){6}", "");
        return input;
    }

    private ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream;
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

}