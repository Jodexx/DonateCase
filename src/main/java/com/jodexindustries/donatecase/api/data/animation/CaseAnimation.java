package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.JavaAnimation;

public final class CaseAnimation {
    private final Class<? extends JavaAnimation> animation;
    private final Addon addon;
    private final String name;
    private final String description;

    public CaseAnimation(Class<? extends JavaAnimation> animation, Addon addon, String name, String description) {
        this.animation = animation;
        this.addon = addon;
        this.name = name;
        this.description = description;
    }

    public Class<? extends JavaAnimation> getAnimation() {
        return animation;
    }

    public Addon getAddon() {
        return addon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
