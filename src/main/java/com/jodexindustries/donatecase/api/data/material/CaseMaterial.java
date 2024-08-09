package com.jodexindustries.donatecase.api.data.material;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @since 2.2.4.8
 */
public class CaseMaterial implements MaterialHandler {
    private final MaterialHandler materialHandler;
    private final Addon addon;
    private final String id;
    private final String description;

    public CaseMaterial(MaterialHandler materialHandler, Addon addon, String id, String description) {
        this.materialHandler = materialHandler;
        this.addon = addon;
        this.id = id;
        this.description = description;
    }

    public Addon getAddon() {
        return addon;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        return materialHandler.handle(context);
    }
}
