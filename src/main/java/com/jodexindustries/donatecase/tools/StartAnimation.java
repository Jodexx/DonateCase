package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.DonateCase;
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
                DonateCase.t.msg(player, DonateCase.t.rc("&cAn error occurred while opening the case!"));
                DonateCase.t.msg(player, DonateCase.t.rc("&cContact the project administration!"));
                DonateCase.instance.getLogger().log(Level.WARNING, "Case animation "  + animation + " does not exist!");
            }
        } else {
            DonateCase.t.msg(player, DonateCase.t.rc("&cAn error occurred while opening the case!"));
            DonateCase.t.msg(player, DonateCase.t.rc("&cContact the project administration!"));
            DonateCase.instance.getLogger().log(Level.WARNING, "Case animation name does not exist!");
        }
    }
}
