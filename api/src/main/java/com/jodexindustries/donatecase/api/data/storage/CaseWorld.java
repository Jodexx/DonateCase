package com.jodexindustries.donatecase.api.data.storage;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;


@Accessors(fluent = true)
@Data
public class CaseWorld {

    private final String name;
    private CaseLocation spawnLocation;

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        CaseWorld caseWorld = (CaseWorld) object;
        return caseWorld.name.equals(name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return "CaseWorld{" +
                "name='" + name + '\'' +
                '}';
    }
}
