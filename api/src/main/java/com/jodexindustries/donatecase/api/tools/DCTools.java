package com.jodexindustries.donatecase.api.tools;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.chat.ColorUtils;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseInventory;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.CaseMaterialException;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.manager.MaterialManager;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.platform.DCOfflinePlayer;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.scheduler.DCFuture;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for the DonateCase system, providing tools for parsing,
 * validation, and manipulation.
 */
public abstract class DCTools {

    @Contract("null -> new")
    public static Integer @NotNull [] parseRGB(String string) {
        if (string == null || string.isEmpty()) return new Integer[0];

        List<Integer> list = new ArrayList<>();
        for (String s : string.replace(" ", "").split(",")) {
            try {
                Integer parseInt = Integer.parseInt(s);
                list.add(parseInt);
            } catch (NumberFormatException ignored) {
                list.add(0);
            }
        }
        return list.toArray(new Integer[0]);
    }

    public abstract CaseInventory createInventory(CaseGuiWrapper wrapper, int size, @Nullable String title);

    public abstract ArmorStandCreator createArmorStand(UUID animationUuid, CaseLocation location);

    @Nullable
    public abstract Object loadCaseItem(String id);

    public abstract Object createSkullFromTexture(String texture);

    public abstract DCFuture<?> createSkullFromPlayer(String playerName);

    public abstract DCFuture<?> createSkullFromUuid(UUID uuid);

    public static DateFormat getDateFormat() {
        return new SimpleDateFormat(
                DCAPI.getInstance().getConfigManager().getConfig().dateFormat());
    }

    @NotNull
    public static DCFuture<@NotNull String> formatPlayerName(String name) {
        String trimmed = name.trim();

        return DCFuture.supplyAsync(() -> {
            if (!DCAPI.getInstance().getConfigManager().getConfig().formatPlayerName()) {
                return trimmed;
            }

            for (DCOfflinePlayer player : DCAPI.getInstance().getPlatform().getOfflinePlayers()) {
                String offlinePlayer = player.getName();

                if (offlinePlayer != null && trimmed.equalsIgnoreCase(offlinePlayer.trim())) {
                    return player.getName();
                }
            }

            return trimmed;
        });
    }

