package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class JavaAnimation<M extends CaseDataMaterial<I>, I, C> implements IAnimation {
    private UUID uuid;
    private C caseData;
    private CaseDataItem<M, I> winItem;

    /**
     * @param uuid     Active case uuid
     * @param caseData Case data
     * @param winItem  winItem
     */
    public void init(UUID uuid, C caseData,
                           CaseDataItem<M, I> winItem) {
        this.uuid = uuid;
        this.caseData = caseData;
        this.winItem = winItem;
    }

    @NotNull
    public final UUID getUuid() {
        return uuid;
    }

    @NotNull
    public final C getCaseData() {
        return caseData;
    }

    @NotNull
    public final CaseDataItem<M, I> getWinItem() {
        return winItem;
    }
}