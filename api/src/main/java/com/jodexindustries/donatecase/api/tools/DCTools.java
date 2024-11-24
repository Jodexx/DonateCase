package com.jodexindustries.donatecase.api.tools;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataHistory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        version = version.replaceAll("\\.", "");
        if (version.length() == 4) {
            return Integer.parseInt(version);
        }
        version = version.concat("0000");
        return Integer.parseInt(version.substring(0, 4));
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

    /**
     * Get sorted history data by case
     * @param historyData HistoryData from all cases (or not all)
     * @param caseType type of case for filtering
     * @return list of case HistoryData
     */
    static List<CaseDataHistory> sortHistoryDataByCase(List<CaseDataHistory> historyData, String caseType) {
        return historyData.stream().filter(Objects::nonNull)
                .filter(data -> data.getCaseType().equals(caseType))
                .sorted(Comparator.comparingLong(CaseDataHistory::getTime).reversed())
                .collect(Collectors.toList());
    }
}