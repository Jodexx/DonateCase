package com.jodexindustries.donatecase.api.data;

/**
 * Class to define command type
 */

public enum SubCommandType {
    /**
     * User with player rights can use, and see this command in tab completer (donatecase.player)
     */
    PLAYER,
    /**
     * User with moder rights can use, and see this command in tab completer (donatecase.moder)
     */
    MODER,
    /**
     * User with admin rights can use, and see this command in tab completer (donatecase.admin)
     */
    ADMIN
}