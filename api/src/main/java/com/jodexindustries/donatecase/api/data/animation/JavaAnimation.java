package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class JavaAnimation<M extends CaseDataMaterial<I>, I> implements IAnimation {
    private UUID uuid;
    private CaseData<M> caseData;
    private CaseDataItem<M> winItem;

    /**
     * @param uuid     Active case uuid
     * @param caseData Case data
     * @param winItem  winItem
     */
    public void init(UUID uuid, CaseData<M> caseData,
                           CaseDataItem<M> winItem) {
        this.uuid = uuid;
        this.caseData = caseData;
        this.winItem = winItem;
    }

    @NotNull
    public final UUID getUuid() {
        return uuid;
    }

    @NotNull
    public final CaseData<M> getCaseData() {
        return caseData;
    }

    @NotNull
    public final CaseDataItem<M> getWinItem() {
        return winItem;
    }
}