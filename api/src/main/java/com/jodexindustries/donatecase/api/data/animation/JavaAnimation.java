package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.platform.DCPlayer;

public abstract class JavaAnimation extends Animation {

    @Override
    public DCPlayer getPlayer() {
        return (DCPlayer) super.getPlayer();
    }
}