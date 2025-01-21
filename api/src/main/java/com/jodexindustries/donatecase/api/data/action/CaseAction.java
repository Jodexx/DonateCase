package com.jodexindustries.donatecase.api.data.action;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class for custom actions storage
 */
public class CaseAction implements ActionExecutor {
    private final ActionExecutor executor;
    /**
     * -- GETTER --
     *  Gets addon which registered this action
     *
     * @return addon action
     */
    @Getter
    private final Addon addon;
    /**
     * -- GETTER --
     *  Gets action name
     *
     * @return action name
     */
    @Getter
    private final String name;
    /**
     * -- GETTER --
     *  Gets action description
     *
     * @return action description
     */
    @Getter
    private final String description;

    /**
     * Default constructor
     *
     * @param executor    Action executor
     * @param addon       Action addon
     * @param name        Action name
     * @param description Action description
     */
    public CaseAction(ActionExecutor executor, Addon addon, String name, String description) {
        this.executor = executor;
        this.addon = addon;
        this.name = name;
        this.description = description;
    }

    @Override
    public void execute(@Nullable DCPlayer player, @NotNull String context, int cooldown) {
        executor.execute(player, context, cooldown);
    }
}
