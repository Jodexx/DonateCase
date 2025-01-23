package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.command.DefaultCommand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AddonsCommand extends DefaultCommand {

    private final DCAPI api;

    public AddonsCommand(DCAPI api) {
        super(api, "addons", SubCommandType.ADMIN);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        List<InternalJavaAddon> addons = new ArrayList<>(api.getAddonManager().getMap().values());
        addons.sort(Comparator.comparing(InternalJavaAddon::getName));
        sender.sendMessage(DCTools.prefix("&7Currently loaded addons in DonateCase (&a" + addons.size() + "&7): " + compileAddons(addons)));
        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

    private @NotNull StringBuilder compileAddons(List<InternalJavaAddon> addons) {
        StringBuilder builder = new StringBuilder();
        if (addons != null && !addons.isEmpty()) {
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
