package com.jodexindustries.dcblockanimations;

import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.event.Subscriber;
import com.jodexindustries.donatecase.api.event.animation.AnimationEndEvent;
import com.jodexindustries.donatecase.api.event.animation.AnimationStartEvent;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import net.kyori.event.method.annotation.Subscribe;
import org.bukkit.block.Block;
import org.bukkit.block.Lidded;

import java.util.HashMap;
import java.util.Map;

public class EventListener implements Subscriber {

    private static final Map<CaseLocation, Lidded> openedBlocks = new HashMap<>();

    private final MainAddon addon;

    public EventListener(MainAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onCaseOpen(AnimationStartEvent e) {
        ActiveCase activeCase = e.activeCase();
        if(addon.api.getAnimationManager().getActiveCasesByBlock().containsKey(activeCase.block())) return;

        String caseType = activeCase.caseType();
        if (!addon.getConfig().getConfig().getStringList("enabled-types").contains(caseType)) return;

        openBlock(activeCase.block());
    }

    @Subscribe
    public void onAnimationEnd(AnimationEndEvent e) {
        closeBlock(e.activeCase().block());
    }

    @Subscribe
    public void onConfigReload(DonateCaseReloadEvent e) {
        if (e.type() == DonateCaseReloadEvent.Type.CONFIG) addon.getConfig().load();
    }

    private void openBlock(CaseLocation caseLocation) {
        Block block = BukkitUtils.toBukkit(caseLocation).getBlock();
        if (block instanceof Lidded) {
            Lidded lidded = (Lidded) block;
            lidded.open();
            openedBlocks.put(caseLocation, lidded);
        }
    }

    private void closeBlock(CaseLocation block) {
        Lidded openedBlock = openedBlocks.get(block);
        if (openedBlock == null) return;

        openedBlocks.remove(block);
        openedBlock.close();
    }
}
