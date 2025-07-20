package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.UUID;

@Getter
public abstract class Animation {

    private DCPlayer player;
    private CaseLocation location;
    private UUID uuid;
    private CaseDefinition caseDefinition;
    private CaseItem item;
    private ConfigurationNode settings;

    /**
     * @param uuid     Active case uuid
     * @param caseDefinition Case definition
     * @param winItem  winItem
     */
    public void init(DCPlayer player, CaseLocation location, UUID uuid, CaseDefinition caseDefinition,
                     CaseItem winItem, ConfigurationNode settings) {
        this.player = player;
        this.location = location;
        this.uuid = uuid;
        this.caseDefinition = caseDefinition;
        this.item = winItem;
        this.settings = settings;
    }

    public abstract void start();

    public Object getPlayer() {
        return player;
    }

    public final void preEnd() {
        DCAPI.getInstance().getAnimationManager().preEnd(getUuid());
    }

    public final void end() {
        DCAPI.getInstance().getAnimationManager().end(getUuid());
    }

    @Deprecated
    public CaseData getCaseData() {
        return CaseData.fromDefinition(caseDefinition);
    }

    @Deprecated
    public CaseDataItem getWinItem() {
        return CaseDataItem.fromItem(item);
    }
}
