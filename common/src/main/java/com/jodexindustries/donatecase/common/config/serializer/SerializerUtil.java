package com.jodexindustries.donatecase.common.config.serializer;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class SerializerUtil {

    public static List<Integer> intNode(ConfigurationNode node) throws SerializationException {
        return node.isList() ? intList(node.getList(String.class)) : intRange(node.getString());
    }

    public static List<Integer> intList(List<String> list) {
        if (list == null || list.isEmpty()) return new ArrayList<>();

        List<Integer> integers = new ArrayList<>();
        for (String s : list) {
            integers.addAll(intRange(s));
        }

        return integers;
    }

    public static List<Integer> intRange(String string) {
        if (string == null || string.isEmpty()) return new ArrayList<>();

        List<Integer> integers = new ArrayList<>();

        String[] args = string.split("-", 2);
        int start = parseInt(args[0]);
        int end = args.length >= 2 ? parseInt(args[1]) : start;

        addRange(integers, start, end);

        return integers;
    }

    private static void addRange(List<Integer> slots, int start, int end) {
        for (int i = start; i <= end; i++) {
            slots.add(i);
        }
    }

}
