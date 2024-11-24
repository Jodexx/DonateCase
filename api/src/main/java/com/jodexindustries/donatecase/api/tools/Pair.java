package com.jodexindustries.donatecase.api.tools;

import org.jetbrains.annotations.NotNull;

public class Pair<A, B> {
    private final A first;
    private final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @NotNull
    public A getFirst() {
        return first;
    }

    @NotNull
    public B getSecond() {
        return second;
    }

}