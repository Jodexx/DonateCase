package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.animations.FireworkShape;
import com.jodexindustries.donatecase.tools.animations.Rainly;
import com.jodexindustries.donatecase.tools.animations.Shape;
import com.jodexindustries.donatecase.tools.animations.Wheel;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class StartAnimation {
    public static List<Player> caseOpen = new ArrayList<>();

    public StartAnimation(final Player player, Location location, final String c) {
        String animation = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Animation");
        if(animation != null) {
            if (animation.equalsIgnoreCase("SHAPE")) {
                new Shape(player, location, c);
            } else if (animation.equalsIgnoreCase("FIREWORK")) {
                new FireworkShape(player, location, c);
            } else if (animation.equalsIgnoreCase("RAINLY")) {
                new Rainly(player, location, c);
            } else if (animation.equalsIgnoreCase("WHEEL")) {
                new Wheel(player, location, c);
            } else {
                Main.t.msg(player, Main.t.rc("&cAn error occurred while opening the case!"));
                Main.t.msg(player, Main.t.rc("&cContact the project administration!"));
                Main.instance.getLogger().log(Level.WARNING, "Case animation name does not exist!");
            }
        } else {
            Main.t.msg(player, Main.t.rc("&cAn error occurred while opening the case!"));
            Main.t.msg(player, Main.t.rc("&cContact the project administration!"));
            Main.instance.getLogger().log(Level.WARNING, "Case animation name does not exist!");
        }
    }
}
