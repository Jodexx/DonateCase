package com.jodexindustries.testpluginaddon;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.Animation;
import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TestAnimation implements Animation {
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void start(Player player, Location location, UUID uuid, CaseData caseData, CaseData.Item item) {
        Case.onCaseOpenFinish(caseData, player, true, item);
        Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> Case.animationEnd(caseData, getName(), player, uuid, item),20L);
    }
}
