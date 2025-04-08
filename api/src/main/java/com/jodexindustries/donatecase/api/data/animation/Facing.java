package com.jodexindustries.donatecase.api.data.animation;

public enum Facing {
    // Horizontal
    NORTH(180, 0),
    NORTH_EAST(225, 0),
    EAST(270, 0),
    SOUTH_EAST(315, 0),
    SOUTH(0, 0),
    SOUTH_WEST(45, 0),
    WEST(90, 0),
    NORTH_WEST(135, 0),

    // Vertical
    UP(0, -90),
    DOWN(0, 90),

    // Vertical Diagonals
    UP_NORTH(180, -45),
    UP_EAST(270, -45),
    UP_SOUTH(0, -45),
    UP_WEST(90, -45),

    DOWN_NORTH(180, 45),
    DOWN_EAST(270, 45),
    DOWN_SOUTH(0, 45),
    DOWN_WEST(90, 45);

    public final float yaw;
    public final float pitch;

    Facing(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
