package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.Animation;
import com.jodexindustries.donatecase.api.data.JavaAnimation;
import org.jetbrains.annotations.Nullable;

/**
 * Class for custom animation storage
 */
public class CaseAnimation {
    private final Class<? extends JavaAnimation> animation;
    private final Animation oldAnimation;
    private final Addon addon;
    private final String name;
    private final String description;

    /**
     * Default constructor
     *
     * @param animation   Animation class
     * @param addon       Animation addon
     * @param name        Animation name
     * @param description Animation description
     */
    public CaseAnimation(Class<? extends JavaAnimation> animation, Addon addon, String name, String description) {
        this.animation = animation;
        this.oldAnimation = null;
        this.addon = addon;
        this.name = name;
        this.description = description;
    }

    @Deprecated
    public CaseAnimation(Animation oldAnimation, Addon addon, String name, String description) {
        this.animation = null;
        this.oldAnimation = oldAnimation;
        this.addon = addon;
        this.name = name;
        this.description = description;
    }

    /**
     * Gets animation class
     *
     * @return animation class
     */
    @Nullable
    public Class<? extends JavaAnimation> getAnimation() {
        return animation;
    }

    /**
     * Gets addon which registered this animation
     *
     * @return addon animation
     */
    public Addon getAddon() {
        return addon;
    }

    /**
     * Gets animation name
     *
     * @return animation name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets animation description
     *
     * @return animation description
     */
    public String getDescription() {
        return description;
    }

    @Nullable
    public Animation getOldAnimation() {
        return oldAnimation;
    }

}
