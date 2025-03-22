package com.jodexindustries.donatecase.spigot.animations;

public enum Facing {
    WEST(90),
    NORTH(180),
    EAST(270),
    SOUTH(0);

    public final float yaw;

    Facing(float yaw) {
        this.yaw = yaw;
    }
}