package com.jodexindustries.donatecase.api.data.material;

public class CaseMaterialException extends RuntimeException {
    public CaseMaterialException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaseMaterialException(String message) {
        super(message);
    }
}
