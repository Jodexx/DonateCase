package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.addon.Addon;

/**
 * Class for custom animation storage
 * @param <A> the type of JavaAnimation
 */
public class CaseAnimation<A> {
    private final Addon addon;
    private final String name;

    private Class<? extends A> animation;
    private String description;
    private boolean requireSettings;
    private boolean removeKeyAtStart;

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

    public boolean isRemoveKeyAtStart() {
        return removeKeyAtStart;
    }

    public static class Builder<A> {
        private final Addon addon;
        private final String name;

        private String description;
        private boolean requireSettings;
        private boolean removeKeyAtStart;
        private Class<? extends A> animation;

        public Builder(String name, Addon addon) {
            this.addon = addon;
            this.name = name;
        }

        public Builder<A> animation(Class<? extends A> animation) {
            this.animation = animation;
            return this;
        }

        public Builder<A> description(String description) {
            this.description = description;
            return this;
        }

        public Builder<A> requireSettings(boolean requireSettings) {
            this.requireSettings = requireSettings;
            return this;
        }

        public Builder<A> removeKeyAtStart(boolean removeKeyAtStart) {
            this.removeKeyAtStart = removeKeyAtStart;
            return this;
        }

        public CaseAnimation<A> build() {
            CaseAnimation<A> caseAnimation = new CaseAnimation<>(name, addon);
            caseAnimation.animation = animation;
            caseAnimation.description = description;
            caseAnimation.requireSettings = requireSettings;
            caseAnimation.removeKeyAtStart = removeKeyAtStart;
            return caseAnimation;
        }
    }

}
