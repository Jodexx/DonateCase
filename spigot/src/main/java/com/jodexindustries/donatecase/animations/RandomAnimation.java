package com.jodexindustries.donatecase.animations;

import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.manager.AnimationManager;
import com.jodexindustries.donatecase.api.tools.ProbabilityCollection;
import com.jodexindustries.donatecase.gui.items.OPENItemClickHandlerImpl;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class RandomAnimation extends JavaAnimationBukkit {

    public static void register(AnimationManager<JavaAnimationBukkit, CaseDataMaterialBukkit, Player, Location, Block, CaseDataBukkit> manager) {
        CaseAnimation<JavaAnimationBukkit> caseAnimation = manager.builder("RANDOM")
                .animation(RandomAnimation.class)
                .description("Selects the random animation from config")
                .requireSettings(true)
                .build();

        manager.registerAnimation(caseAnimation);
    }

    @Override
    public void start() {
        ProbabilityCollection<String> collection = new ProbabilityCollection<>();
        getSettings().getKeys(false).forEach(animation -> collection.add(animation, getSettings().getInt(animation)));
        getCaseData().setAnimation(collection.get());
        end();
        OPENItemClickHandlerImpl.executeOpenWithoutEvent(getPlayer(), getLocation(), getCaseData(), true);
    }
}
