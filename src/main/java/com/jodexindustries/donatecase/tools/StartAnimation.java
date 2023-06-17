package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.logging.Level;

import static com.jodexindustries.donatecase.dc.Main.customConfig;

public class StartAnimation {

    public StartAnimation(final Player player, Location location, final String c) {
        String animation = customConfig.getConfig().getString("DonatCase.Cases." + c + ".Animation");
        if(animation != null) {
            Case.caseOpen.add(player);
            if(AnimationManager.isRegistered(animation)) {
                AnimationManager.registeredAnimations.put(animation, AnimationManager.getRegisteredAnimations().get(animation));
                AnimationManager.playAnimation(animation, player, location, c);
            } else {
                Case.caseOpen.remove(player);
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
