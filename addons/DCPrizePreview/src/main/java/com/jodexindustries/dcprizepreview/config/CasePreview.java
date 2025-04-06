package com.jodexindustries.dcprizepreview.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class CasePreview {

    @Setting("preview")
    private PreviewType preview;

    @Setting("command")
    private String command;

    public PreviewType type() {
        return preview;
    }

    @NotNull
    public String command() {
        return command;
    }

    public enum PreviewType {
        AUTO, COMMAND
    }
}
