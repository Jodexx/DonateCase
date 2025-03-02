package com.jodexindustries.dceventmanager.data;

import lombok.Getter;

@Getter
public class Placeholder {
    private final String name;
    private final String method;

    public Placeholder(String name, String method) {
        this.name = name;
        this.method = method;
    }

}
