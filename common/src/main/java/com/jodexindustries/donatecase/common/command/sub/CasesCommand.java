package com.jodexindustries.donatecase.common.command.sub;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.command.DefaultCommand;
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
            sender.sendMessage(DCTools.prefix(DCTools.rt(api.getConfigManager().getMessages().getString("list-of-cases"),
                    "%casename:" + data.caseType(), "%num:" + num,
                    "%casedisplayname:" + data.caseDisplayName(),
                    "%casetitle:" + data.caseGui().title())));
        }
        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

}
