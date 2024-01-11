package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class StartAnimation {

    public StartAnimation(Player player, Location location, String c) {
        CaseData caseData = Case.getCase(c).clone();
        String animation = caseData.getAnimation();
        if(animation != null) {
            if(AnimationManager.isRegistered(animation)) {
                AnimationManager.playAnimation(animation, player, location, caseData);
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
