package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CasesCommand extends SubCommand<CommandSender> {

    private final DCAPIBukkit api;

    public CasesCommand(DCAPIBukkit api) {
        super("cases", api.getAddon());
        setPermission(SubCommandType.MODER.permission);
        this.api = api;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        int num = 0;
        for (CaseDataBukkit data : api.getCaseManager().getMap().values()) {
            num++;
            DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("list-of-cases"),
                    "%casename:" + data.getCaseType(), "%num:" + num,
                    "%casedisplayname:" + data.getCaseDisplayName(),
                    "%casetitle:" + data.getCaseTitle()));
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

}
