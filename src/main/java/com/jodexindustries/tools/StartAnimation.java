package com.jodexindustries.tools;

import com.jodexindustries.tools.animations.FireworkShape;
import com.jodexindustries.tools.animations.Shape;
import com.jodexindustries.dc.Main;
import com.jodexindustries.tools.animations.Rainly;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class StartAnimation {
    public static List<Player> caseOpen = new ArrayList<>();

    public StartAnimation(final Player player, Location location, final String c) {
        String animation = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Animation").toUpperCase();
        if (animation.equalsIgnoreCase("SHAPE")) {
            new Shape(player, location, c);
        } else
        if (animation.equalsIgnoreCase("FIREWORK")) {
            new FireworkShape(player, location, c);
        } else
        if (animation.equalsIgnoreCase("RAINLY")) {
            new Rainly(player, location, c);
        } else {
            Main.t.msg(player, Main.t.rc("&cAn error occurred while opening the case!"));
            Main.t.msg(player, Main.t.rc("&cContact the project administration!"));
            Main.instance.getLogger().log(Level.WARNING, "Case animation name does not exist!");
        }
    }
}
