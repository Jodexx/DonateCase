package net.jodexindustries.tools;

import net.jodexindustries.dc.DonateCase;
import net.jodexindustries.tools.animations.FireworkShape;
import net.jodexindustries.tools.animations.Shape;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class StartAnimation {
    public static List<Player> caseOpen = new ArrayList();

    public StartAnimation(final Player player, Location location, final String c) {
        String animation = CustomConfig.getConfig().getString("DonatCase.Cases." + c + ".Animation").toUpperCase();
        if(animation.equalsIgnoreCase("SHAPE")) {
            new Shape(player, location, c);
        } else
        if(animation.equalsIgnoreCase("FIREWORK")) {
            new FireworkShape(player, location, c);
        } else {
            DonateCase.t.msg(player, DonateCase.t.rc("&cAn error occurred while opening the case!"));
            DonateCase.t.msg(player, DonateCase.t.rc("&cContact the project administration!"));
            DonateCase.instance.getLogger().log(Level.WARNING, "Case animation name does not exist!");
        }
    }
}
