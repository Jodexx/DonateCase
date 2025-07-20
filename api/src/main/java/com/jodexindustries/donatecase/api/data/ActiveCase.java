package com.jodexindustries.donatecase.api.data;

import com.jodexindustries.donatecase.api.data.animation.Animation;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
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
    private final String caseType;
    private final Animation animation;

    private boolean locked;
    private boolean keyRemoved;

    /**
     * Default constructor
     *
     * @param block    Case block
     * @param caseType Case type
     */
    public ActiveCase(UUID uuid, CaseLocation block, DCPlayer player, CaseItem winItem, String caseType, Animation animation) {
        this.uuid = uuid;
        this.block = block;
        this.player = player;
        this.winItem = winItem;
        this.caseType = caseType;
        this.animation = animation;
    }
}