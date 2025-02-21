package com.jodexindustries.donatecase.common.animations;

import com.jodexindustries.donatecase.api.data.animation.JavaAnimation;
import com.jodexindustries.donatecase.api.tools.ProbabilityCollection;
import com.jodexindustries.donatecase.common.gui.items.OPENItemClickHandlerImpl;

public class RandomAnimation extends JavaAnimation {

    @Override
    public void start() {
        ProbabilityCollection<String> collection = new ProbabilityCollection<>();
        getSettings().childrenMap().forEach((key, value) -> collection.add((String) key, value.getInt()));
        getCaseData().animation(collection.get());
        end();
        OPENItemClickHandlerImpl.executeOpenWithoutEvent(getPlayer(), getLocation(), getCaseData(), true);
    }
}
