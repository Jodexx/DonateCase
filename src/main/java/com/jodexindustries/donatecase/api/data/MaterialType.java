package com.jodexindustries.donatecase.api.data;

public enum MaterialType {
    /**
     * HeadDataBase
     */
    HDB,
    /**
     * CustomHeads
     */
    CH,
    /**
     * Minecraft head (by nick)
     */
    HEAD,
    /**
     * Minecraft BASE64 head
     */
    BASE64,

    /**
     * Minecraft url head
     */
    MCURL,
    /**
     * ItemsAdder
     */
    IA,
    /**
     * Default Bukkit material
     */
    DEFAULT;

    /**
     * Parse material type from string
     * @param material String, to be parsed
     * @return Parsed enum
     */
    public static MaterialType fromString(String material) {
        MaterialType materialType = DEFAULT;
        try {
            materialType = MaterialType.valueOf(material.toUpperCase());
        } catch (IllegalArgumentException ignored) {}
        return materialType;
    }
}
