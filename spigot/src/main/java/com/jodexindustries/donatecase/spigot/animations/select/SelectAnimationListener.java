package com.jodexindustries.donatecase.spigot.animations.select;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.animation.Animation;
import com.jodexindustries.donatecase.api.event.player.ArmorStandCreatorInteractEvent;
import net.kyori.event.EventSubscriber;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SelectAnimationListener implements EventSubscriber<ArmorStandCreatorInteractEvent> {

    private static final DCAPI api = DCAPI.getInstance();

    @Override
    public void invoke(@NonNull ArmorStandCreatorInteractEvent event) {
        ArmorStandCreator creator = event.armorStandCreator();
        SelectAnimation animation = getAnimation(creator.getAnimationId());
        if (animation == null) return;

        SelectAnimation.Task task = animation.getTask();
        if (task.selected || !task.canSelect) return;

        if(!animation.getPlayer().equals(event.player())) return;

        task.selected = true;

        creator.setEquipment(animation.settings.itemSlot, animation.getWinItem().material().itemStack());
        if (animation.getWinItem().material().displayName() != null && !animation.getWinItem().material().displayName().isEmpty())
            creator.setCustomNameVisible(true);
        creator.setCustomName(api.getPlatform().getPAPI().setPlaceholders(event.player(), animation.getWinItem().material().displayName()));
        creator.updateMeta();
    }

    @Nullable
    private SelectAnimation getAnimation(UUID uuid) {
        ActiveCase activeCase = api.getAnimationManager().getActiveCases().get(uuid);
        if (activeCase == null) return null;

        Animation animation = activeCase.animation();
        if (!(animation instanceof SelectAnimation)) return null;

        return (SelectAnimation) animation;
    }
}
