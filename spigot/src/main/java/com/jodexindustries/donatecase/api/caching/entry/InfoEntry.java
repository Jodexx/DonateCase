package com.jodexindustries.donatecase.api.caching.entry;

import java.util.Objects;

public class InfoEntry {
    private final String player;
    private final String caseType;

    public InfoEntry(String player, String caseType) {
        this.player = player;
        this.caseType = caseType;
    }

    public String getCaseType() {
        return caseType;
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "InfoEntry{" +
                "player='" + player + '\'' +
                ", caseType='" + caseType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoEntry infoEntry = (InfoEntry) o;
        return Objects.equals(player, infoEntry.player) && Objects.equals(caseType, infoEntry.caseType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, caseType);
    }
}
