package com.jodexindustries.testaddon;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.Animation;
import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class TestAnimation implements Animation {
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void start(org.bukkit.entity.Player player, org.bukkit.Location location, CaseData c, CaseData.Item winItem) {
        Location loc = location.clone();
        Case.onCaseOpenFinish(c,player,true, winItem);
        Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> Case.animationEnd(c, getName(), player, loc, winItem),20L);
    }
}
