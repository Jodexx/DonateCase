package com.jodexindustries.donatecase.api.data.action;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class for case actions storage
 */
@Accessors(fluent = true)
@Getter
@Builder
public class CaseAction implements ActionExecutor {

    @NotNull private final Addon addon;
    @NotNull private final String name;
    @NotNull private final ActionExecutor executor;
    @Nullable private final String description;

    @Override
    public void execute(@Nullable DCPlayer player, @NotNull String context) throws ActionException {
        executor.execute(player, context);
    }
}
