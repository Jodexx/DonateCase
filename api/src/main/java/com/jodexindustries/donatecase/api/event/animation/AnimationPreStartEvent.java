package com.jodexindustries.donatecase.api.event.animation;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class AnimationPreStartEvent extends DCEvent {

    private final DCPlayer player;
    private final CaseDefinition definition;
    private final CaseLocation block;

    @NotNull
    private CaseItem winItem;

    @Deprecated
    public CaseData getCaseData() {
        return CaseData.fromDefinition(definition);
    }

}
