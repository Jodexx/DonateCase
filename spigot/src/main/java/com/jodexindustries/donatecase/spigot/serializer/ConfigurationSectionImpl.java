package com.jodexindustries.donatecase.spigot.serializer;

import io.leangen.geantyref.TypeToken;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigurationSectionImpl implements ConfigurationSection {

    private final ConfigurationNode node;

    public ConfigurationSectionImpl(ConfigurationNode node) {
        this.node = node;
    }

    public ConfigurationNode node(String path) {
        return node.node((Object[]) path.split("\\."));
    }

    @Override
    public @NotNull Set<String> getKeys(boolean deep) {
        return node.childrenMap().values().stream().map(ConfigurationNode::getString).collect(Collectors.toSet());
    }

    @Override
    public @NotNull Map<String, Object> getValues(boolean deep) {
        Map<Object, ? extends ConfigurationNode> map = node.childrenMap();

        return map
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                entry -> String.valueOf(entry.getKey()),
                                entry -> new ConfigurationSectionImpl(entry.getValue()),
                                (a, b) -> b
                        )
                );
    }

    @Override
    public boolean contains(@NotNull String path) {
        return !node(path).isNull();
    }

    @Override
    public boolean contains(@NotNull String path, boolean ignoreDefault) {
        return contains(path);
    }

    @Override
    public boolean isSet(@NotNull String path) {
        return node(path).isList();
    }

    @Override
    public @Nullable String getCurrentPath() {
        return node.path().toString();
    }

    @Override
    public @NotNull String getName() {
        return String.valueOf(node.key());
    }

    @Override
    public @Nullable Configuration getRoot() {
        return null;
    }

    @Override
    public @Nullable ConfigurationSection getParent() {
        if(node.parent() == null) return null;
        return new ConfigurationSectionImpl(node.parent());
    }

    @Override
    public @Nullable Object get(@NotNull String path) {
        return node(path).raw();
    }

    @Override
    public @Nullable Object get(@NotNull String path, @Nullable Object def) {
        return node(path).raw(def);
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {

    }

    @Override
    public @NotNull ConfigurationSection createSection(@NotNull String path) {
        return null;
    }

    @Override
    public @NotNull ConfigurationSection createSection(@NotNull String path, @NotNull Map<?, ?> map) {
        return null;
    }

    @Override
    public @Nullable String getString(@NotNull String path) {
        return node(path).getString();
    }

    @Override
    public @Nullable String getString(@NotNull String path, @Nullable String def) {
        String string = getString(path);
        return string == null ? def : string;
    }

    @Override
    public boolean isString(@NotNull String path) {
        return false;
    }

    @Override
    public int getInt(@NotNull String path) {
        return node(path).getInt();
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        return node(path).getInt(def);
    }

    @Override
    public boolean isInt(@NotNull String path) {
        return node(path).raw() instanceof Integer;
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return node(path).getBoolean();
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        return node(path).getBoolean(def);
    }

    @Override
    public boolean isBoolean(@NotNull String path) {
        return node(path).raw() instanceof Boolean;
    }

    @Override
    public double getDouble(@NotNull String path) {
        return node(path).getDouble();
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        return node(path).getDouble(def);
    }

    @Override
    public boolean isDouble(@NotNull String path) {
        return node(path).raw() instanceof Double;
    }

    @Override
    public long getLong(@NotNull String path) {
        return node(path).getLong();
    }

    @Override
    public long getLong(@NotNull String path, long def) {
        return node(path).getLong(def);
    }

    @Override
    public boolean isLong(@NotNull String path) {
        return node(path).raw() instanceof Long;
    }

    @Override
    public @Nullable List<?> getList(@NotNull String path) {
        try {
            return node(path).getList(Object.class);
        } catch (SerializationException e) {
            return null;
        }
    }

    @Override
    public @Nullable List<?> getList(@NotNull String path, @Nullable List<?> def) {
        List<?> list = getList(path);
        return list == null ? def : list;
    }

    @Override
    public boolean isList(@NotNull String path) {
        return false;
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) {
        try {
            List<String> list = node(path).getList(String.class);
            return list == null ? new ArrayList<>() : list;
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public @NotNull List<Integer> getIntegerList(@NotNull String path) {
        try {
            List<Integer> list = node(path).getList(Integer.class);
            return list == null ? new ArrayList<>() : list;
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public @NotNull List<Boolean> getBooleanList(@NotNull String path) {
        try {
            List<Boolean> list = node(path).getList(Boolean.class);
            return list == null ? new ArrayList<>() : list;
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public @NotNull List<Double> getDoubleList(@NotNull String path) {
        try {
            List<Double> list = node(path).getList(Double.class);
            return list == null ? new ArrayList<>() : list;
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public @NotNull List<Float> getFloatList(@NotNull String path) {
        try {
            List<Float> list = node(path).getList(Float.class);
            return list == null ? new ArrayList<>() : list;
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public @NotNull List<Long> getLongList(@NotNull String path) {
        try {
            List<Long> list = node(path).getList(Long.class);
            return list == null ? new ArrayList<>() : list;
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public @NotNull List<Byte> getByteList(@NotNull String path) {
        try {
            List<Byte> list = node(path).getList(Byte.class);
            return list == null ? new ArrayList<>() : list;
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public @NotNull List<Character> getCharacterList(@NotNull String path) {
        try {
            List<Character> list = node(path).getList(Character.class);
            return list == null ? new ArrayList<>() : list;
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public @NotNull List<Short> getShortList(@NotNull String path) {
        try {
            List<Short> list = node(path).getList(Short.class);
            return list == null ? new ArrayList<>() : list;
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public @NotNull List<Map<?, ?>> getMapList(@NotNull String path) {
        TypeToken<Map<?, ?>> typeToken = new TypeToken<Map<?, ?>>(){};

        try {
            List<Map<?, ?>> list = node.getList(typeToken);
            return list == null ? new ArrayList<>() : list;
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public @Nullable <T> T getObject(@NotNull String path, @NotNull Class<T> clazz) {
        try {
            return node(path).get(clazz);
        } catch (SerializationException e) {
            return null;
        }
    }

    @Override
    public @Nullable <T> T getObject(@NotNull String path, @NotNull Class<T> clazz, @Nullable T def) {
        try {
            return node(path).get(clazz, def);
        } catch (SerializationException e) {
            return null;
        }
    }

    @Override
    public @Nullable <T extends ConfigurationSerializable> T getSerializable(@NotNull String path, @NotNull Class<T> clazz) {
        return null;
    }

    @Override
    public @Nullable <T extends ConfigurationSerializable> T getSerializable(@NotNull String path, @NotNull Class<T> clazz, @Nullable T def) {
        return null;
    }

    @Override
    public @Nullable Vector getVector(@NotNull String path) {
        return null;
    }

    @Override
    public @Nullable Vector getVector(@NotNull String path, @Nullable Vector def) {
        return null;
    }

    @Override
    public boolean isVector(@NotNull String path) {
        return false;
    }

    @Override
    public @Nullable OfflinePlayer getOfflinePlayer(@NotNull String path) {
        return null;
    }

    @Override
    public @Nullable OfflinePlayer getOfflinePlayer(@NotNull String path, @Nullable OfflinePlayer def) {
        return null;
    }

    @Override
    public boolean isOfflinePlayer(@NotNull String path) {
        return false;
    }

    @Override
    public @Nullable ItemStack getItemStack(@NotNull String path) {
        return null;
    }

    @Override
    public @Nullable ItemStack getItemStack(@NotNull String path, @Nullable ItemStack def) {
        return null;
    }

    @Override
    public boolean isItemStack(@NotNull String path) {
        return false;
    }

    @Override
    public @Nullable Color getColor(@NotNull String path) {
        return null;
    }

    @Override
    public @Nullable Color getColor(@NotNull String path, @Nullable Color def) {
        return null;
    }

    @Override
    public boolean isColor(@NotNull String path) {
        return false;
    }

    @Override
    public @Nullable Location getLocation(@NotNull String path) {
        return null;
    }

    @Override
    public @Nullable Location getLocation(@NotNull String path, @Nullable Location def) {
        return null;
    }

    @Override
    public boolean isLocation(@NotNull String path) {
        return false;
    }

    @Override
    public @Nullable ConfigurationSection getConfigurationSection(@NotNull String path) {
        return null;
    }

    @Override
    public boolean isConfigurationSection(@NotNull String path) {
        return false;
    }

    @Override
    public @Nullable ConfigurationSection getDefaultSection() {
        return null;
    }

    @Override
    public void addDefault(@NotNull String path, @Nullable Object value) {

    }
}