    public static @NotNull List<String> resolveSDGCompletions(String[] args) {
        List<String> value = DCAPI.getInstance().getCaseManager().definitions().stream()
                .map(def -> def.settings().type())
                .collect(Collectors.toList());
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.addAll(
                    Arrays.stream(DCAPI.getInstance().getPlatform().getOnlinePlayers())
                            .map(DCPlayer::getName)
                            .filter(px -> px.startsWith(args[0]))
                            .collect(Collectors.toList()));
            return list;
        } else if (args.length >= 3) {
            if (args.length == 4) {
                list.add("-s");
                return list;
            }
            return new ArrayList<>();
        }
        if (args[args.length - 1].isEmpty()) {
            list = value;
        } else {
            list.addAll(
                    value.stream()
                            .filter(tmp -> tmp.startsWith(args[args.length - 1]))
                            .collect(Collectors.toList()));
        }
        return list;
    }

    @Nullable
    public static Object getItemFromManager(@NotNull String id) {
        MaterialManager manager = DCAPI.getInstance().getMaterialManager();

        Optional<String> temp = manager.getByStart(id);

        if (temp.isPresent()) {
            CaseMaterial caseMaterial = manager.get(temp.get());
            if (caseMaterial != null) {
                String context = id.replace(temp.get(), "").replaceFirst(":", "").trim();
                try {
                    return caseMaterial.handle(context);
                } catch (CaseMaterialException e) {
                    DCAPI.getInstance().getPlatform().getLogger().log(Level.WARNING,
                            "Error with handling material " + context, e);
                }
            }
        }
        return null;
    }

    public static String prefix(String text) {
        return rc(DCAPI.getInstance().getConfigManager().getMessages().getString("prefix") + text);
    }

    public static String rc(String text) {
        if (text == null)
            return null;
        return ColorUtils.color(text);
    }

    public static String rt(String text, Placeholder... placeholders) {
        if (text == null || placeholders.length == 0)
            return text;
        return rt(text, Arrays.asList(placeholders));
    }

    public static String rt(String text, Collection<? extends Placeholder> placeholders) {
        if (text == null || placeholders == null || placeholders.isEmpty())
            return text;

        StringBuilder result = new StringBuilder(text);
        for (Placeholder placeholder : placeholders) {
            int index;
            while ((index = result.indexOf(placeholder.name())) != -1) {
                result.replace(index, index + placeholder.name().length(), placeholder.value());
            }
        }
        return rc(result.toString());
    }

    public static List<String> rt(List<String> text, Collection<? extends Placeholder> placeholders) {
        if (text == null)
            return null;
        return text.stream().map(t -> rt(t, placeholders)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<String> rt(List<String> text, Placeholder... placeholders) {
        if (text == null)
            return null;
        return text.stream().map(t -> rt(t, placeholders)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<String> rc(List<String> list) {
        if (list == null)
            return null;
        return list.stream().map(DCTools::rc).collect(Collectors.toCollection(ArrayList::new));
    }

    public static boolean isHasCommandForSender(DCCommandSender sender,
                                                Map<String, List<Map<String, SubCommand>>> addonsMap) {
        return addonsMap.keySet().stream().map(addonsMap::get)
                .anyMatch(commands -> isHasCommandForSender(sender, commands));
    }

    /**
     * Check sender for permission to executing commands
     * Checks only if sender has permission for one or more commands, not all
     *
     * @param sender   Player or Console
     * @param commands List of commands, that loaded in DonateCase
     * @return true, if sender has permission
     */
    public static boolean isHasCommandForSender(DCCommandSender sender, List<Map<String, SubCommand>> commands) {
        return commands.stream().flatMap(command -> command.values().stream()).map(SubCommand::permission)
                .anyMatch(permission -> permission == null || sender.hasPermission(permission));
    }

    /**
     * Extracts the local placeholder from a string, delimited by `%`.
     *
     * @param string the input string containing placeholders in the format
     *               `%placeholder%`.
     * @return the extracted placeholder without `%` symbols, or "null" if no
     * placeholder is found.
     */
    public static String getLocalPlaceholder(String string) {
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
     * <li>Input: <code>"2.2.2"</code> → Output: <code>2220</code></li>
     * <li>Input: <code>"2.2.2.2"</code> → Output: <code>2222</code></li>
     * </ul>
     */
    public static int getPluginVersion(String version) {
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
     * @param action the action string containing a cooldown in the format
     *               <code>[cooldown:int]</code>.
     * @return the cooldown value, or 0 if no cooldown is specified.
     */
    public static int extractCooldown(String action) {
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
     * @param historyData the list of {@link CaseData.History} objects to sort and
     *                    filter.
     * @param caseType    the type of case to filter by.
     * @return a sorted list of {@link CaseData.History}, filtered by the specified
     * case type,
     * sorted in descending order of time.
     */
    public static List<CaseData.History> sortHistoryDataByCase(List<CaseData.History> historyData, String caseType) {
        List<CaseData.History> list = new ArrayList<>();
        for (CaseData.History data : historyData) {
            if (data != null) {
                if (data.caseType().equals(caseType)) {
                    list.add(data);
                }
            }
        }

        list.sort(Comparator.comparingLong(object -> ((CaseData.History) object).time()).reversed());
        return list;
    }

    public static List<CaseData.History> sortHistoryDataByDate(List<CaseData.History> list) {
        return list.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(object -> ((CaseData.History) object).time()).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Sort case items by index
     *
     * @param items Map with Case items
     * @return New map with sorted items
     */
    public static Map<String, CaseItem> sortItemsByIndex(Map<String, CaseItem> items) {
        return items.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(CaseItem::index)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    public static boolean isValidGuiSize(int size) {
        return size >= 9 && size <= 54 && size % 9 == 0;
    }

}