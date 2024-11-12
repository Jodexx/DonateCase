package com.jodexindustries.donatecase.api.addon.internal;

public class InvalidAddonException extends Exception {
    public InvalidAddonException(String message) {
        super(message);
    }

    public InvalidAddonException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAddonException(Throwable cause) {
        super(cause);
    }

    public InvalidAddonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
