package com.jodexindustries.donatecase.api.data.material;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.jetbrains.annotations.NotNull;

/**
 * Class for custom material storage
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

    /**
     * Gets material handler
     *
     * @return material handler
     */
    public MaterialHandler getMaterialHandler() {
        return materialHandler;
    }

    @Override
    public @NotNull Object handle(@NotNull String context) {
        return materialHandler.handle(context);
    }
}
