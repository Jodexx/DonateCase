package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.manager.CaseManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CaseManagerImpl implements CaseManager {

    public final Map<String, CaseDefinition> caseDefinitionMap = new ConcurrentHashMap<>();

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

}
