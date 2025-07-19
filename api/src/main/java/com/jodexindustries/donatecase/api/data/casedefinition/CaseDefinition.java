package com.jodexindustries.donatecase.api.data.casedefinition;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true, chain = false)
@Getter
public class CaseDefinition {

    @NotNull
    private final CaseSettings settings;

    @NotNull
    private final CaseItems items;

    @NotNull
    private final List<CaseMenu> menus;

    public CaseDefinition(@NotNull CaseSettings settings, @NotNull CaseItems items, @NotNull List<CaseMenu> menus) {
        this.settings = settings;
        this.items = items;
        this.menus = menus;
    }

    @Nullable
    public CaseMenu getMenuById(@NotNull String id) {
        return menus.stream().filter(menu -> menu.id().equals(id)).findFirst().orElse(null);
    }
}
