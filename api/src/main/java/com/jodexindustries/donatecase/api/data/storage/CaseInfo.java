package com.jodexindustries.donatecase.api.data.storage;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@Getter
@ConfigSerializable
public class CaseInfo {

    @Setting(nodeFromParent = true)
    private final String name;
    private final String type;
    private final CaseLocation location;

    public CaseInfo(String name, String type, CaseLocation location) {
        this.name = name;
        this.type = type;
        this.location = location;
    }
}
