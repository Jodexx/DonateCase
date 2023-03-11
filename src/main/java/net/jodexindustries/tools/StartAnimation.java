package net.jodexindustries.tools;

import net.jodexindustries.tools.animations.Firework;
import net.jodexindustries.tools.animations.Shape;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StartAnimation {
    public static List<Player> caseOpen = new ArrayList();

    public StartAnimation(final Player player, Location location, final String c) {
        String animation = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Animation").toUpperCase();
        if(animation.equalsIgnoreCase("SHAPE")) {
            new Shape(player, location, c);
        }
        if(animation.equalsIgnoreCase("FIREWORK")) {
            new Firework(player, location, c);
        }
    }
}
