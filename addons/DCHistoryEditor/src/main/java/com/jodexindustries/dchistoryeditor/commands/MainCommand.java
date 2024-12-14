package com.jodexindustries.dchistoryeditor.commands;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataHistory;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.api.database.CaseDatabase;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MainCommand implements SubCommandExecutor<CommandSender>, SubCommandTabCompleter<CommandSender> {

    private final CaseDatabase database;
    private final DCAPIBukkit api;

    private static final List<String> params = new ArrayList<>();

    static {
        params.add("item");
        params.add("playername");
        params.add("time");
        params.add("group");
        params.add("action");
    }

    public MainCommand(DCAPIBukkit api) {
        this.api = api;
        this.database = api.getDatabase();
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(api.getDonateCase(), () -> {
            if (args.length < 3) {
                sender.sendMessage(DCToolsBukkit.rc("&c/dc historyeditor (remove/set) (casetype) (index) [param] [value]"));
                return;
            }

            String action = args[0].toLowerCase();
            String caseType = args[1];
            int index = parseInt(args[2]);

            if (index <= -1 || index > 10) {
                sender.sendMessage(DCToolsBukkit.rc("&cNumber format exception!"));
                return;
            }

            switch (action) {
                case "remove": {
                    database.setHistoryData(caseType, index, null).thenAccept(status -> {
                        if (status == DatabaseStatus.COMPLETE) {
                            sender.sendMessage(DCToolsBukkit.rc("&aHistory data removed!"));
                        } else {
                            sender.sendMessage(DCToolsBukkit.rc("&cError with history data removing!"));
                        }
                    });
                    return;
                }

                case "set": {
                    if (args.length < 5) {
                        // usage
                        sender.sendMessage(DCToolsBukkit.rc("&c/dc historyeditor set (casetype) (index) (value) (param)"));
                        return;
                    }

                    String param = args[3].toLowerCase();
                    String value = args[4];

                    if (!params.contains(param)) {
                        // param is not found
                        sender.sendMessage(DCToolsBukkit.rc("&cParam " + param + " is not found!"));
                        return;
                    }

                    CaseDataHistory history = getHistoryData(caseType, index);
                    if(history == null) history = new CaseDataHistory(null, caseType, null, 0, null, null);
                    history.setId(index);

                    switch (param) {
                        case "item": {
                            history.setItem(value);
                            break;
                        }

                        case "playername": {
                            history.setPlayerName(value);
                            break;
                        }

                        case "time": {
                            history.setTime(Long.parseLong(value));
                            break;
                        }

                        case "group": {
                            history.setGroup(value);
                            break;
                        }

                        case "action": {
                            history.setAction(value);
                            break;
                        }
                    }

                    database.setHistoryData(caseType, index, history).thenAccept(status -> {
                        if (status == DatabaseStatus.COMPLETE) {
                            sender.sendMessage(DCToolsBukkit.rc("&aHistory data updated!"));
                        } else {
                            sender.sendMessage(DCToolsBukkit.rc("&cError with history data updating!"));
                        }
                    });

                    return;
                }

                default: {
                    sender.sendMessage(DCToolsBukkit.rc("&c/dc historyeditor (remove/set) (casetype) (index) [param] [value]"));
                }
            }
        });
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if(args.length == 1) {
            list.add("remove");
            list.add("set");
        }

        if(args.length == 2) {
            list.addAll(api.getCaseManager().getMap().keySet());
        }

        if(args.length == 3) {
            IntStream.rangeClosed(0, 10).mapToObj(String::valueOf).forEach(list::add);
        }

        if(args.length == 4 && args[0].equals("set")) {
            list.addAll(params);
        }

        return list;
    }

    private CaseDataHistory getHistoryData(String caseType, int index) {
        List<CaseDataHistory> histories = database.getHistoryDataByCaseType(caseType).join();

        return histories.stream().filter(history -> history.getId() == index).findFirst().orElse(null);
    }

    private int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ignored) {}
        return -1;
    }
}
