package com.jodexindustries.donatecase.api.data;

import com.jodexindustries.donatecase.api.data.animation.Animation;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

/**
 * Class for saving active cases data
 */
@Accessors(fluent = true)
@Getter
@Setter
public class ActiveCase {

    private final UUID uuid;
    private final CaseLocation block;
    private final DCPlayer player;
    private final CaseItem winItem;
    private final CaseDefinition definition;
    private final Animation animation;

    private boolean locked;
    private boolean keyRemoved;

    public ActiveCase(UUID uuid, CaseLocation block, DCPlayer player, CaseItem winItem, CaseDefinition definition, Animation animation) {
        this.uuid = uuid;
        this.block = block;
        this.player = player;
        this.winItem = winItem;
        this.definition = definition;
        this.animation = animation;
    }

    public String caseType() {
        return definition.settings().type();
    }
}