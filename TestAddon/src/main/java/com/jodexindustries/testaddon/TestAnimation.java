package com.jodexindustries.testaddon;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.JavaAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TestAnimation extends JavaAnimation {
    private final Player player;
    private final Location location;
    private final UUID uuid;
    private final CaseData caseData;
    private final CaseData.Item winItem;

    public TestAnimation(Player player, Location location, UUID uuid, CaseData caseData, CaseData.Item winItem) {
        super(player, location, uuid, caseData, winItem);
        this.player = player;
        this.location = location;
        this.uuid = uuid;
        this.caseData = caseData;
        this.winItem = winItem;
    }

    @Override
    public void start() {
        Case.animationPreEnd(caseData, player, true, winItem);
        Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> Case.animationEnd(caseData, player, uuid, winItem),20L);
    }
}
