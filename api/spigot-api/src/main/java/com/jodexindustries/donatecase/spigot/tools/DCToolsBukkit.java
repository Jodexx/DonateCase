package com.jodexindustries.donatecase.spigot.tools;

import com.jodexindustries.donatecase.api.tools.DCTools;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class DCToolsBukkit extends DCTools {

    public static Color parseColor(String s) {
        Color color = getColor(s);
        if (color == null) color = fromRGBString(s, null);
        return color;
    }

    public static Color getColor(String color) {
        Field[] fields = Color.class.getFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())
                    && field.getType() == Color.class) {

                if (field.getName().equalsIgnoreCase(color)) {
                    try {
                        return (Color) field.get(null);
                    } catch (IllegalArgumentException | IllegalAccessException e1) {
                        return null;
                    }
                }

            }
        }
        return null;
    }

    public static Color fromRGB(Integer @NotNull [] rgb, Color def) {
        return rgb.length < 3 ? def : Color.fromRGB(rgb[0], rgb[1], rgb[2]);
    }

    public static Color fromRGBString(String string, Color def) {
        return fromRGB(parseRGB(string), def);
    }

}