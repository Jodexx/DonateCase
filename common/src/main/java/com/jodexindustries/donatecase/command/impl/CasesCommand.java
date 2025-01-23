package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.command.DefaultCommand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CasesCommand extends DefaultCommand {

    private final DCAPI api;

    public CasesCommand(DCAPI api) {
        super(api, "cases", SubCommandType.MODER);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        int num = 0;
        for (CaseData data : api.getCaseManager().getMap().values()) {
            num++;
            sender.sendMessage(DCTools.prefix(DCTools.rt(api.getConfig().getMessages().getString("list-of-cases"),
                    "%casename:" + data.getCaseType(), "%num:" + num,
                    "%casedisplayname:" + data.getCaseDisplayName(),
                    "%casetitle:" + data.getCaseGui().getTitle())));
        }
        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

}
