package com.jodexindustries.donatecase.api.event.plugin;

import com.jodexindustries.donatecase.api.event.DCEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DonateCaseReloadEvent extends DCEvent {

    private final Type type;

    /**
     * Enum for reload type
     */
    public enum Type {
        /**
         * Config reloaded
         */
        CONFIG,
        /**
         * Cases reloaded
         */
        CASES
    }
}
