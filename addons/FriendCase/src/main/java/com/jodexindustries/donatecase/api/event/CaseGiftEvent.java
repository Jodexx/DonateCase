package com.jodexindustries.donatecase.api.event;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CaseGiftEvent extends DCEvent {

    private final DCPlayer sender;
    private final DCPlayer receiver;
    private final CaseData caseData;
    private final int keys;
}
