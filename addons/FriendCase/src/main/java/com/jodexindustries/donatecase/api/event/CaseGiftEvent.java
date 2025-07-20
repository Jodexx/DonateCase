package com.jodexindustries.donatecase.api.event;

import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CaseGiftEvent extends DCEvent {

    private final DCPlayer sender;
    private final DCPlayer receiver;
    private final CaseDefinition definition;
    private final int keys;
}
