package com.jodexindustries.donatecase.common.actions;

import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TitleActionExecutorImpl implements ActionExecutor {

    @Override
    public void execute(@Nullable DCPlayer player, @NotNull String context) {
        if (player == null) return;

        String[] args = context.split(";");
        String title = args.length > 0 ? args[0] : "";
        String subTitle = args.length > 1 ? args[1] : "";
        player.showTitle(title, subTitle, 10, 70, 20);
    }
}
