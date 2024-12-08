package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;

/**
 * Class for custom animation storage
 */
public class CaseAnimation<A extends JavaAnimation<M, I>, M extends CaseDataMaterial<I>, I> {
    private final Addon addon;
    private final String name;

    private Class<? extends A> animation;
    private String description;
    private boolean requireSettings;

    public CaseAnimation(String name, Addon addon) {
        this.addon = addon;
        this.name = name;
    }

    /**
     * Gets animation class
     *
     * @return animation class
     */
    public Class<? extends A> getAnimation() {
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

    public boolean isRequireSettings() {
        return requireSettings;
    }

    public void setAnimation(Class<? extends A> animation) {
        this.animation = animation;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequireSettings(boolean requireSettings) {
        this.requireSettings = requireSettings;
    }

    public static class Builder<A extends JavaAnimation<M, I>, M extends CaseDataMaterial<I>, I> {
        private final Addon addon;
        private final String name;

        private String description;
        private boolean requireSettings;
        private Class<? extends A> animation;

        public Builder(String name, Addon addon) {
            this.addon = addon;
            this.name = name;
        }

        public Builder<A, M, I> animation(Class<? extends A> animation) {
            this.animation = animation;
            return this;
        }

        public Builder<A, M, I> description(String description) {
            this.description = description;
            return this;
        }

        public Builder<A, M, I> requireSettings(boolean requireSettings) {
            this.requireSettings = requireSettings;
            return this;
        }

        public CaseAnimation<A, M, I> build() {
            CaseAnimation<A, M, I> caseAnimation = new CaseAnimation<>(name, addon);
            caseAnimation.setAnimation(animation);
            caseAnimation.setDescription(description);
            caseAnimation.setRequireSettings(requireSettings);
            return caseAnimation;
        }
    }

}
