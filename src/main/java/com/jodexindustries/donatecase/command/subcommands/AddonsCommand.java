package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.AddonManager;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class for /dc addons subcommand implementation
 */
public class AddonsCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        List<InternalJavaAddon> addons = new ArrayList<>(AddonManager.getAddons());
        addons.sort(Comparator.comparing(InternalJavaAddon::getName));
        Tools.msgRaw(sender, "&7Currently loaded addons in DonateCase (&a" + addons.size() + "&7): " + compileAddons(addons));
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.ADMIN;
    }
    private @NotNull StringBuilder compileAddons(List<InternalJavaAddon> addons) {
        StringBuilder builder = new StringBuilder();
        if(addons != null && !addons.isEmpty()) {
            for (int i = 0; i < addons.size(); i++) {
                InternalJavaAddon addon = addons.get(i);
                String name = addon.isEnabled() ? "&a" + addon.getName() : "&c" + addon.getName();
                builder.append(name);
                if (i < addons.size() - 1) {
                    builder.append("&7, ");
                }
            }
        }
        return builder;
    }
}
