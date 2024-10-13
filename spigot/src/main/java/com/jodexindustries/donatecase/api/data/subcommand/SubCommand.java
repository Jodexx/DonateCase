package com.jodexindustries.donatecase.api.data.subcommand;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for subcommand storage
 * @since 2.2.4.5
 */
public class SubCommand implements SubCommandExecutor, SubCommandTabCompleter {
    private final Addon addon;
    private final String name;

    private SubCommandExecutor executor;
    private SubCommandTabCompleter tabCompleter;

    private String description;
    private String permission;
    private String[] args;

    public SubCommand(String name, Addon addon) {
        this.addon = addon;
        this.name = name;
    }


    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (executor != null) executor.execute(sender, label, args);
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (tabCompleter == null) return new ArrayList<>();
        return tabCompleter.getTabCompletions(sender, label, args);
    }

    public SubCommandExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(SubCommandExecutor executor) {
        this.executor = executor;
    }

    public SubCommandTabCompleter getTabCompleter() {
        return tabCompleter;
    }

    public void setTabCompleter(SubCommandTabCompleter tabCompleter) {
        this.tabCompleter = tabCompleter;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the subcommand description
     * @return subcommand description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the subcommand description that will be displayed in help list
     * @param description subcommand description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    /**
     * Gets permission for command executing
     * @return Value of the permission
     * @since 2.2.5.6
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Sets permission for command executing
     * @param permission Value of the permission
     * @since 2.2.5.6
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * @since 2.2.5.6
     */
    public Builder toBuilder() {
        Builder builder = new Builder(name, addon);
        builder.permission = permission;
        builder.description = description;
        builder.args = args;
        builder.tabCompleter = tabCompleter;
        builder.executor = executor;
        return builder;
    }

    public Addon getAddon() {
        return addon;
    }

    @Override
    public String toString() {
        return "SubCommand{" +
                "addon=" + addon +
                ", name='" + name + '\'' +
                ", executor=" + executor +
                ", tabCompleter=" + tabCompleter +
                ", description='" + description + '\'' +
                ", permission='" + permission + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }

    public static class Builder {
        private final Addon addon;
        private final String name;

        private SubCommandExecutor executor;
        private SubCommandTabCompleter tabCompleter;
        private String description;
        private String[] args;
        private String permission;

        public Builder(String name, Addon addon) {
            this.addon = addon;
            this.name = name;
        }

        public Builder executor(SubCommandExecutor executor) {
            this.executor = executor;
            return this;
        }

        public Builder tabCompleter(SubCommandTabCompleter tabCompleter) {
            this.tabCompleter = tabCompleter;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder args(String[] args) {
            this.args = args;
            return this;
        }

        public Builder permission(String permission) {
            this.permission = permission;
            return this;
        }

        public SubCommand build() {
            SubCommand subCommand = new SubCommand(name, addon);
            subCommand.setExecutor(executor);
            subCommand.setTabCompleter(tabCompleter);
            subCommand.setDescription(description);
            subCommand.setArgs(args);
            subCommand.setPermission(permission);
            return subCommand;
        }
    }
}
