package com.jodexindustries.donatecase.api.data.subcommand;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a subcommand with execution and tab-completion capabilities.
 *
 */
public class SubCommand implements SubCommandExecutor, SubCommandTabCompleter {
    @Getter
    private final Addon addon;
    @Getter
    private final String name;

    private SubCommandExecutor executor;
    private SubCommandTabCompleter tabCompleter;

    @Setter
    @Getter
    private String description;
    @Setter
    @Getter
    private String permission;
    @Setter
    @Getter
    private String[] args;

    /**
     * Creates a new subcommand instance.
     *
     * @param name  The name of the subcommand.
     * @param addon The addon associated with the subcommand.
     */
    public SubCommand(@NotNull String name, @NotNull Addon addon) {
        this.addon = addon;
        this.name = name;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, @NotNull String[] args) {
        return executor.execute(sender, label, args);
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, @NotNull String[] args) {
        return tabCompleter.getTabCompletions(sender, label, args);
    }

    /**
     * Builder class for creating or modifying subcommands.
     *
     */
    public static class Builder {
        private final Addon addon;
        private final String name;

        private SubCommandExecutor executor;
        private SubCommandTabCompleter tabCompleter;
        private String description;
        private String[] args;
        private String permission;

        /**
         * Creates a new builder for a subcommand.
         *
         * @param name  The name of the subcommand.
         * @param addon The addon associated with the subcommand.
         */
        public Builder(@NotNull String name, @NotNull Addon addon) {
            this.addon = addon;
            this.name = name;
        }

        /**
         * Sets the executor for the subcommand.
         *
         * @param executor The executor.
         * @return The builder instance.
         */
        public Builder executor(SubCommandExecutor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Sets the tab completer for the subcommand.
         *
         * @param tabCompleter The tab completer.
         * @return The builder instance.
         */
        public Builder tabCompleter(SubCommandTabCompleter tabCompleter) {
            this.tabCompleter = tabCompleter;
            return this;
        }

        /**
         * Sets the description for the subcommand.
         *
         * @param description The description.
         * @return The builder instance.
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the arguments for the subcommand.
         *
         * @param args The arguments.
         * @return The builder instance.
         */
        public Builder args(String[] args) {
            this.args = args;
            return this;
        }

        /**
         * Sets the permission required to execute the subcommand.
         *
         * @param permission The permission.
         * @return The builder instance.
         */
        public Builder permission(String permission) {
            this.permission = permission;
            return this;
        }

        /**
         * Builds the subcommand with the specified properties.
         *
         * @return The constructed subcommand.
         */
        public SubCommand build() {
            SubCommand subCommand = new SubCommand(name, addon);
            subCommand.executor = executor;
            subCommand.tabCompleter = tabCompleter;
            subCommand.description = description;
            subCommand.args = args;
            subCommand.permission = permission;
            return subCommand;
        }
    }
}
