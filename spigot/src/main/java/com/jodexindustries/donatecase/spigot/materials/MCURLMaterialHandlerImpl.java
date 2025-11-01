package com.jodexindustries.donatecase.spigot.materials;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.logging.Level;

public class MCURLMaterialHandlerImpl implements MaterialHandler {

    private static final String MINECRAFT_TEXTURES_URL = "http://textures.minecraft.net/texture/";

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        try {
            return (ItemStack) DCAPI.getInstance().getPlatform().getTools().createSkullFromTexture(urlToBase64(context));
        } catch (Exception e) {
            DCAPI.getInstance().getPlatform().getLogger().log(Level.WARNING, "Error with handling item: " + context, e);
        }

        return new ItemStack(Material.AIR);
    }

    private static String urlToBase64(String url) {
        URI actualUrl;
        try {
            actualUrl = new URI(MINECRAFT_TEXTURES_URL + url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + actualUrl + "\"}}}";
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }
}
