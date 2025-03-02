package com.jodexindustries.dceventmanager.data;

import lombok.Getter;

import java.util.List;

@Getter
public class EventData {
    private final List<String> actions;
    private final String caseType;
    private final int slot;

    public EventData(List<String> actions, String caseType, int slot) {
        this.actions = actions;
        this.caseType = caseType;
        this.slot = slot;
    }

}
