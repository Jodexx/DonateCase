package com.jodexindustries.donatecase.api.data.subcommand;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for subcommand storage
 * @since 2.2.4.5
 */
public class SubCommand<S> implements SubCommandExecutor<S>, SubCommandTabCompleter<S> {
    private final Addon addon;
    private final String name;

    private SubCommandExecutor<S> executor;
    private SubCommandTabCompleter<S> tabCompleter;

    private String description;
    private String permission;
    private String[] args;

    public SubCommand(@NotNull String name, @NotNull Addon addon) {
        this.addon = addon;
        this.name = name;
    }


    @Override
    public void execute(@NotNull S sender, @NotNull String label, String[] args) {
        if (executor != null) executor.execute(sender, label, args);
    }

    @Override
    public List<String> getTabCompletions(@NotNull S sender, @NotNull String label, String[] args) {
        if (tabCompleter == null) return new ArrayList<>();
        return tabCompleter.getTabCompletions(sender, label, args);
    }

    public SubCommandExecutor<S> getExecutor() {
        return executor;
    }

    public void setExecutor(SubCommandExecutor<S> executor) {
        this.executor = executor;
    }

    public SubCommandTabCompleter<S> getTabCompleter() {
        return tabCompleter;
    }

    public void setTabCompleter(SubCommandTabCompleter<S> tabCompleter) {
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
    public Builder<S> toBuilder() {
        Builder<S> builder = new Builder<>(name, addon);
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

    public static class Builder<S> {
        private final Addon addon;
        private final String name;

        private SubCommandExecutor<S> executor;
        private SubCommandTabCompleter<S> tabCompleter;
        private String description;
        private String[] args;
        private String permission;

        public Builder(@NotNull String name, @NotNull Addon addon) {
            this.addon = addon;
            this.name = name;
        }

        public Builder<S> executor(SubCommandExecutor<S> executor) {
            this.executor = executor;
            return this;
        }

        public Builder<S> tabCompleter(SubCommandTabCompleter<S> tabCompleter) {
            this.tabCompleter = tabCompleter;
            return this;
        }

        public Builder<S> description(String description) {
            this.description = description;
            return this;
        }

        public Builder<S> args(String[] args) {
            this.args = args;
            return this;
        }

        public Builder<S> permission(String permission) {
            this.permission = permission;
            return this;
        }

        public SubCommand<S> build() {
            SubCommand<S> subCommand = new SubCommand<>(name, addon);
            subCommand.setExecutor(executor);
            subCommand.setTabCompleter(tabCompleter);
            subCommand.setDescription(description);
            subCommand.setArgs(args);
            subCommand.setPermission(permission);
            return subCommand;
        }
    }
}
