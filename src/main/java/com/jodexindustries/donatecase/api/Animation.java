package com.jodexindustries.donatecase.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Animation {

    String getName();

    void start(Player player, Location location, String c);
}
