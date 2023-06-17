package com.jodexindustries.donatecase.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Animation {
    public abstract void start(Player player, Location location, String c);
}
