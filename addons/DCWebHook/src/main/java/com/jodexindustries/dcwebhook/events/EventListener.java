package com.jodexindustries.dcwebhook.events;

import com.jodexindustries.dcwebhook.tools.DiscordWebhook;
import com.jodexindustries.dcwebhook.tools.Tools;
import com.jodexindustries.donatecase.api.events.AnimationEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;
import java.io.IOException;

public class EventListener implements Listener {
    private final Tools t;
    public EventListener(Tools t) {
        this.t = t;
    }
    @EventHandler
    public void onAnimationEnd(AnimationEndEvent e) {
        String player = e.getPlayer().getName();
        String animation = e.getAnimation();
        String caseType = e.getCaseData().getCaseType();
        String winGroup = e.getWinItem().getGroup();
        String caseTitle = ChatColor.stripColor(e.getCaseData().getCaseTitle());
        Bukkit.getScheduler().runTaskAsynchronously(t.getMain().getPlugin(),  () -> {
            String webhook = t.getConfig().getConfig().getString("Webhook");
            if(webhook != null && !webhook.isEmpty()) {
                DiscordWebhook.EmbedObject object = new DiscordWebhook.EmbedObject();
                DiscordWebhook discordWebhook = new DiscordWebhook(webhook);
                String title = t.getConfig().getConfig().getString("Embed.Title");
                String authorName = t.getConfig().getConfig().getString("Embed.Author.Name", "")
                        .replaceAll("%player%", player)
                        .replaceAll("%animation%", animation)
                        .replaceAll("%wingroup%", winGroup)
                        .replaceAll("%casetitle%", caseTitle)
                        .replaceAll("%casetype%", caseType);
                String description = t.getConfig().getConfig().getString("Embed.Description", "")
                        .replaceAll("%player%", player)
                        .replaceAll("%animation%", animation)
                        .replaceAll("%wingroup%", winGroup)
                        .replaceAll("%casetitle%", caseTitle)
                        .replaceAll("%casetype%", caseType);
                String footerText = t.getConfig().getConfig().getString("Embed.Footer.Text", "")
                        .replaceAll("%player%", player)
                        .replaceAll("%animation%", animation)
                        .replaceAll("%wingroup%", winGroup)
                        .replaceAll("%casetitle%", caseTitle)
                        .replaceAll("%casetype%", caseType);
                String footerIcon = t.getConfig().getConfig().getString("Embed.Footer.Icon", "");
                ConfigurationSection fieldsSection = t.getConfig().getConfig().getConfigurationSection("Embed.Fields");
                object.setTitle(title);
                if(fieldsSection != null) {
                    for (String field : fieldsSection.getKeys(false)) {
                        String fieldTitle = t.getConfig().getConfig().getString("Embed.Fields." + field + ".Title", "")
                                .replaceAll("%player%", player)
                                .replaceAll("%animation%", animation)
                                .replaceAll("%wingroup%", winGroup)
                                .replaceAll("%casetitle%", caseTitle)
                                .replaceAll("%casetype%", caseType);
                        String fieldValue = t.getConfig().getConfig().getString("Embed.Fields." + field + ".Value", "")
                                .replaceAll("%player%", player)
                                .replaceAll("%animation%", animation)
                                .replaceAll("%wingroup%", winGroup)
                                .replaceAll("%casetitle%", caseTitle)
                                .replaceAll("%casetype%", caseType);
                        boolean inline = t.getConfig().getConfig().getBoolean("Embed.Fields." + field + ".Inline");
                        object.addField(fieldTitle, fieldValue, inline);
                    }
                }
                if (!authorName.isEmpty()) {
                    object.setAuthor(authorName,
                            t.getConfig().getConfig().getString("Embed.Author.Url"),
                            t.getConfig().getConfig().getString("Embed.Author.Icon"));
                }
                if (!description.isEmpty()) {
                    object.setDescription(description);
                }
                if (!footerText.isEmpty() || footerIcon.isEmpty()) {
                    object.setFooter(footerText, footerIcon);
                }
                int r = t.getConfig().getConfig().getInt("Embed.Color.r");
                int g = t.getConfig().getConfig().getInt("Embed.Color.g");
                int b = t.getConfig().getConfig().getInt("Embed.Color.b");
                object.setColor(new Color(r, g, b));
                discordWebhook.addEmbed(object);
                try {
                    discordWebhook.execute();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
