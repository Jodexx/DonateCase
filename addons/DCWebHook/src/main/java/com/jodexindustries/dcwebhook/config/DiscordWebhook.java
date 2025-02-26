package com.jodexindustries.dcwebhook.config;

import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.event.animation.AnimationEndEvent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ConfigSerializable
public class DiscordWebhook {

    @Setting
    private String url;

    @Setting
    private String content;

    @Setting
    private String username;

    @Setting("avatar_url")
    private String avatarUrl;

    @Setting
    private boolean tts;

    @Setting
    private List<EmbedObject> embeds;

    public void execute(AnimationEndEvent event) throws IOException {
        if (this.content == null && this.embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }

        if (this.url == null || this.url.isEmpty()) {
            throw new IllegalArgumentException("Set webhook url!");
        }

        JSONObject json = new JSONObject();

        json.put("content", replace(this.content, event));
        json.put("username", replace(this.username, event));
        json.put("avatar_url", this.avatarUrl);
        json.put("tts", this.tts);

        if (!this.embeds.isEmpty()) {
            List<JSONObject> embedObjects = new ArrayList<>();

            for (EmbedObject embed : this.embeds) {
                JSONObject jsonEmbed = new JSONObject();

                jsonEmbed.put("title", replace(embed.title, event));
                jsonEmbed.put("description", replace(embed.description, event));
                jsonEmbed.put("url", embed.url);

                if (embed.color != null) {
                    EmbedObject.Color color = embed.color;
                    int rgb = color.r;
                    rgb = (rgb << 8) + color.g;
                    rgb = (rgb << 8) + color.b;

                    jsonEmbed.put("color", rgb);
                }

                EmbedObject.Footer footer = embed.footer;
                EmbedObject.Image image = embed.image;
                EmbedObject.Thumbnail thumbnail = embed.thumbnail;
                EmbedObject.Author author = embed.author;
                List<EmbedObject.Field> fields = embed.fields;

                if (footer != null) {
                    JSONObject jsonFooter = new JSONObject();

                    jsonFooter.put("text", replace(footer.text, event));
                    jsonFooter.put("icon_url", footer.iconUrl);
                    jsonEmbed.put("footer", jsonFooter);
                }

                if (image != null) {
                    JSONObject jsonImage = new JSONObject();

                    jsonImage.put("url", image.url);
                    jsonEmbed.put("image", jsonImage);
                }

                if (thumbnail != null) {
                    JSONObject jsonThumbnail = new JSONObject();

                    jsonThumbnail.put("url", thumbnail.url);
                    jsonEmbed.put("thumbnail", jsonThumbnail);
                }

                if (author != null) {
                    JSONObject jsonAuthor = new JSONObject();

                    jsonAuthor.put("name", replace(author.name, event));
                    jsonAuthor.put("url", author.url);
                    jsonAuthor.put("icon_url", author.iconUrl);
                    jsonEmbed.put("author", jsonAuthor);
                }

                List<JSONObject> jsonFields = new ArrayList<>();
                for (EmbedObject.Field field : fields) {
                    JSONObject jsonField = new JSONObject();

                    jsonField.put("name", replace(field.name, event));
                    jsonField.put("value", replace(field.value, event));
                    jsonField.put("inline", field.inline);

                    jsonFields.add(jsonField);
                }

                jsonEmbed.put("fields", jsonFields.toArray());
                embedObjects.add(jsonEmbed);
            }

            json.put("embeds", embedObjects.toArray());
        }

        URL url = new URL(this.url);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Java-DiscordWebhook");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        OutputStream stream = connection.getOutputStream();
        stream.write(json.toString().getBytes());
        stream.flush();
        stream.close();

        connection.getInputStream().close(); //I'm not sure why but it doesn't work without getting the InputStream
        connection.disconnect();
    }

    private String replace(String text, AnimationEndEvent event) {
        if(text == null) return null;
        ActiveCase activeCase = event.activeCase();
        return text
                .replace("%player%", event.player().getName())
                .replace("%group%", activeCase.winItem().group())
                .replace("%casetype%", activeCase.caseType());
    }

    @Override
    public String toString() {
        return "DiscordWebhook{" +
                "url='" + url + '\'' +
                ", content='" + content + '\'' +
                ", username='" + username + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", tts=" + tts +
                ", embeds=" + embeds +
                '}';
    }

    @ConfigSerializable
    public static class EmbedObject {

        @Setting
        private String title;

        @Setting
        private String description;

        @Setting
        private String url;

        @Setting
        private Color color;

        @Setting
        private Footer footer;

        @Setting
        private Thumbnail thumbnail;

        @Setting
        private Image image;

        @Setting
        private Author author;

        @Setting
        private List<Field> fields;

        @Override
        public String toString() {
            return "EmbedObject{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", url='" + url + '\'' +
                    ", color=" + color +
                    ", footer=" + footer +
                    ", thumbnail=" + thumbnail +
                    ", image=" + image +
                    ", author=" + author +
                    ", fields=" + fields +
                    '}';
        }

        @ConfigSerializable
        private static class Footer {

            @Setting
            private String text;

            @Setting("icon_url")
            private String iconUrl;
        }

        @ConfigSerializable
        private static class Thumbnail {

            @Setting
            private String url;
        }

        @ConfigSerializable
        private static class Image {

            @Setting
            private String url;
        }

        @ConfigSerializable
        private static class Author {

            @Setting
            private String name;

            @Setting
            private String url;

            @Setting("icon_url")
            private String iconUrl;
        }

        @ConfigSerializable
        private static class Field {

            @Setting
            private String name;

            @Setting
            private String value;

            @Setting
            private boolean inline;

        }

        @ConfigSerializable
        private static class Color {

            @Setting
            private int r;

            @Setting
            private int g;

            @Setting
            private int b;
        }
    }

    private static class JSONObject {

        private final HashMap<String, Object> map = new HashMap<>();

        void put(String key, Object value) {
            if (value != null) {
                map.put(key, value);
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            builder.append("{");

            int i = 0;
            for (Map.Entry<String, Object> entry : entrySet) {
                Object val = entry.getValue();
                builder.append(quote(entry.getKey())).append(":");

                if (val instanceof String) {
                    builder.append(quote(String.valueOf(val)));
                } else if (val instanceof Integer) {
                    builder.append(Integer.valueOf(String.valueOf(val)));
                } else if (val instanceof Boolean) {
                    builder.append(val);
                } else if (val instanceof JSONObject) {
                    builder.append(val);
                } else if (val.getClass().isArray()) {
                    builder.append("[");
                    int len = Array.getLength(val);
                    for (int j = 0; j < len; j++) {
                        builder.append(Array.get(val, j).toString()).append(j != len - 1 ? "," : "");
                    }
                    builder.append("]");
                }

                builder.append(++i == entrySet.size() ? "}" : ",");
            }

            return builder.toString();
        }

        private String quote(String string) {
            return "\"" + string + "\"";
        }
    }

}