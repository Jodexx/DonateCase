package com.jodexindustries.donatecase.spigot.tools;

public class Pair<U, V> {
    public final U fst;
    public final V snd;

    private Pair(U first, V second) {
        this.fst = first;
        this.snd = second;
    }

    public static <U, V> Pair<U, V> of(U a, V b) {
        return new Pair<>(a, b);
    }
}