package com.jodexindustries.donatecase.api.event.player;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.kyori.event.Cancellable;

/**
 * Called when a player tries to open a case via the open menu
 * <br>
 * Key checks has not started yet
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
@Data
public class PreOpenCaseEvent extends DCEvent implements Cancellable {

    private final DCPlayer player;
    private final CaseDefinition definition;
    private final CaseLocation block;
    private boolean cancelled;
    private boolean ignoreKeys;

    @Deprecated
    public CaseData getCaseData() {
        return CaseData.fromDefinition(definition);
    }

    @Override
    public boolean cancelled() {
        return cancelled;
    }

    @Override
    public void cancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
