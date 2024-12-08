package com.jodexindustries.donatecase.api.tools;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataHistory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility interface for the DonateCase system, providing tools for parsing, validation, and manipulation.
 */
public interface DCTools {

    /**
     * Extracts the local placeholder from a string, delimited by `%`.
     *
     * @param string the input string containing placeholders in the format `%placeholder%`.
     * @return the extracted placeholder without `%` symbols, or "null" if no placeholder is found.
     */
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
     * Parses a plugin version string into a numbered version.
     *
     * @param version the version string to be parsed (e.g., "2.2.2").
     * @return the numeric representation of the version (e.g., "2220").
     * <p>
     * Examples:
     * <ul>
     *     <li>Input: <code>"2.2.2"</code> → Output: <code>2220</code></li>
     *     <li>Input: <code>"2.2.2.2"</code> → Output: <code>2222</code></li>
     * </ul>
     */
    static int getPluginVersion(String version) {
        version = version.replaceAll("\\.", "");
        if (version.length() == 4) {
            return Integer.parseInt(version);
        }
        version = version.concat("0000");
        return Integer.parseInt(version.substring(0, 4));
    }

    /**
     * Extracts a cooldown value from an action string.
     *
     * @param action the action string containing a cooldown in the format <code>[cooldown:int]</code>.
     * @return the cooldown value, or 0 if no cooldown is specified.
     */
    static int extractCooldown(String action) {
        Pattern pattern = Pattern.compile("\\[cooldown:(.*?)]");
        Matcher matcher = pattern.matcher(action);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    /**
     * Sorts and filters case history data based on a specific case type.
     *
     * @param historyData the list of {@link CaseDataHistory} objects to sort and filter.
     * @param caseType    the type of case to filter by.
     * @return a sorted list of {@link CaseDataHistory}, filtered by the specified case type,
     * sorted in descending order of time.
     */
    static List<CaseDataHistory> sortHistoryDataByCase(List<CaseDataHistory> historyData, String caseType) {
        return historyData.stream()
                .filter(Objects::nonNull)
                .filter(data -> data.getCaseType().equals(caseType))
                .sorted(Comparator.comparingLong(CaseDataHistory::getTime).reversed())
                .collect(Collectors.toList());
    }
}
