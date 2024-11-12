package com.jodexindustries.donatecase.api.addon.internal;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.CaseManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class InternalJavaAddonBukkit extends InternalJavaAddon implements InternalAddonBukkit {
    private CaseManager caseAPI;

    @Override
    void init(InternalAddonDescription description, File file, InternalAddonClassLoader loader) {
        super.init(description, file, loader);
        this.caseAPI = new CaseManager(this);
    }

    @Override
    public @NotNull CaseManager getCaseAPI() {
        return this.caseAPI;
    }

    @Override
    public @NotNull Plugin getDonateCaseBukkit() {
        return Case.getInstance();
    }
}
