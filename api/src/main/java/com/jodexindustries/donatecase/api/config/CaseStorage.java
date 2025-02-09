package com.jodexindustries.donatecase.api.config;

import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Map;

public interface CaseStorage extends Loadable {

    void save(@NotNull String name, @NotNull CaseInfo caseInfo) throws ConfigurateException;

    void delete(String name);

    boolean delete(CaseLocation location);

    @Nullable
    CaseInfo get(String name);

    @Nullable
    CaseInfo get(CaseLocation location);

    @NotNull
    Map<String, CaseInfo> get();

    boolean has(String name);

    default boolean has(CaseLocation location) {
        return get(location) != null;
    }
}
