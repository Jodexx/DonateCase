package com.jodexindustries.testaddon;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.JavaAnimation;
import org.bukkit.Bukkit;

public class TestAnimation extends JavaAnimation {
    @Override
    public void start() {
        Case.animationPreEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());
        Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> Case.animationEnd(getCaseData(), getPlayer(), getUuid(), getWinItem()),20L);
    }
}
