package com.jodexindustries.donatecase.common.actions;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandActionExecutorImpl implements ActionExecutor {

    @Override
    public void execute(@Nullable DCPlayer player, @NotNull String context) {
        DCAPI.getInstance().getPlatform().dispatchConsoleCommand(context);
    }
}
