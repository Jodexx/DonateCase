package com.jodexindustries.donatecase.api.data.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Accessors(fluent = true)
@Data
@AllArgsConstructor
@ConfigSerializable
public class CaseInfo {

    private String type;
    private CaseLocation location;

    public CaseInfo() {}
}
