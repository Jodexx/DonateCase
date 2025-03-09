package com.jodexindustries.dceventmanager.data;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;

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
            EQUALS(0),

            /**
             * !=
             */
            NOT_EQUAL_TO(1),

            /**
             * <
             */
            LESS_THAN(2),

            /**
             * <=
             */
            LESS_THAN_OR_EQUAL_TO(3),

            /**
             * >
             */
            GREATER_THAN(4),

            /**
             * >=
             */
            GREATER_THAN_OR_EQUAL_TO(5);

            public final int operation;

            Type(int operation) {
                this.operation = operation;
            }

            public boolean compare(@NotNull Object a, @NotNull Object b) {
                if (a instanceof Number && b instanceof Number) {
                    double numA = ((Number) a).doubleValue();
                    double numB = ((Number) b).doubleValue();

                    switch (operation) {
                        case 0: return numA == numB;
                        case 1: return numA != numB;
                        case 2: return numA < numB;
                        case 3: return numA <= numB;
                        case 4: return numA > numB;
                        case 5: return numA >= numB;
                    }
                }

                switch (operation) {
                    case 0: return a.equals(b);
                    case 1: return !a.equals(b);
                }

                return false;
            }


        }
    }
}