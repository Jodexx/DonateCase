package com.jodexindustries.donatecase.api.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface DCTools {

    static String getLocalPlaceholder(String string) {
        Pattern pattern = Pattern.compile("%(.*?)%");
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            int startIndex = string.indexOf("%") + 1;
            int endIndex = string.lastIndexOf("%");
            return string.substring(startIndex, endIndex);
        } else {
            return "null";
        }
    }

    /**
     * Parse version from string
     * @param version String, to be parsed
     * @return numbered version.
     * <br>
     * Example: <br>
     * Input text: <code>2.2.2</code> <br>
     * Output: <code>2220</code> <br>
     * Input text: <code>2.2.2.2</code> <br>
     * Output: <code>2222</code>
     */
    static int getPluginVersion(String version) {
        StringBuilder builder = new StringBuilder();
        version = version.replaceAll("\\.", "");
        if(version.length() < 4) {
            for (int i = 0; i < 4 - version.length(); i++) builder.append(version).append("0");
        } else {
            builder.append(version);
        }
        return Integer.parseInt(builder.toString());
    }

    /**
     * Extract cooldown from action string
     * @param action Action string. Format [cooldown:int]
     * @return cooldown
     */
    static int extractCooldown(String action) {
        Pattern pattern = Pattern.compile("\\[cooldown:(.*?)]");
        Matcher matcher = pattern.matcher(action);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
}