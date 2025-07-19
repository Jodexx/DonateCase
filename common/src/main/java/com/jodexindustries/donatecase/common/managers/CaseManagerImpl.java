package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.manager.CaseManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CaseManagerImpl implements CaseManager {

    public final Map<String, CaseDefinition> caseDefinitionMap = new ConcurrentHashMap<>();

    @Deprecated
    private final static Map<String, CaseData> caseDataMap = new ConcurrentHashMap<>();

    @Override
    public boolean hasByType(@NotNull String type) {
        return caseDefinitionMap.containsKey(type);
    }

    @Override
    public @NotNull Optional<CaseDefinition> getByType(@NotNull String type) {
        return Optional.ofNullable(caseDefinitionMap.get(type));
    }

    @Override
    public @NotNull Collection<CaseDefinition> definitions() {
        return Collections.unmodifiableCollection(caseDefinitionMap.values());
    }

    @Deprecated
    @Override
    public @Nullable CaseData get(@NotNull String type) {
        return caseDataMap.computeIfAbsent(type, key -> getByType(key).map(this::createFromDefinition).orElse(null));
    }

    @Deprecated
    @Override
    public Map<String, CaseData> getMap() {
        if (caseDataMap.size() != caseDefinitionMap.size()) {
            for (Map.Entry<String, CaseDefinition> entry : caseDefinitionMap.entrySet()) {
                caseDataMap.computeIfAbsent(entry.getKey(), key -> createFromDefinition(entry.getValue()));
            }
        }
        return caseDataMap;
    }

    @Deprecated
    private @Nullable CaseData createFromDefinition(@Nullable CaseDefinition definition) {
        if (definition == null) return null;
        return CaseData.fromDefinition(definition);
    }

}
