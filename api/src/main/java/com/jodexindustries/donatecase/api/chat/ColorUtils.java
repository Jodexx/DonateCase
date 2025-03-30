package com.jodexindustries.donatecase.api.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class ColorUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");

    public static String color(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            String replacement = "&x&" + hex.charAt(0) + "&" + hex.charAt(1) + "&" + hex.charAt(2) + "&" +
                    hex.charAt(3) + "&" + hex.charAt(4) + "&" + hex.charAt(5);
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return translate(sb.toString());
    }

    public static @NotNull String translate(@NotNull String textToTranslate) {
        if (!textToTranslate.contains("&")) return textToTranslate;
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx#".indexOf(b[i + 1]) > -1) {
                b[i] = '§';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
}
