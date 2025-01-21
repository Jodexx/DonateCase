package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.addon.Addon;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Class for custom animation storage
 */
@Builder
@Getter
@Setter
public class CaseAnimation {
    private final Addon addon;
    private final String name;

    private Class<? extends Animation> animation;
    private String description;
    private boolean requireBlock = true;
    private boolean requireSettings;
    private boolean removeKeyAtStart;

    public CaseAnimation(String name, Addon addon) {
        this.addon = addon;
        this.name = name;
    }

}
