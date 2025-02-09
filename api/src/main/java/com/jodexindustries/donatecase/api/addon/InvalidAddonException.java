package com.jodexindustries.donatecase.api.addon;

public class InvalidAddonException extends Exception {
    public InvalidAddonException(String message) {
        super(message);
    }

    public InvalidAddonException(String message, Throwable cause) {
        super(message, cause);
    }
}
