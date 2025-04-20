package com.jodexindustries.donatecase.api.data.config;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = false)
@Getter
public class ConfigSerializer {

    private final Class<?> serializer;
    private final Object[] path;

    public ConfigSerializer(Class<?> serializer, Object... path) {
        this.serializer = serializer;
        this.path = path;
    }
}
