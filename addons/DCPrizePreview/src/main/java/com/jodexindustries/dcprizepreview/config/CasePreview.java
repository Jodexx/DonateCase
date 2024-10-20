package com.jodexindustries.dcprizepreview.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class CasePreview {
    private final PreviewType type;
    private final String command;

    public CasePreview(@NotNull PreviewType type, @NotNull String command) {
        this.type = type;
        this.command = command;
    }

    @NotNull
    public static CasePreview deserialize(@NotNull ConfigurationSection section) {
        String type = section.getString("preview", "AUTO");
        String command = section.getString("command", "");
        return new CasePreview(PreviewType.type(type), command);
    }

    public PreviewType type() {
        return type;
    }

    @NotNull
    public String command() {
        return command;
    }

    public enum PreviewType {
        AUTO, COMMAND;

        public static PreviewType type(@NotNull String string) {
            try {
                return valueOf(string.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }

            return AUTO;
        }
    }
}
