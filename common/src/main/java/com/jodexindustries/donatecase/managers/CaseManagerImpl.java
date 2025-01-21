package com.jodexindustries.donatecase.managers;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.manager.CaseManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CaseManagerImpl implements CaseManager {

    private final static Map<String, CaseData> caseData = new HashMap<>();

    @Override
    public boolean hasCaseByType(@NotNull String type) {
        return !caseData.isEmpty() && caseData.containsKey(type);
    }

    @Override
    public @Nullable CaseData getCase(@NotNull String type) {
        return caseData.get(type);
    }

    @Override
    public Map<String, CaseData> getMap() {
        return caseData;
    }
}
