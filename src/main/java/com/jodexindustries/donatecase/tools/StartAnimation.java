package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.logging.Level;

import static com.jodexindustries.donatecase.dc.Main.casesConfig;

public class StartAnimation {

    public StartAnimation(final Player player, Location location, final String c) {
        String animation = casesConfig.getCase(c).getString("case.Animation");
        if(animation != null) {
            if(AnimationManager.isRegistered(animation)) {
                AnimationManager.playAnimation(animation, player, location, c);
            } else {
                Main.t.msg(player, Main.t.rc("&cAn error occurred while opening the case!"));
                Main.t.msg(player, Main.t.rc("&cContact the project administration!"));
                Main.instance.getLogger().log(Level.WARNING, "Case animation "  + animation + " does not exist!");
            }
        } else {
            Main.t.msg(player, Main.t.rc("&cAn error occurred while opening the case!"));
            Main.t.msg(player, Main.t.rc("&cContact the project administration!"));
            Main.instance.getLogger().log(Level.WARNING, "Case animation name does not exist!");
        }
    }
}
