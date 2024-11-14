package com.jodexindustries.donatecase.impl.actions;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TitleActionExecutorImpl implements ActionExecutor<Player> {
    /**
     * Send title for player with specific cooldown<br>
     * {@code - "[title] (title);(subtitle)"}
     *
     * @param player The player to whom the title will be sent
     * @param context Title message. Format: "title;subtitle"
     * @param cooldown Cooldown in seconds
     */
    @Override
    public void execute(@Nullable Player player, @NotNull String context, int cooldown) {
        String[] args = context.split(";");
        String title = args.length > 0 ? args[0] : "";
        String subTitle = args.length > 1 ? args[1] : "";
        Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> {
            if (player != null) {
                player.sendTitle(
                        title,
                        subTitle, 10, 70, 20);
            }
        }, 20L * cooldown);
    }
}
