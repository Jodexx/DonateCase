package com.jodexindustries.donatecase.api.actions;

import com.jodexindustries.donatecase.api.data.CaseAction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import static com.jodexindustries.donatecase.DonateCase.instance;


public class TitleAction implements CaseAction {
    /**
     * Send title for player with specific cooldown
     * @param player The player to whom the title will be sent
     * @param context Title message. Format: "title;subtitle"
     * @param cooldown Cooldown in seconds
     */
    @Override
    public void execute(OfflinePlayer player, String context, int cooldown) {
        String[] args = context.split(";");
        String title = args.length > 0 ? args[0] : "";
        String subTitle = args.length > 1 ? args[1] : "";
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            if (player.getPlayer() != null) {
                player.getPlayer().sendTitle(
                        title,
                        subTitle, 10, 70, 20);
            }
        }, 20L * cooldown);
    }
}
