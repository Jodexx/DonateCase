package com.jodexindustries.dceventmanager.data;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ConfigSerializable
public class EventPlaceholder {

    @Setting
    @Nullable
    private List<Placeholder> placeholders;

    @Getter
    @Setter
    @ConfigSerializable
    public static class Placeholder {

        @Setting
        private String name;

        @Setting
        private String replace;

        @Setting
        private String method;

        public Placeholder() {}

        public Placeholder(String name, String replace, String method) {
            this.name = name;
            this.replace = replace;
            this.method = method;
        }

        @Override
        public boolean equals(Object object) {
            if (object == null || getClass() != object.getClass()) return false;
            Placeholder that = (Placeholder) object;
            return Objects.equals(method, that.method);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(method);
        }
    }
}
