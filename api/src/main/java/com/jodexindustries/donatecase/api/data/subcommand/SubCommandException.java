package com.jodexindustries.donatecase.api.data.subcommand;

public class SubCommandException extends RuntimeException {

    public SubCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubCommandException(String message) {
        super(message);
    }
}
