package com.jodexindustries.donatecase.spigot.animations.select;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.animation.Animation;
import com.jodexindustries.donatecase.api.event.Subscriber;
import com.jodexindustries.donatecase.api.event.player.ArmorStandCreatorInteractEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import net.kyori.event.method.annotation.Subscribe;
import org.jetbrains.annotations.Nullable;

public class SelectAnimationListener implements Subscriber {

    private static final DCAPI api = DCAPI.getInstance();

    @Subscribe
    public void onInteract(ArmorStandCreatorInteractEvent event) {
        Animation animation = getAnimation(event.player());
        if (animation == null) return;
        if (!(animation instanceof SelectAnimation)) return;

        SelectAnimation selectAnimation = (SelectAnimation) animation;
        SelectAnimation.Task task = selectAnimation.getTask();
        if (task.selected || !task.canSelect) return;

        task.selected = true;

        ArmorStandCreator creator = event.armorStandCreator();

        creator.setEquipment(task.itemSlot, selectAnimation.getWinItem().material().itemStack());
        if (selectAnimation.getWinItem().material().displayName() != null && !selectAnimation.getWinItem().material().displayName().isEmpty())
            creator.setCustomNameVisible(true);
        creator.setCustomName(api.getPlatform().getPAPI().setPlaceholders(event.player(), selectAnimation.getWinItem().material().displayName()));
        creator.updateMeta();
    }

    @Nullable
    private Animation getAnimation(DCPlayer player) {
        for (ActiveCase activeCase : api.getAnimationManager().getActiveCases().values()) {
            if (activeCase.player().getUniqueId().equals(player.getUniqueId())) {
                return activeCase.animation();
            }
        }

        return null;
    }
}
