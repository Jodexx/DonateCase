package com.jodexindustries.donatecase.api.data.casedefinition;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Accessors(fluent = true, chain = false)
@Getter
public class CaseDefinition implements Cloneable {

    @NotNull
    private CaseSettings settings;

    @NotNull
    private CaseItems items;

    @NotNull
    private List<CaseMenu> menus;

    public CaseDefinition(@NotNull CaseSettings settings, @NotNull CaseItems items, @NotNull List<CaseMenu> menus) {
        this.settings = settings;
        this.items = items;
        this.menus = menus;
    }

    @NotNull
    public Optional<CaseMenu> getMenuById(@NotNull String id) {
        return menus.stream().filter(menu -> menu.id().equals(id)).findFirst();
    }

    @Override
    public CaseDefinition clone() {
        try {
            CaseDefinition clone = (CaseDefinition) super.clone();
            clone.settings = this.settings.clone();
            clone.items = this.items.clone();
            clone.menus = this.menus.stream().map(CaseMenu::clone).collect(Collectors.toList());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
