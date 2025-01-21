package com.jodexindustries.donatecase.api.animation;

import com.jodexindustries.donatecase.api.data.animation.Animation;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.bukkit.entity.Player;

public abstract class BukkitJavaAnimation extends Animation {

    @Override
    public Player getPlayer() {
        return (Player) ((DCPlayer) super.getPlayer()).getHandler();
    }
}
