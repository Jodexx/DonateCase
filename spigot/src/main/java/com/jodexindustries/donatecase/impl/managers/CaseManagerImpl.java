package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.manager.CaseManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CaseManagerImpl implements CaseManager<CaseDataBukkit> {

    private final static Map<String, CaseDataBukkit> caseData = new HashMap<>();


    @Override
    public boolean hasCaseByType(@NotNull String type) {
        return !caseData.isEmpty() && caseData.containsKey(type);
    }

    @Override
    public @Nullable CaseDataBukkit getCase(@NotNull String type) {
        return caseData.get(type);
    }

    @Override
    public Map<String, CaseDataBukkit> getMap() {
        return caseData;
    }
}
