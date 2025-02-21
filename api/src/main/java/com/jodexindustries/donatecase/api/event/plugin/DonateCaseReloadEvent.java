package com.jodexindustries.donatecase.api.event.plugin;

import com.jodexindustries.donatecase.api.event.DCEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
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
