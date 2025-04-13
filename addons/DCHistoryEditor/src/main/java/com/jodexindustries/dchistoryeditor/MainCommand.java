package com.jodexindustries.dchistoryeditor;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.api.database.CaseDatabase;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.jodexindustries.donatecase.api.tools.DCTools.rc;

public class MainCommand implements SubCommandExecutor, SubCommandTabCompleter {

    private final CaseDatabase database;
    private final MainAddon addon;

    private static final List<String> params = new ArrayList<>();

    static {
        params.add("item");
        params.add("playername");
        params.add("time");
        params.add("group");
        params.add("action");
    }

    public MainCommand(MainAddon addon) {
        this.addon = addon;
        this.database = addon.api.getDatabase();
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, @NotNull String[] args) {
        addon.api.getPlatform().getScheduler().async(addon, () -> {

            if (args.length < 3) {
                sender.sendMessage(rc("&c/dc historyeditor (remove/set) (casetype) (index/all) [param] [value]"));
                return;
            }

            String action = args[0].toLowerCase();
            String caseType = args[1];

            CaseData caseData = addon.api.getCaseManager().get(caseType);

            if(caseData == null) {
                sender.sendMessage(rc("&cCase with type: " + caseType + " not found!"));
                return;
            }

            switch (action) {
                case "remove": {
                    handleRemove(sender, caseData, args[2]);
                    return;
                }

                case "set": {
                    handleSet(sender, caseData, args);
                    return;
                }

                default: {
                    sender.sendMessage(rc("&c/dc historyeditor (remove/set) (casetype) (index) [param] [value]"));
                }
            }
        }, 0L);

        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if(args.length == 1) {
            list.add("remove");
            list.add("set");
        }

        if(args.length == 2) {
            list.addAll(addon.api.getCaseManager().getMap().keySet());
        }

        if(args.length == 3) {
            list.add("all");
            IntStream.rangeClosed(0, 10).mapToObj(String::valueOf).forEach(list::add);
        }

        if(args.length == 4 && args[0].equals("set")) {
            list.addAll(params);
        }

        return list;
    }

    private void handleRemove(DCCommandSender sender, CaseData caseData, String arg) {
        if(arg.equalsIgnoreCase("all") ) {
            database.removeHistoryData(caseData.caseType()).thenAccept(status -> {
                removeInform(sender, status);
            });
        } else {
            int index = getIndex(sender, arg, caseData.historyDataSize());
            if(index == -1) return;

            database.removeHistoryData(caseData.caseType(), index).thenAccept(status -> {
                removeInform(sender, status);
            });
        }
    }

    private void handleSet(DCCommandSender sender, CaseData caseData, String[] args) {
        int index = getIndex(sender, args[2], caseData.historyDataSize());
        if(index == -1) return;

        if (args.length < 5) {
            // usage
            sender.sendMessage(rc("&c/dc historyeditor set (casetype) (index) (value) (param)"));
            return;
        }

        String param = args[3].toLowerCase();
        String value = args[4];

        if (!params.contains(param)) {
            // param is not found
            sender.sendMessage(rc("&cParam " + param + " is not found!"));
            return;
        }

        CaseData.History tempHistory = getHistoryData(caseData.caseType(), index);

        CaseData.History history = tempHistory == null ? new CaseData.History(null, caseData.caseType(), null, index, null, null) : tempHistory;

        switch (param) {
            case "item": {
                history.item(value);
                break;
            }

            case "playername": {
                history.playerName(value);
                break;
            }

            case "time": {
                history.time(Long.parseLong(value));
                break;
            }

            case "group": {
                history.group(value);
                break;
            }

            case "action": {
                history.action(value);
                break;
            }
        }

        database.setHistoryData(caseData.caseType(), index, history).thenAccept(status -> {
            if (status == DatabaseStatus.COMPLETE) {
                sender.sendMessage(rc("&aHistory data updated!"));
            } else {
                sender.sendMessage(rc("&cError with history data updating!"));
            }
        });
    }

    private CaseData.History getHistoryData(String caseType, int index) {
        List<CaseData.History> histories = database.getHistoryData(caseType).join();

        return histories.get(index);
    }

    private void removeInform(DCCommandSender sender, DatabaseStatus status) {
        sender.sendMessage(status == DatabaseStatus.COMPLETE ? rc("&aHistory data removed!") :
                rc("&cError with history data removing!"));
    }

    private int getIndex(DCCommandSender sender, String string, int maxSize) {
        int index = parseInt(string);
        if (index <= -1 || index >= maxSize) {
            sender.sendMessage(rc("&cNumber format exception!"));
            return -1;
        }
        return index;
    }

    private int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ignored) {}
        return -1;
    }
}
