package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.manager.CaseManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CaseManagerImpl implements CaseManager {

    private final static Map<String, CaseData> caseData = new ConcurrentHashMap<>();

    @Override
    public boolean hasByType(@NotNull String type) {
        return caseData.containsKey(type);
    }

    @Override
    public @Nullable CaseData get(@NotNull String type) {
        return caseData.get(type);
    }

    @Override
    public Map<String, CaseData> getMap() {
        return caseData;
    }
}
