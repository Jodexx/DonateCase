package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.hologram.HologramDriver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface HologramManager extends HologramDriver {

    void register(@NotNull String name, @NotNull HologramDriver driver);

    void unregister(@NotNull String name);

    default void unregister() {
        List<String> list = new ArrayList<>(get().keySet());
        list.forEach(this::unregister);
    }

    Map<String, HologramDriver> get();

    void set(@NotNull String name);

    void load();

}