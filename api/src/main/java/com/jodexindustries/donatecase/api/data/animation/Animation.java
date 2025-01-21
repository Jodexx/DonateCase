package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
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
    private CaseData caseData;
    private CaseDataItem winItem;
    private ConfigurationNode settings;

    /**
     * @param uuid     Active case uuid
     * @param caseData Case data
     * @param winItem  winItem
     */
    public void init(DCPlayer player, CaseLocation location, UUID uuid, CaseData caseData,
                     CaseDataItem winItem, ConfigurationNode settings) {
        this.player = player;
        this.location = location;
        this.uuid = uuid;
        this.caseData = caseData;
        this.winItem = winItem;
        this.settings = settings;
    }

    public abstract void start();

    public Object getPlayer() {
        return player;
    }

    public final void preEnd() {
        DCAPI.getInstance().getAnimationManager().animationPreEnd(getUuid());
    }

    public final void end() {
        DCAPI.getInstance().getAnimationManager().animationEnd(getUuid());
    }
}
