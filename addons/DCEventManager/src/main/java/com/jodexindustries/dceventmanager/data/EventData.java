package com.jodexindustries.dceventmanager.data;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

@Getter
@ConfigSerializable
public class EventData {

    @Setting
    private List<Executor> executors;

    @Getter
    @ConfigSerializable
    public static class Executor {

        @Setting
        private String name;

        @Setting
        private List<Condition> conditions;

        @Setting
        private List<String> actions;
    }

    @Getter
    @ConfigSerializable
    public static class Condition {

        @Setting
        private Type type;

        @Setting
        private String placeholder;

        @Setting
        private ConfigurationNode input;

        public boolean compare(@Nullable Object output) {
            Object input = this.input.raw();
            if (input == null || output == null) return false;

            return type.compare(input, output);
        }

        public enum Type {
            /**
             * =
             */
            EQUALS,

            /**
             * !=
             */
            NOT_EQUAL_TO,

            /**
             * <
             */
            LESS_THAN,

            /**
             * <=
             */
            LESS_THAN_OR_EQUAL_TO,

            /**
             * >
             */
            GREATER_THAN,

            /**
             * >=
             */
            GREATER_THAN_OR_EQUAL_TO;

            private static final Map<Type, BiPredicate<Double, Double>> COMPARISON_MAP = new EnumMap<>(Type.class);

            static {
                COMPARISON_MAP.put(EQUALS, Double::equals);
                COMPARISON_MAP.put(NOT_EQUAL_TO, (a, b) -> !a.equals(b));
                COMPARISON_MAP.put(LESS_THAN, (a, b) -> a < b);
                COMPARISON_MAP.put(LESS_THAN_OR_EQUAL_TO, (a, b) -> a <= b);
                COMPARISON_MAP.put(GREATER_THAN, (a, b) -> a > b);
                COMPARISON_MAP.put(GREATER_THAN_OR_EQUAL_TO, (a, b) -> a >= b);
            }

            public boolean compare(@NotNull Object a, @NotNull Object b) {
                Double numA = parseNumber(a);
                Double numB = parseNumber(b);

                if (numA != null && numB != null) return COMPARISON_MAP.get(this).test(numA, numB);
                return this == EQUALS ? a.equals(b) : this == NOT_EQUAL_TO && !a.equals(b);
            }

            private static @Nullable Double parseNumber(@NotNull Object obj) {
                try {
                    return Double.parseDouble(obj.toString().trim());
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }

        }
    }
}