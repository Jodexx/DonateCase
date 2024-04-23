package com.jodexindustries.testaddon;

import com.jodexindustries.donatecase.api.CaseAPI;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.Animation;
import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TestAnimation implements Animation {
    private final Addon addon;
    public TestAnimation(Addon addon) {
        this.addon = addon;
    }
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void start(Player player, Location location, UUID uuid, CaseData caseData, CaseData.Item item) {
        addon.getCaseAPI().onCaseOpenFinish(caseData, player, true, item);
        Bukkit.getScheduler().runTaskLater(addon.getDonateCase(), () -> addon.getCaseAPI().animationEnd(caseData, getName(), player, uuid, item),20L);
    }
}
