package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AddonsCommand extends SubCommand<CommandSender> {

    private final DCAPIBukkit api;

    public AddonsCommand(DCAPIBukkit api) {
        super("addons", api.getAddon());
        setPermission(SubCommandType.ADMIN.permission);
        this.api = api;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<InternalJavaAddon> addons = new ArrayList<>(api.getAddonManager().getAddons().values());
        addons.sort(Comparator.comparing(InternalJavaAddon::getName));
        DCToolsBukkit.msgRaw(sender, "&7Currently loaded addons in DonateCase (&a" + addons.size() + "&7): " + compileAddons(addons));
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
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
