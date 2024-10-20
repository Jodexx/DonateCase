package com.jodexindustries.dceventmanager.data;

import java.util.List;

public class EventData {
    private final List<String> actions;
    private final String caseName;
    private final int slot;

    public EventData(List<String> actions, String caseName, int slot) {
        this.actions = actions;
        this.caseName = caseName;
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public String getCase() {
        return caseName;
    }

    public List<String> getActions() {
        return actions;
    }

}
