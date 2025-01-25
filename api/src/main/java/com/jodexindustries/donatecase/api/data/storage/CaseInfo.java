package com.jodexindustries.donatecase.api.data.storage;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Getter
@ConfigSerializable
public class CaseInfo {
    private String type;
    private CaseLocation location;

    public CaseInfo() {}

    public CaseInfo(String type, CaseLocation location) {
        this.type = type;
        this.location = location;
    }
}
