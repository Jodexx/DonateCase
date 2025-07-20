package com.jodexindustries.dcblockanimations;

import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.event.Subscriber;
import com.jodexindustries.donatecase.api.event.animation.AnimationEndEvent;
import com.jodexindustries.donatecase.api.event.animation.AnimationPreStartEvent;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import net.kyori.event.method.annotation.Subscribe;
import org.bukkit.block.BlockState;
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
    public void onCaseOpen(AnimationPreStartEvent e) {
        if (!addon.getConfig().getEnabledTypes().contains(e.definition().settings().type())) return;

        if (MainAddon.api.getAnimationManager().getActiveCasesByBlock().containsKey(e.block())) return;

        MainAddon.api.getPlatform().getScheduler().run(addon, () -> openBlock(e.block()), 0L);
    }

    @Subscribe
    public void onAnimationEnd(AnimationEndEvent e) {
        closeBlock(e.activeCase().block());
    }

    @Subscribe
    public void onConfigReload(DonateCaseReloadEvent e) {
        if (e.type() == DonateCaseReloadEvent.Type.CONFIG) addon.load(true);
    }

    private void openBlock(CaseLocation caseLocation) {
        BlockState blockState = BukkitUtils.toBukkit(caseLocation).getBlock().getState();
        if (blockState instanceof Lidded) {
            Lidded lidded = (Lidded) blockState;
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
