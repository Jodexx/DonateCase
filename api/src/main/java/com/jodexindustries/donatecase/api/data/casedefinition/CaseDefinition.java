package com.jodexindustries.donatecase.api.data.casedefinition;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
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

    @NotNull
    @Setter
    private CaseMenu defaultMenu;

    public CaseDefinition(@NotNull CaseSettings settings, @NotNull CaseItems items, @NotNull List<CaseMenu> menus) {
        this(settings, items, menus, null);
    }

    public CaseDefinition(@NotNull CaseSettings settings, @NotNull CaseItems items, @NotNull List<CaseMenu> menus, @Nullable CaseMenu defaultMenu) {
        this.settings = settings;
        this.items = items;
        this.menus = menus;
        this.defaultMenu = defaultMenu == null ? getMenuById(settings.defaultMenu()).orElseGet(() -> menus.get(0)) : defaultMenu;
    }

    @NotNull
    public List<CaseMenu> menus() {
        return Collections.unmodifiableList(menus);
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
