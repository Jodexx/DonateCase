package com.jodexindustries.donatecase.api.data;

import org.bukkit.command.CommandSender;

/**
 * Class to define command type
 */

public enum SubCommandType {
    /**
     * User with player rights can use, and see this command in tab completer (donatecase.player)
     */
    PLAYER("donatecase.player"),
    /**
     * User with moder rights can use, and see this command in tab completer (donatecase.mod)
     */
    MODER("donatecase.mod"),
    /**
     * User with admin rights can use, and see this command in tab completer (donatecase.admin)
     */
    ADMIN("donatecase.admin");

    private final String permission;

    SubCommandType(String permission) {
        this.permission = permission;
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(permission);
    }

}