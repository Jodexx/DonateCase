package com.jodexindustries.donatecase.api.data.material;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Class for custom material storage
 * @since 2.2.4.8
 */
public class CaseMaterial implements MaterialHandler {
    private final MaterialHandler materialHandler;
    private final Addon addon;
    private final String id;
    private final String description;

    /**
     * Default constructor
     *
     * @param materialHandler Handler for creating item
     * @param addon           Material addon
     * @param id              Material id
     * @param description     Material description
     */
    public CaseMaterial(MaterialHandler materialHandler, Addon addon, String id, String description) {
        this.materialHandler = materialHandler;
        this.addon = addon;
        this.id = id;
        this.description = description;
    }

    /**
     * Gets addon which registered this material
     *
     * @return addon material
     */
    public Addon getAddon() {
        return addon;
    }

    /**
     * Gets material id, like: <code>BASE64</code>, <code>MCURL</code>
     *
     * @return material id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets material description
     *
     * @return material description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        return materialHandler.handle(context);
    }
}
