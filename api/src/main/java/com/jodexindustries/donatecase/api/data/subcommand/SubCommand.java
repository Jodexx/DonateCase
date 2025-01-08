package com.jodexindustries.donatecase.api.data.subcommand;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a subcommand with execution and tab-completion capabilities.
 *
 * @param <S> The type of the command sender.
 */
public class SubCommand<S> implements SubCommandExecutor<S>, SubCommandTabCompleter<S> {
    private final Addon addon;
    private final String name;

    private SubCommandExecutor<S> executor;
    private SubCommandTabCompleter<S> tabCompleter;

    private String description;
    private String permission;
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

    /**
     * Gets the name of the subcommand.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the subcommand.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the arguments for the subcommand.
     *
     * @return The arguments.
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Gets the permission required to execute the subcommand.
     *
     * @return The permission.
     */
    public String getPermission() {
        return permission;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    /**
     * Gets the addon associated with this subcommand.
     *
     * @return The associated addon.
     */
    public Addon getAddon() {
        return addon;
    }

    @Override
    public void execute(@NotNull S sender, @NotNull String label, @NotNull String[] args) {
        executor.execute(sender, label, args);
    }

    @Override
    public List<String> getTabCompletions(@NotNull S sender, @NotNull String label, @NotNull String[] args) {
        return tabCompleter.getTabCompletions(sender, label, args);
    }

    /**
     * Builder class for creating or modifying subcommands.
     *
     * @param <S> The type of the command sender.
     */
    public static class Builder<S> {
        private final Addon addon;
        private final String name;

        private SubCommandExecutor<S> executor;
        private SubCommandTabCompleter<S> tabCompleter;
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
        public Builder<S> executor(SubCommandExecutor<S> executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Sets the tab completer for the subcommand.
         *
         * @param tabCompleter The tab completer.
         * @return The builder instance.
         */
        public Builder<S> tabCompleter(SubCommandTabCompleter<S> tabCompleter) {
            this.tabCompleter = tabCompleter;
            return this;
        }

        /**
         * Sets the description for the subcommand.
         *
         * @param description The description.
         * @return The builder instance.
         */
        public Builder<S> description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the arguments for the subcommand.
         *
         * @param args The arguments.
         * @return The builder instance.
         */
        public Builder<S> args(String[] args) {
            this.args = args;
            return this;
        }

        /**
         * Sets the permission required to execute the subcommand.
         *
         * @param permission The permission.
         * @return The builder instance.
         */
        public Builder<S> permission(String permission) {
            this.permission = permission;
            return this;
        }

        /**
         * Builds the subcommand with the specified properties.
         *
         * @return The constructed subcommand.
         */
        public SubCommand<S> build() {
            SubCommand<S> subCommand = new SubCommand<>(name, addon);
            subCommand.executor = executor;
            subCommand.tabCompleter = tabCompleter;
            subCommand.description = description;
            subCommand.args = args;
            subCommand.permission = permission;
            return subCommand;
        }
    }
}
