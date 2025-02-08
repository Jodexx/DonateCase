package com.jodexindustries.donatecase.api.data.storage;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class CaseWorld {

    private final String name;
    private CaseLocation spawnLocation;

    public CaseWorld(String name) {
        this.name = name;
    }

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
